package net.coldbyte.ppinfscr.util;

import java.io.File;

import net.coldbyte.ppinfscr.settings.DefaultSettings;

/**
 *
 * (C) 2015 - Lucy von K�nel
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
	
	public static String getPPStartupCmd(String fullpath){
		return DefaultSettings.ppexeStartupCmds.replace("[pptfile]", "\""+fullpath+"\"");
	}
	
	public static String[] getPPProcessCmd(){
		String processname = new File(DefaultSettings.ppexeLocation).getName().replace("\"", "").replace("'", "");
		String[] ret = DefaultSettings.tasklistCmds;
		ret[2] = ret[2].replace("[ppexe]", "\""+processname+"\"");
		return ret;
	}
}
