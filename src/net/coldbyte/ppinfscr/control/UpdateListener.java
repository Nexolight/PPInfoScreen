package net.coldbyte.ppinfscr.control;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.interfaces.IfUpdateListener;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.PPTContainer;
import net.coldbyte.ppinfscr.settings.DefaultSettings;
import net.coldbyte.ppinfscr.ui.Output;
import net.coldbyte.ppinfscr.util.Helper;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public abstract class UpdateListener implements IfUpdateListener{
	
	private final IOHandler io = new IOHandler();
	private final UpdateListener inst = this;
	private Output out = new Output(this.getClass().getName());
	private boolean killtoggle = false;
	private Timer mysrvTimer;
	
	/**
	 * Use this class to keep track of updates in the program structure
	 */
	public UpdateListener(){
		this.mysrvTimer = new Timer();
		createUpdateService(this.mysrvTimer);
		out.cOut("Started thread");
	}
	
	/**
	 * This will return a service which will keep looking for updated
	 * files and or updated structure
	 * @param t
	 */
	private void createUpdateService(Timer t){
		TimerTask mysrv = new TimerTask(){
			private List<PPTContainer> sortedContainers = new ArrayList<PPTContainer>();
			private PPTContainer containerOfInterest;
			private PPTContainer latestContainer;
			private long now = new Date().getTime();
			private List<File> containerFiles = new ArrayList<File>();
			private File latestFile;
			private long latestFileModified; //File seems to be only a reference to the physical file so after it's gone every call returns null
			@Override
			public void run() {
				if(!inst.killtoggle){
					this.now = new Date().getTime();
					this.sortedContainers = io.getPPTContainers(true);
					Collections.sort(this.sortedContainers);
					
					
					if(this.sortedContainers.size() > 0){
						//Check current container
						
						
						
						//take the one which is the current one not the latest
						//so the relevant content is displayed...
						
						//containerOfInterest = this.sortedContainers.get(0);
						for(int i = 0; i < this.sortedContainers.size(); i++){
							
						}
						
						if(!this.sortedContainers.get(0).equals(this.latestContainer)){
							if(this.latestContainer == null){
								onContainerUpdated(null, this.sortedContainers.get(0).getContainer());
							}else{
								out.cOut(this.latestContainer.getContainer());
								out.cOut(this.sortedContainers.get(0).getContainer());
								onContainerUpdated(this.latestContainer.getContainer(), this.sortedContainers.get(0).getContainer());
							}
							this.latestContainer = this.sortedContainers.get(0);
						}
						
						//Check files inside the current container
						this.containerFiles = io.getPPTFiles(this.latestContainer.getContainer(), true);
						if(this.containerFiles.size() > 0){
							if(	Helper.pptDiff(this.containerFiles.get(0), this.latestFile) || 		//It should compare names
								this.containerFiles.get(0).lastModified() != latestFileModified){ 	//last modified would be 0 when the file doesn't exist anymore
								onFileUpdated(this.latestFile, this.containerFiles.get(0));
								this.latestFile = containerFiles.get(0);
								this.latestFileModified = containerFiles.get(0).lastModified();
							}
						}
					}else{
						onDoCloseAll();
					}
				}
			}
		};
		t.scheduleAtFixedRate(mysrv, 0, DefaultSettings.datedFoldersLookupDelay);
	}
	
	/**
	 * Use this to properly exit the listener thread
	 */
	protected void killThread(){
		this.killtoggle = true;
		this.mysrvTimer.cancel();
	}
}
