package net.coldbyte.ppinfscr.interfaces;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfKillable {
	/**
	 * This will kill all running threads inside this reference
	 */
	public void killThread();
}
