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
	}
	
	/**
	 * This will return a service which will keep looking for updated
	 * files and or updated structure
	 * @param t
	 */
	private void createUpdateService(Timer t){
		TimerTask mysrv = new TimerTask(){
			private List<PPTContainer> sortedContainers = new ArrayList<PPTContainer>();
			private PPTContainer latestContainer;
			private List<File> containerFiles = new ArrayList<File>();
			private File latestFile;
			@Override
			public void run() {
				if(!inst.killtoggle){
					sortedContainers = io.getPPTContainers(true);
					Collections.sort(sortedContainers);
					
					if(sortedContainers.size() > 0){
						
						if(sortedContainers.get(0) != this.latestContainer){
							onContainerUpdated(this.latestContainer.getContainer(), sortedContainers.get(0).getContainer());
							this.latestContainer = sortedContainers.get(0);
						}
						
						
						this.containerFiles = io.getPPTFiles(this.latestContainer.getContainer(), true);
						if(this.containerFiles.size() > 0){
							if(this.containerFiles.get(0) != this.latestFile){ //Only one the first file in a container will be taken
								onFileUpdated(this.latestFile, this.containerFiles.get(0));
								this.latestFile = containerFiles.get(0);
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
