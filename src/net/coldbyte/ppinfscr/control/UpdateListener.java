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
	public enum ContainerOfInterest{
		LATEST, INTIME, SPECIFIED
	}
	private final ContainerOfInterest coi;
	
	/**
	 * Use this class to keep track of updates in the program structure
	 */
	public UpdateListener(ContainerOfInterest coi){
		this.coi = coi;
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
				
			private PPTContainer usedContainer;
			private File usedFile;
			private long usedFileModified;
			private PPTContainer reqContainer;
			private File reqFile;

			@Override
			public void run() {
				if(!inst.killtoggle){
					updateContainersAndFiles();
					if(this.sortedContainers.size() > 0){
						
						if(isDifferentContainer()){
							switchContainer();
						}
						
						if(isDifferentFile()){
							switchFile();
						}
						
					}else{
						onDoCloseAll();
					}
				}
			}
			
			/**
			 * This will update and resort the container as well as the currently requested container and files
			 */
			private void updateContainersAndFiles(){
				this.sortedContainers = io.getPPTContainers(true);
				Collections.sort(this.sortedContainers);
				this.reqContainer = getContainerOfInterest(inst.coi);
				this.reqFile = getFile(this.reqContainer);
			}
			
			/**
			 * This will return the container of interest given in the super class it's constructor
			 * @param coi
			 * @return
			 */
			private PPTContainer getContainerOfInterest(ContainerOfInterest coi){
				if (coi == ContainerOfInterest.INTIME){
					PPTContainer finalOne = null;
					long now = new Date().getTime();
					for(int i = 0; i < this.sortedContainers.size(); i++){
						if(now >= this.sortedContainers.get(i).getDate().getTime()){
							finalOne = this.sortedContainers.get(i); //this works because the list is sorted
							break;
						}
					}
					return finalOne;
					
				}else if (coi == ContainerOfInterest.SPECIFIED){
					
					//Implement optional user settings
					
				}else{ 	//ContainerOfInterest.LATEST
					if(this.sortedContainers.size() > 0){
						return this.sortedContainers.get(0);
					}
				}
				return null; //no container found
			}
			
			/**
			 * This will return the first file inside a PPTContainer
			 * @param container
			 * @return
			 */
			private File getFile(PPTContainer container){
				if(container == null){
					return null;
				}
				List<File> files = io.getPPTFiles(container.getContainer(), true);
				if (files.size() > 0){
					return files.get(0);
				}else{
					return null;
				}
			}
			
			/**
			 * Checks if reqContainer and usedContainer are different
			 * @return
			 */
			private boolean isDifferentContainer(){
				if(this.usedContainer == null && this.reqContainer != null){
					return true;
				}
				if(this.usedContainer != null && this.reqContainer == null){
					return true;
				}
				if(this.usedContainer == null && this.reqContainer == null){
					return false;
				}
				if(!this.usedContainer.equals(this.reqContainer)){
					return true;
				}
				return false;
			}
			
			/**
			 * Checks if reqFile and usedFile are different
			 * @return
			 */
			private boolean isDifferentFile(){
				if(this.usedFile == null && this.reqFile != null){
					return true;
				}
				if(this.usedFile != null && this.reqFile == null){
					return true;
				}
				if(this.usedFile == null && this.reqFile == null){
					return false;
				}
				if(!this.usedFile.getName().equals(this.reqFile.getName())){
					return true;
				}
				if(this.usedFileModified != this.reqFile.lastModified()){
					return true;
				}
				return false;
			}
			
			/**
			 * Changes the local variable usedContainer to reqContainer and calls the callback
			 */
			private void switchContainer(){
				File oldC = null, newC = null;
				if(this.usedContainer != null){
					oldC = this.usedContainer.getContainer();
				}
				if(this.reqContainer != null){
					newC = this.reqContainer.getContainer();
				}
				onContainerUpdated(oldC, newC);
				this.usedContainer = this.reqContainer;
			}
			
			/**
			 * Changes the local variable usedFile together with it's modified date to reqFile and calls the callback
			 */
			private void switchFile(){
				File oldF = null, newF = null;
				if(this.usedFile != null){
					oldF = this.usedFile;
				}
				if(this.reqFile != null){
					newF = this.reqFile;
				}
				onFileUpdated(oldF, newF);
				this.usedFile = this.reqFile;
				if(this.reqFile != null){
					this.usedFileModified = this.reqFile.lastModified(); //workaround it would be missing when removed and it's used to check for file updates
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
