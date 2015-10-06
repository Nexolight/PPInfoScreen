package net.coldbyte.ppinfscr.control;

import java.io.File;
import java.util.concurrent.Callable;

import net.coldbyte.ppinfscr.control.UpdateListener.ContainerOfInterest;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.GUISettings;
import net.coldbyte.ppinfscr.settings.UserSettings;
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
	private IOHandler io;
	private Output out;
	private WindowManager wm;
	private UserSettings uS;
	private HealthListener health;
	private PPBot ppbot;
	private UpdateListener update;
	
	/**
	 * Use this constructor to start the program with the given params.
	 */
	public MainThread(String[] args){
		this.args = args;
		this.wm = new WindowManager();
		createConsole();
		this.out = Output.getInstance();
		this.out.setWM(this.wm);
	}
	
	/**
	 * Start the program you have to call this after the constructor
	 * Call prepare to create everything needed.
	 */
	public void start(){
		this.uS = new UserSettings();
		this.io = new IOHandler();
		if(prepare()){
			if(checkSettings()){
				createWelcome();
			}else{
				createSettings();
			}
		}
	}
	
	/**
	 * This will create the console view for further output
	 */
	private void createConsole(){
		this.wm.showConsole(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				out.cWarn("Reset application structure - This will restore the inital state");
				wm.closeWelcome();
				wm.closeSettings();
				stopServices();
				io.saveSettings(uS.getDefaultGUISettings());
				io.removeAll(new File(uS.ppinfscrRoot));
				io.removeAll(new File(UserSettings.ppinfscrDefaultStruct));
				start();
				return null;
			}
		}, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				wm.clearConsole();
				return null;
			}
		}, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				out.cInf("Reload application");
				wm.closeWelcome();
				wm.closeSettings();
				stopServices();
				wm.closeConsole();
				System.exit(0);
				return null;
			}
		});
	}
	
	/**
	 * Use this to make sure that all settings are valid (including paths)
	 * @return
	 */
	private boolean checkSettings(){
		GUISettings currentSettings = this.uS.getGUISettings();
		if(!io.checkExistence(currentSettings.getPpExeLocation())){
			out.cWarn("The PowerPoint executable is unavailable - please set the path manually");
			return false;
		}
		if(!new File(currentSettings.getPpExeLocation()).getName().matches(UserSettings.validPPEXENameRegex)){
			out.cWarn("The executable file does not seem to be the PowerPoint executable (Wrong filename)");
			return false;
		}
		if(!io.checkExistence(currentSettings.getApplicationRoot())){
			out.cErr("Cannot find the application root - make sure you have write access to it");
			return false;
		}
		return true;
	}
	
	/**
	 * This will open the welcome gui with a countdown for default startup
	 */
	private void createWelcome(){
		this.wm.showWelcome(5000, new Callable<Void>(){
			@Override
			public Void call() throws Exception{
				out.cInf("Startup interrupt - show settings");
				wm.closeWelcome();
				createSettings();
				return null;
			}
		}, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				out.cInf("No interrupt - start application");
				wm.closeWelcome();
				runServices();
				return null;
			}
		});
	}
	
	
	/**
	 * This will open the settings gui where the user can edit some basic information
	 * which is needed to run the prgram properly
	 */
	private void createSettings(){
		GUISettings currentSettings = this.uS.getGUISettings();
		this.wm.showSettings(currentSettings, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				out.cInf("Save settings");
				io.saveSettings(wm.getSettings());
				wm.closeSettings();
				stopServices();
				start();
				return null;
			}
		}, new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				out.cInf("Cancel - ignore setting changes");
				wm.closeSettings();
				stopServices();
				start();
				return null;
			}
		});
	}
	
	/**
	 * This will do all initial work to make sure the program will run properly
	 * @return
	 */
	private boolean prepare(){
		if (io.createRequired(this.uS.requiredDirs, this.uS.requiredFiles) &&
			io.extractTemplate(this.uS.ppinfscrSetFile_JAR, UserSettings.ppinfscrSetFile_OUT) &&
			io.extractTemplate(this.uS.ppinfscrOptTmpl_JAR, this.uS.ppinfscrOptTmpl_OUT)){
			out.cInf("Application structure is ok!");
			if(Helper.is64()){
				System.loadLibrary("jacob-1.18-x64");
			}else{
				System.loadLibrary("jacob-1.18-x86");
			}
			return true;
		}else{
			out.cErr("Could not create required files and folders. Please check the base base path of the application and make sure you have write access to them");
			return false;
		}
	}
	
	/**
	 * Start the service in the background which will open the presentations
	 * depending on the current structure
	 */
	private void runServices(){
		this.health = new HealthListener(){
			@Override
			public void onStructureChange() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStructureRepair() {
				// TODO Auto-generated method stub
			}
		};
		
		this.ppbot = new PPBot(){
			@Override
			public void stateChanged(PPBotState oldstate, PPBotState newstate) {
				out.cInf("PowerPoint bot changed state from: " + oldstate.name() + " to: " + newstate.name());
			}
		};
		
		this.update = new UpdateListener(ContainerOfInterest.INTIME){
			@Override
			public void onContainerUpdated(File old, File updated) {
				String oldstr = "null", newstr = "null";
				if(old != null){
					oldstr = old.getName();
				}
				if(updated != null){
					newstr = updated.getName();
				}
				out.cInf("Latest container ("+oldstr+") was updated new one is: " + newstr);
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
				out.cInf("Latest file ("+oldstr+") was updated new one is: " + newstr);
				ppbot.openNew(updated);
			}

			@Override
			public void onDoCloseAll() {
				out.cWarn("No files or directories found - close everything");
			}
		};
	}
	
	/**
	 * This will kill all running services
	 */
	private void stopServices(){
		if(this.health != null){
			this.health.killThread();
		}
		if(this.ppbot != null){
			this.ppbot.killThread();
		}
		if(this.update != null){
			this.update.killThread();
		}
	}
}
