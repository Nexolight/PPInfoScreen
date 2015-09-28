package net.coldbyte.ppinfscr.main;

import net.coldbyte.ppinfscr.control.MainThread;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class Main {
	
	/**
	 * Init program
	 * @param args
	 */
	public static void main(String[] args) {
		MainThread mt = new MainThread(args);
		mt.start();
	}
	
}
