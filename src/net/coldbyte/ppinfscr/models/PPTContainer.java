package net.coldbyte.ppinfscr.models;

import java.io.File;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class PPTContainer {
	
	private File myPPT;
	private File me;
	
	/**
	 * This represents a powerpoint file container including it's file
	 */
	public PPTContainer(File myPPT, File me){
		this.myPPT = myPPT;
		this.me = me;
	}

	public File getMyPPT() {
		return myPPT;
	}

	public void setMyPPT(File myPPT) {
		this.myPPT = myPPT;
	}

	public File getMe() {
		return me;
	}

	public void setMe(File me) {
		this.me = me;
	}
}
