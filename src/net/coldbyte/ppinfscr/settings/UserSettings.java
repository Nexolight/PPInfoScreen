package net.coldbyte.ppinfscr.settings;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class UserSettings {
	
	public static enum Settings{
		APPLICATION_ROOT,
		FOLDER_LOOKUP_DELAY,
		PP_STATE_LOOKUP_DELAY,
		PP_NEXT_SHEET_DELAY,
		PP_EXE_LOCATION
	}
	
	/**
	 * Use this class to read & save the user settings
	 */
	public UserSettings(){
		
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static long getLong(Settings setting){
		return 0;
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static int getInt(Settings setting){
		return 0;
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static String getString(Settings setting){
		return null;
	}
	
}
