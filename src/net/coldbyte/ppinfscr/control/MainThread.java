package net.coldbyte.ppinfscr.control;

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
public class MainThread {
	
	private String[] args;
	private IOHandler io = new IOHandler();
	private Output out = new Output(this.getClass().getName());
	
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
		if (io.createRequired(DefaultSettings.requiredDirs, DefaultSettings.requiredFiles)){
			out.cOut("End of dev");
		}else{
			out.cOut("Could not create required files and folders. Please check the base base path of the application and make sure you have write access to them");
		}
	}
	
	
	
	
}
