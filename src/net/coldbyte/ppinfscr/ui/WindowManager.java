package net.coldbyte.ppinfscr.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import net.coldbyte.ppinfscr.control.Output;
import net.coldbyte.ppinfscr.models.GUISettings;
import net.miginfocom.swing.MigLayout;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class WindowManager {
	private Output out;
	private JFrame welcomeWindow = null;
	private boolean welcomeWindowInterrupted = false;
	private JFrame settingsWindow = null;
	private GUISettings currentSettings = null;
	private JFrame consoleWindow = null;
	private JTextPane consoleContent = null;
	
	
	/**
	 * Use this to display parts of the user interface
	 */
	public WindowManager(){
		this.out = Output.getInstance();
	}
	
	
	/**
	 * Show the console with additional buttons
	 * @param onResetApp
	 * @param onClearLog
	 * @param onCloseApp
	 */
	public void showConsole(Callable<Void> onResetApp, Callable<Void> onClearLog, Callable onCloseApp){
		int w = 720;
		int h = 480;
		
		MigLayout mlw = new MigLayout("fill, nogrid");
		this.consoleWindow = new JFrame("PPInfoScreen - Console");
		this.consoleWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.consoleWindow.setLayout(mlw);
		this.consoleWindow.setBounds(400, 0, w, h);
		this.consoleWindow.setResizable(false);
		this.consoleWindow.setPreferredSize(new Dimension(w,h));
		this.consoleWindow.setMinimumSize(new Dimension(w,h));
		
		MigLayout mlp1 = new MigLayout("fill, wrap 9, inset 10 10 10 10, gap 0px 10px");
		JPanel p1 = new JPanel();
		p1.setLayout(mlp1);
		this.consoleWindow.setContentPane(p1);
		
		this.consoleContent = new JTextPane();
		this.consoleContent.setContentType("text/html");
		p1.add(consoleContent, "spanx 9, spany 8, growx, growy, pushy, wrap");
		
		JButton resetApp = new JButton("Reset application");
		p1.add(resetApp, "height 30, spany 1, spanx 2, align left");
		resetApp.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					onResetApp.call();
				} catch (Exception e) {
					out.cWarn("Cannot call callable - Exception", e);
					e.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
		});
		
		JButton clearLog = new JButton("Clear log");
		p1.add(clearLog, "height 30, spany 1, spanx 2, align center");
		clearLog.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					onClearLog.call();
				} catch (Exception e) {
					out.cWarn("Cannot call callable - Exception", e);
					e.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
		});
		
		JButton closeApp = new JButton("Close application");
		p1.add(closeApp, "height 30, spany 1, spanx 2, align right");
		closeApp.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					onCloseApp.call();
				} catch (Exception e) {
					out.cWarn("Cannot call callable - Exception", e);
					e.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {	
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {	
			}
		});
		
		this.consoleWindow.setVisible(true);
		p1.setVisible(true);
	}
	
	/**
	 *  This will append the given input as red text into the visual console
	 */
	public void appendToConsole(String htmlString){
        HTMLDocument cmdashtml = (HTMLDocument)this.consoleContent.getStyledDocument();
        Element last = cmdashtml.getParagraphElement(cmdashtml.getLength());
        try {
        	cmdashtml.insertBeforeEnd(last, htmlString);
        } catch (BadLocationException ex) {
        	out.silentOut("Unable to write to the visual console - BadLocationException");
        } catch (IOException ex) {
        	out.silentOut("Unable to write to the visual console - IOException");
        }
	}
	
	/**
	 * This will clear the console
	 */
	public void clearConsole(){
		this.consoleContent.setText("");
	}
	
	/**
	 * Closes the console window without any action
	 */
	public void closeConsole(){
		if(this.consoleWindow != null){
			this.consoleWindow.dispose();
		}
	}
	
	/**
	 * Show the welcome screen 
	 * @param interruptDelay the time the user can choose if he want to edit settings or just start as it is
	 * @param onSettings
	 * @param onNonInterrput
	 */
	public void showWelcome(long interruptDelay, Callable<Void> onSettings, Callable<Void> onNonInterrput){
		int w = 300;
		int h = 200;
		this.welcomeWindowInterrupted = false;
		
		MigLayout mlw = new MigLayout("fill, nogrid");
		this.welcomeWindow = new JFrame("PPInfoScreen - Welcome");
		this.welcomeWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.welcomeWindow.setLayout(mlw);
		this.welcomeWindow.setBounds(0, 0, w, h);
		this.welcomeWindow.setResizable(false);
		this.welcomeWindow.setPreferredSize(new Dimension(w,h));
		this.welcomeWindow.setMinimumSize(new Dimension(w,h));
		
		MigLayout mlp1 = new MigLayout("fill, wrap 1, insets 0");
		JPanel p1 = new JPanel();
		p1.setLayout(mlp1);
		this.welcomeWindow.setContentPane(p1);
		
		JLabel lbl1 = new JLabel("PPInfoScreen - Welcome");
		JLabel lbl2 = new JLabel("Wait for startup or Edit the settings...");
		p1.add(lbl1, "align center");
		p1.add(lbl2, "align center");
		
		final JButton btnSettings = new JButton("Settings");
		p1.add(btnSettings, "align center");
		
		this.welcomeWindow.setVisible(true);
		p1.setVisible(true);
		
		TimerTask countdown = new TimerTask(){
			private long iD = interruptDelay;
			private Callable<Void> nonInterrupt = onNonInterrput;
			@Override
			public void run() {
				while(iD >= 0){
					try {
						btnSettings.setText("Settings" + " (" + iD / 1000 + "s)");
						Thread.sleep(1000);
						iD -= 1000;
					} catch (InterruptedException e) {
						out.cWarn("Cannot delay thread - InterruptedException",e);
						e.printStackTrace();
					}
				}
				if(!welcomeWindowInterrupted){
					try {
						nonInterrupt.call();
					} catch (Exception e) {
						out.cWarn("Cannot call callable - Exception",e);
						e.printStackTrace();
					}
				}
			}
			
		};
		Timer t = new Timer();
		t.schedule(countdown, 0);
		btnSettings.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				try {
					onSettings.call();
				} catch (Exception e1) {
					out.cWarn("Cannot call callable - Exception", e1);
					e1.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
			}
		});
	}
	
	/**
	 * Closes the welcome window without any action
	 */
	public void closeWelcome(){
		if(this.welcomeWindow != null){
			this.welcomeWindowInterrupted = true;
			this.welcomeWindow.dispose();
		}
	}
	
	/**
	 * Open the settings window
	 */
	public void showSettings(GUISettings settings, Callable<Void> onSaveAndProceed, Callable<Void> onCancel){
		int w = 550;
		int h = 450;
		this.currentSettings = settings;
		
		MigLayout mlw = new MigLayout("fill, nogrid");
		this.settingsWindow = new JFrame("PPInfoScreen - Settings");
		this.settingsWindow.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.settingsWindow.setLayout(mlw);
		this.settingsWindow.setBounds(0, 0, w, h);
		this.settingsWindow.setResizable(false);
		MigLayout mlp1 = new MigLayout("fillx, wrap 10, inset 20 20 20 20");
		JPanel p1 = new JPanel();
		p1.setLayout(mlp1);
		this.settingsWindow.setContentPane(p1);
		
		//Application root directory selection
		JLabel lblApplicationRoot = new JLabel("Select the location which you want to use for the Presentations - A share is recommended");
		JTextField txtbApplicationRoot = new JTextField(currentSettings.getApplicationRoot());
		JButton btnApplicatonRoot = new JButton("Change...");
		btnApplicatonRoot.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				JFileChooser fcApplicationRoot = new JFileChooser();
				fcApplicationRoot.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int fcApplicationRootStatus = fcApplicationRoot.showOpenDialog(settingsWindow);
				if(fcApplicationRootStatus == JFileChooser.APPROVE_OPTION){
					txtbApplicationRoot.setText(fcApplicationRoot.getSelectedFile().getAbsolutePath().replace("\\", "/"));
					currentSettings.setApplicationRoot(fcApplicationRoot.getSelectedFile().getAbsolutePath());
				}
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent arg0) {
			}
			
		});
		p1.add(lblApplicationRoot,"gaptop 10, spanx 10");
		p1.add(txtbApplicationRoot, "newline, height 30, spanx 8, growx, push");
		p1.add(btnApplicatonRoot, "spanx 2, height 30, wrap, growx");
		
		
		//Powerpoint exe location selection
		JLabel lblPPExe = new JLabel("Select the POWERPNT.EXE file - (PowerPoint executable)");
		JTextField txtbPPExe = new JTextField(currentSettings.getPpExeLocation());
		JButton btnPPExe = new JButton("Change...");
		btnPPExe.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				JFileChooser fcPPExe = new JFileChooser();
				fcPPExe.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fcPPExe.setFileFilter(new FileNameExtensionFilter("POWERPNT.EXE", "exe"));
				int fcPPExeStatus = fcPPExe.showOpenDialog(settingsWindow);
				if(fcPPExeStatus == JFileChooser.APPROVE_OPTION){
					txtbPPExe.setText(fcPPExe.getSelectedFile().getAbsolutePath().replace("\\", "/"));
					currentSettings.setPpExeLocation(fcPPExe.getSelectedFile().getAbsolutePath());
				}
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
			}
		});
		p1.add(lblPPExe, "gaptop 10, spanx 10");
		p1.add(txtbPPExe, "newline, height 30, spanx 8, growx, push");
		p1.add(btnPPExe, "spanx 2, height 30, wrap, growx");
		
		
		//Folder lookup delay
		JLabel lblFLUD = new JLabel("Set the lookup interval for new files");
		JTextField txtbFLUD = new JTextField(String.valueOf(currentSettings.getFolderLookupDelay() / 1000));
		JLabel lblFLUDFormat = new JLabel("seconds");
		p1.add(lblFLUD, "spanx 6, gaptop 10");
		p1.add(txtbFLUD, "gaptop 10, width 75, height 30, spanx 2, align right");
		p1.add(lblFLUDFormat, "gaptop 10, spanx 2, wrap");
		
		//PowerPoint state lookup delay
		JLabel lblPPLUD = new JLabel("Set Powerpoint status check intervall");
		JTextField txtbPPLUD = new JTextField(String.valueOf(currentSettings.getPpStateLookupDelay() / 1000));
		JLabel lblPPLUDFormat = new JLabel("seconds");
		p1.add(lblPPLUD, "spanx 6, gaptop 5");
		p1.add(txtbPPLUD, "gaptop 5, width 75, height 30, spanx 2, align right");
		p1.add(lblPPLUDFormat, "gaptop 5, spanx 2, wrap");
		
		//PowerPoint next sheet every n seconds
		JLabel lblPPNSD = new JLabel("Set the 'next sheet' delay for PowerPoint");
		JTextField txtbPPNSD = new JTextField(String.valueOf(currentSettings.getPpNextSheetDelay() / 1000));
		JLabel lblPPNSDFormat = new JLabel("seconds");
		p1.add(lblPPNSD, "spanx 6, gaptop 5");
		p1.add(txtbPPNSD, "gaptop 5, width 75, height 30, spanx 2, align right");
		p1.add(lblPPNSDFormat, "gaptop 5, spanx 2, wrap");

		//Action buttons
		JButton saveProceed = new JButton("Save & Proceed");
		saveProceed.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				
				currentSettings.setFolderLookupDelay(Long.valueOf(txtbFLUD.getText()) * 1000);
				currentSettings.setPpStateLookupDelay(Long.valueOf(txtbPPLUD.getText()) * 1000);
				currentSettings.setPpNextSheetDelay(Long.valueOf(txtbPPNSD.getText()) * 1000);
				
				
				try {
					onSaveAndProceed.call();
				} catch (Exception e) {
					out.cWarn("Cannot call callable - Exception",e);
					e.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent arg0) {
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(java.awt.event.MouseEvent arg0) {
				try {
					onCancel.call();
				} catch (Exception e) {
					out.cWarn("Cannot call callable - Exception", e);
					e.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent arg0) {
			}
		});
		p1.add(cancel, "align left, gaptop 30, spanx 2, height 30");
		p1.add(saveProceed, "skip 6 , align right, gaptop 30, spanx 2, height 30");
		
		this.settingsWindow.setVisible(true);
		p1.setVisible(true);
	}
	
	/**
	 * Closes the settings window without any action
	 */
	public void closeSettings(){
		if(this.settingsWindow != null){
			this.settingsWindow.dispose();
		}
	}
	
	/**
	 * This will return the current settings made in the gui
	 * @return
	 */
	public GUISettings getSettings(){
		return this.currentSettings;
	}
	
}
