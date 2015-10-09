package net.coldbyte.ppinfscr.control;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import net.coldbyte.ppinfscr.control.UpdateListener.ContainerOfInterest;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.GUISettings;
import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;
import net.coldbyte.ppinfscr.ui.WindowManager;

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
			createWelcome();
		}else{
			createSettings();
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
				String approot = uS.getString(Settings.APPLICATION_ROOT);
				if(!approot.isEmpty()){
					io.removeAll(new File(approot));
				}
				io.removeAll(new File(UserSettings.ppinfscrDatadir));
				io.saveSettings(uS.getDefaultGUISettings());
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
				GUISettings changedSettings = wm.getSettings();
				changedSettings.setApplicationRoot(changedSettings.getApplicationRoot());
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
	 * This will do all initial work to make sure the program will run properly it returns false if something went wrong
	 * @return
	 */
	private boolean prepare(){
		GUISettings currentSettings = this.uS.getGUISettings();
		
		if(!io.createRequired(UserSettings.requiredStatic , null)){
			return false;
		}
		boolean status = true;
		String appRoot = currentSettings.getApplicationRoot();
		if(!io.checkExistence(appRoot)){
			out.cWarn("Cannot find the application root - please choose an existing folder");
			status = false;
		}else{
			List<File> appRootContent = io.getAll(appRoot);
			for(File f : appRootContent){
				if(!f.getName().matches(UserSettings.datedFoldersRegex)){
					out.cWarn("The selected application root contains files which are not part of PPInfoScreen - "
							+ "Please create and/or select an empty folder");
					break;
				}
			}
		}
		if(!io.checkExistence(currentSettings.getPpExeLocation())){
			out.cWarn("The PowerPoint or Simpress executable is unavailable - please set the path manually");
			status = false;
		}else{
			if(!new File(currentSettings.getPpExeLocation()).getName().matches(UserSettings.validPPEXENameRegex)){
				out.cErr("The executable file does not seem to be the PowerPoint or Simpress executable (Wrong filename)");
				status = false;
			}
			if(new File(currentSettings.getPpExeLocation()).getName().matches(UserSettings.isOpenOfficePRegex)){
				out.cWarn("You selected the OpenOffice Simpress to show the presentations - please not that this program was designed to use Microsoft PowerPoint. It might not work with Simpress");
			}
		}
		if(!status){
			return status;
		}
		
		if(!io.createRequired(this.uS.requiredDynamic , null)){
			return false;
		}
		
		if (io.extractTemplate(UserSettings.ppinfscrOptTmpl_JAR, this.uS.ppinfscrOptTmpl_OUT)){
			out.cInf("Application structure is ok!");
		}else{
			out.cErr("Could not create required files. Please check the base base path of the application and make sure you have write access to them");
			status = false;
		}
		return status;
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
