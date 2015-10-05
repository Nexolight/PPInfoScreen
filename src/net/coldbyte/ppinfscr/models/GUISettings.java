package net.coldbyte.ppinfscr.models;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class GUISettings {
	
	private String applicationRoot;
	private long folderLookupDelay;
	private long ppStateLookupDelay;
	private long ppNextSheetDelay;
	private String ppExeLocation;
	
	/**
	 * This model represents the possible gui settings
	 */
	public GUISettings() {
		super();
	}
	
	/**
	 * This model represents the possible gui settings
	 * @param applicationRoot
	 * @param folderLookupDelay
	 * @param ppStateLookupDelay
	 * @param ppNextSheetDelay
	 * @param ppExeLocation
	 */
	public GUISettings(String applicationRoot, long folderLookupDelay, long ppStateLookupDelay, long ppNextSheetDelay, String ppExeLocation) {
		super();
		this.applicationRoot = applicationRoot;
		this.folderLookupDelay = folderLookupDelay;
		this.ppStateLookupDelay = ppStateLookupDelay;
		this.ppNextSheetDelay = ppNextSheetDelay;
		this.ppExeLocation = ppExeLocation;
	}
	
	public String getApplicationRoot() {
		return applicationRoot;
	}
	public void setApplicationRoot(String applicationRoot) {
		this.applicationRoot = applicationRoot;
	}
	public long getFolderLookupDelay() {
		return folderLookupDelay;
	}
	public void setFolderLookupDelay(long folderLookupDelay) {
		this.folderLookupDelay = folderLookupDelay;
	}
	public long getPpStateLookupDelay() {
		return ppStateLookupDelay;
	}
	public void setPpStateLookupDelay(long ppStateLookupDelay) {
		this.ppStateLookupDelay = ppStateLookupDelay;
	}
	public long getPpNextSheetDelay() {
		return ppNextSheetDelay;
	}
	public void setPpNextSheetDelay(long ppNextSheetDelay) {
		this.ppNextSheetDelay = ppNextSheetDelay;
	}
	public String getPpExeLocation() {
		return ppExeLocation;
	}
	public void setPpExeLocation(String ppExeLocation) {
		this.ppExeLocation = ppExeLocation;
	}
	
}
