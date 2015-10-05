package net.coldbyte.ppinfscr.util;

import java.io.File;

import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class Helper {
	
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
	 * Replace placeholder from UserSettings.ppexeStartupCmds
	 * @param fullpath
	 * @return
	 */
	public static String getPPStartupCmd(String fullpath){
		String pploc = "\""+UserSettings.getString(Settings.PP_EXE_LOCATION)+"\"".replace("/", "\\");
		String ppcmd = UserSettings.ppexeStartupCmds.replace("[pptfile]", "\""+fullpath+"\"");
		return pploc + " " + ppcmd;
	}
	
	/**
	 * replace placeholder from UserSettings.ppexeLocation
	 * @return
	 */
	public static String[] getPPProcessCmd(){
		String processname = new File(UserSettings.ppexeLocation).getName().replace("\"", "").replace("'", "");
		String[] ret = UserSettings.tasklistCmds;
		ret[2] = ret[2].replace("[ppexe]", "\""+processname+"\"");
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
