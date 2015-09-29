package net.coldbyte.ppinfscr.interfaces;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfPPBot {
	/**
	 * This will be called when the process is started up the first time
	 */
	public void readyToLoad();
	
	/**
	 * This will be called if the process is ready to reload a new file
	 */
	public void readyToReaload();
	
	/**
	 * This will be called if the process is ready to close the opened file
	 */
	public void readyToClose();
}
