package net.coldbyte.ppinfscr.util;

import java.io.File;

import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;

/**
 *
 * (C) 2015 - Lucy von K�nel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class Helper {
	
	private UserSettings uS = new UserSettings();
	
	/**
	 * Just a helper class for some misc things
	 */
	public Helper(){
		
	}
	
	/**
	 * Compare the PowerPoint files only depending on their names and modified date
	 * Since it would be to much to override the File class itself.
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean pptDiff(File first, File second){
		if(first == null && second == null){
			return false;
		}else if(first == null && second != null){
			return true;
		}else if(first != null && second == null){
			return true;
		}else if(!first.getName().equals(second.getName())){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Get command to startup PowerPoint
	 * @param fullpath
	 * @return
	 */
	public String getPPStartupCmd(String fullpath){
		String pploc = "\""+this.uS.getString(Settings.PP_EXE_LOCATION)+"\"".replace("/", "\\");
		String ppcmd = UserSettings.ppexeStartupCmds;
		ppcmd = ppcmd.replace("[pptfile]", "\""+fullpath+"\"");
		String processname = new File(this.uS.getString(Settings.PP_EXE_LOCATION)).getName();
		if(processname.matches(UserSettings.isMicrosoftOSPRegex)){
			ppcmd = ppcmd.replace("[fullscreencmd]", UserSettings.isMicrosoftOSPFParam);
		}
		if(processname.matches(UserSettings.isMicrosoftPPVPRegex)){
			ppcmd = ppcmd.replace("[fullscreencmd]", UserSettings.isMicrosoftPPVPFParam);
		}
		if(processname.matches(UserSettings.isOpenOfficePRegex)){
			ppcmd = ppcmd.replace("[fullscreencmd]", UserSettings.isOpenOfficePFParam);
		}
		return pploc + " " + ppcmd;
	}
	
	/**
	 * Get command to check status of PowerPoint
	 * @return
	 */
	public String[] getPPProcessCmd(){
		String processname = new File(this.uS.getString(Settings.PP_EXE_LOCATION)).getName();
		String[] ret = UserSettings.tasklistCmds;	
		ret[2] = ret[2].replace("[ppexe]", "\""+processname+"\"");
		return ret;
	}
	
	/**
	 * Get command to kill PowerPoint
	 * @return
	 */
	public String[] getPPKillCmd(){
		String[] ret = UserSettings.taskkillCmds;
		ret[ret.length-1] = ret[ret.length-1].replace("[processname]", new File(this.uS.getString(Settings.PP_EXE_LOCATION)).getName());
		return ret;
	}
	
	/**
	 * This will determine if the platform is 64 bit or not
	 * @return
	 */
	public static boolean is64(){
		boolean is64bit = false;
		if (System.getProperty("os.name").contains("Windows")) {
		    is64bit = (System.getenv("ProgramFiles(x86)") != null);
		} else {
		    is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
		}
		return is64bit;
	}
}
