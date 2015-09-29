package net.coldbyte.ppinfscr.interfaces;

import java.io.File;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfUpdateListener {
	
	/**
	 * This will be called when the container was updated
	 */
	public void onContainerUpdated(File old, File updated);
	
	/**
	 * This will be called if the listener detected a file change depending on the last edited date
	 * It will be also called on initialization when it finds any file
	 * @param fullpath
	 */
	public void onFileUpdated(File old, File updated);
	
	/**
	 * This will be called when the controller should close all opened files
	 * @param fullpath
	 */
	public void onDoCloseAll();
	
}
