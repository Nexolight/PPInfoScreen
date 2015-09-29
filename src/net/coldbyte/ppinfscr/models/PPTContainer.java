package net.coldbyte.ppinfscr.models;

import java.io.File;
import java.util.Date;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class PPTContainer implements Comparable<PPTContainer>{
	
	private File container;
	private Date date;
	
	/**
	 * This represents a valid named PowerPoint container including the date
	 * @param container
	 * @param date
	 */
	public PPTContainer(File container, Date date){
		this.container = container;
		this.date = date;
	}

	public File getContainer() {
		return container;
	}

	public void setContainer(File container) {
		this.container = container;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int compareTo(PPTContainer pptc) {
		return -1 * this.date.compareTo(pptc.getDate());
	}
}
