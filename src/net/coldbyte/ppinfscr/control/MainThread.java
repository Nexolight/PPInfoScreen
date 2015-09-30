package net.coldbyte.ppinfscr.control;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.control.UpdateListener.ContainerOfInterest;
import net.coldbyte.ppinfscr.interfaces.IfHealthListener;
import net.coldbyte.ppinfscr.interfaces.IfUpdateListener;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.settings.DefaultSettings;
import net.coldbyte.ppinfscr.ui.Output;

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
	private TimerTask myDspSrv;
	
	/**
	 * Use this constructor to start the program with the given params.
	 */
	public MainThread(String[] args){
		this.args = args;
	}
	
	/**
	 * Start the program you have to call this after the constructor
	 */
	public void start(){
		if(prepare()){
			runServices();
		}else{
			out.cOut("Not implemented");
		}
	}
	
	/**
	 * This will do all initial work to make sure the program will run properly
	 * @return
	 */
	private boolean prepare(){
		if (io.createRequired(DefaultSettings.requiredDirs, DefaultSettings.requiredFiles) &&
			io.extractTemplate(DefaultSettings.ppinfscrOptSetFile, DefaultSettings.ppinfscrOptSetFile) &&
			io.extractTemplate(DefaultSettings.ppinfscrOptTmpl, DefaultSettings.ppinfscrOptTmpl) &&
			io.extractTemplate(DefaultSettings.ppinfscrSetFile, DefaultSettings.ppinfscrSetFile)){
			out.cOut("Successfully created all required files");
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
