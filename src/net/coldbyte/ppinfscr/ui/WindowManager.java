package net.coldbyte.ppinfscr.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.glass.events.MouseEvent;

import net.coldbyte.ppinfscr.models.GUISettings;
import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class WindowManager {
	private Output out = new Output(this.getClass().getName());
	private JFrame welcomeWindow = null;
	private boolean welcomeWindowInterrupted = false;
	private JFrame settingsWindow = null;
	private GUISettings currentSettings = null;
	
	
	/**
	 * Use this to display parts of the user interface
	 */
	public WindowManager(){

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
		this.welcomeWindow = new JFrame("Welcome");
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
						out.cOut("Cannot delay thread - InterruptedException");
						e.printStackTrace();
					}
				}
				if(!welcomeWindowInterrupted){
					try {
						nonInterrupt.call();
					} catch (Exception e) {
						out.cOut("Cannot call callable - Exception");
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
					out.cOut("Cannot call callable - Exception");
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
		this.settingsWindow = new JFrame("Settings");
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
					out.cOut("Cannot call callable - Exception");
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
					out.cOut("Cannot call callable - Exception");
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
	 * Thiw will return the current settings made in the gui
	 * @return
	 */
	public GUISettings getSettings(){
		return this.currentSettings;
	}
	
}
