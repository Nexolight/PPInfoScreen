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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PPTContainer))
			return false;
		PPTContainer other = (PPTContainer) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}
}
