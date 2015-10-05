package net.coldbyte.ppinfscr.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import net.coldbyte.ppinfscr.control.UpdateListener.ContainerOfInterest;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.GUISettings;
import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;
import net.coldbyte.ppinfscr.ui.Output;
import net.coldbyte.ppinfscr.ui.WindowManager;
import net.coldbyte.ppinfscr.util.Helper;

/**
 * 
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class MainThread{
	
	private String[] args;
	private IOHandler io = new IOHandler();
	private Output out = new Output(this.getClass().getName());
	private WindowManager wm = new WindowManager();
	private TimerTask myDspSrv;
	
	/**
	 * Use this constructor to start the program with the given params.
	 */
	public MainThread(String[] args){
		this.args = args;
	}
	
	/**
	 * Start the program you have to call this after the constructor
	 * Call prepare to create everything needed and then start the gui
	 * including the call to start the program when the settings were not
	 * opened
	 */
	public void start(){
		if(prepare()){
			wm.showWelcome(5000, new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent arg0) {
					wm.closeWelcome();
					GUISettings currentSettings = new GUISettings();
					currentSettings.setApplicationRoot(UserSettings.getString(Settings.APPLICATION_ROOT));
					currentSettings.setPpExeLocation(UserSettings.getString(Settings.PP_EXE_LOCATION));
					currentSettings.setFolderLookupDelay(UserSettings.getLong(Settings.FOLDER_LOOKUP_DELAY));
					currentSettings.setPpStateLookupDelay(UserSettings.getLong(Settings.PP_STATE_LOOKUP_DELAY));
					currentSettings.setPpNextSheetDelay(UserSettings.getLong(Settings.PP_NEXT_SHEET_DELAY));
					wm.showSettings(currentSettings, new Callable<Void>(){
						@Override
						public Void call() throws Exception {
							//io.saveSettings(currentSettings);
							wm.closeSettings();
							start();
							return null;
						}
					}, new Callable<Void>(){
						@Override
						public Void call() throws Exception {
							wm.closeSettings();
							start();
							return null;
						}
					});
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
				
			}, new Callable<Void>(){
				@Override
				public Void call() throws Exception {
					wm.closeWelcome();
					runServices();
					return null;
				}
			});
		}else{
			out.cOut("Not implemented");
		}
	}
	
	/**
	 * This will do all initial work to make sure the program will run properly
	 * @return
	 */
	private boolean prepare(){
		if (io.createRequired(UserSettings.requiredDirs, UserSettings.requiredFiles) &&
			io.extractTemplate(UserSettings.ppinfscrSetFile_JAR, UserSettings.ppinfscrSetFile_OUT) &&
			io.extractTemplate(UserSettings.ppinfscrOptSetFile_JAR, UserSettings.ppinfscrOptSetFile_OUT) &&
			io.extractTemplate(UserSettings.ppinfscrOptTmpl_JAR, UserSettings.ppinfscrOptTmpl_OUT)){
			out.cOut("Successfully created all required files");
			if(Helper.is64()){
				System.loadLibrary("jacob-1.18-x64");
			}else{
				System.loadLibrary("jacob-1.18-x86");
			}
			return true;
		}else{
			out.cOut("Could not create required files and folders. Please check the base base path of the application and make sure you have write access to them");
			return false;
		}
	}
	
	/**
	 * Start the service in the background which will open the presentations
	 * depending on the current structure
	 */
	private void runServices(){
		
		final HealthListener health = new HealthListener(){

			@Override
			public void onStructureChange() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStructureRepair() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		final PPBot ppbot = new PPBot(){

			@Override
			public void stateChanged(PPBotState oldstate, PPBotState newstate) {
				out.cOut("PPBot changed state from: " + oldstate.name() + " to: " + newstate.name());
				
			}

		};
		
		final UpdateListener update = new UpdateListener(ContainerOfInterest.INTIME){

			@Override
			public void onContainerUpdated(File old, File updated) {
				String oldstr = "null", newstr = "null";
				if(old != null){
					oldstr = old.getName();
				}
				if(updated != null){
					newstr = updated.getName();
				}
				out.cOut("Latest container ("+oldstr+") was updated new one is: " + newstr);
			}

			@Override
			public void onFileUpdated(File old, File updated) {
				String oldstr = "null", newstr = "null";
				if(old != null){
					oldstr = old.getName();
				}
				if(updated != null){
					newstr = updated.getName();
				}
				out.cOut("Latest file ("+oldstr+") was updated new one is: " + newstr);
				ppbot.openNew(updated);
			}

			@Override
			public void onDoCloseAll() {
				out.cOut("No files or directories found - close everything");
			}
			
		};
		
		
		
	}
}
