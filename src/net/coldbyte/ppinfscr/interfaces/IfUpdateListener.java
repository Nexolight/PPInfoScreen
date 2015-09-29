package net.coldbyte.ppinfscr.interfaces;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfUpdateListener {
	public void onFileUpdated(String fullpath);
	public void onFileRemoved(String fullpath);
	public void onDoCloseAll(String fullpath);
	public void onDoDisplayNew(String fullpath);
}
