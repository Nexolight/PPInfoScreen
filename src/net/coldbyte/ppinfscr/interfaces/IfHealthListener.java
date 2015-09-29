package net.coldbyte.ppinfscr.interfaces;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfHealthListener {
	/**
	 * This will be called if something on the program structure has changed
	 */
	public void onStructureChange();
	
	/**
	 * This will be called if the HealtListener restores the healthy structure of the
	 * program because of any structure violation
	 */
	public void onStructureRepair();
}
