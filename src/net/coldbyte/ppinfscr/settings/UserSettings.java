package net.coldbyte.ppinfscr.settings;

import net.coldbyte.ppinfscr.io.IOHandler;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class UserSettings {
	
	/**
	 * Use this to read and save the user setings. It's a combination 
	 * with the DefaultSettings. The methods here will try to look for 
	 * the some settings int the settings.ini first. If they found nothing the
	 * will redirect to the DefaultSettings. However there are some unchangeable
	 * settings. These are statically saved only here.
	 */
	public UserSettings(){
		
	}
	
	public static final String	ppinfscrSetDir 			= System.getProperty("user.home").replace("\\", "/") + "/.PPInfoScreen";
	public static final String 	ppinfscrSetFile_OUT 	= ppinfscrSetDir + "settings.ini";
	
	public static final String	ppinfscrRoot	= getString(Settings.APPLICATION_ROOT);
	public static final String	ppinfscrSources = ppinfscrRoot 		+ "/InformationScreen/";
	public static final String 	ppinfscrOptSet	= ppinfscrRoot 		+ "/Settings/";
	public static final String 	ppinfscrDatadir	= ppinfscrSetDir 	+ "/data/";
	public static final String	ppinfscrTmpldir = ppinfscrSources 	+ "/01.01.2015_1830/";
	public static final String	ppexeLocation	= getString(Settings.PP_EXE_LOCATION);
	
	public static final String	ppinfscrSetFile_JAR		= "/templates/settings.ini";
	public static final String	ppinfscrOptSetFile_JAR	= "/templates/optsettings.ini";
	public static final String	ppinfscrOptTmpl_JAR		= "/templates/example.pptx";
	
	public static final String 	ppinfscrOptSetFile_OUT 	= ppinfscrOptSet + "optsettings.ini";
	public static final String 	ppinfscrOptTmpl_OUT 	= ppinfscrTmpldir + "example.pptx";
	public static final String	jacobDLLx86				= "jacob-1.18-x86";
	public static final String	jacobDLLx64				= "jacob-1.18-x64";
	
	public static final String 	datedFoldersRegex	= "^[0-3]{0,1}[0-9]{1}\\.[0-1]{0,1}[0-9]{1}\\.[2-3]{1}[0-9]{3}_[0-2]{0,1}[0-9]{1}[0-5]{0,1}[0-9]{1}$";
	public static final String	datedFoldersFormat	= "dd.MM.yyyy_HHmm";
	public static final String	validPPTRegex		= "^.*\\.(ppt|pptx)$";
	
	public static final String[] requiredDirs		= {	ppinfscrSetDir,
														ppinfscrSources, 
														ppinfscrOptSet, 
														ppinfscrDatadir, 
														ppinfscrTmpldir};
	
	public static final String[] requiredFiles			= {};
	
	public static final long datedFoldersLookupDelay 	= getLong(Settings.FOLDER_LOOKUP_DELAY);
	public static final long ppexeStateLookupDelay 		= getLong(Settings.PP_STATE_LOOKUP_DELAY); //ms
	
	public static final String ppexeStartupCmds 		= ppexeLocation + " -S [pptfile]"; //[pptfile] will be replaced automatically
	public static final String[] tasklistCmds 			= {"cmd.exe", "/C", "tasklist.exe | find [ppexe] /I"}; //[ppexe] will be replaced automatically

	public static final long ppNextActionDelay			= getLong(Settings.PP_NEXT_SHEET_DELAY); //ms
	
	
	public static enum Settings{
		APPLICATION_ROOT,
		FOLDER_LOOKUP_DELAY,
		PP_STATE_LOOKUP_DELAY,
		PP_NEXT_SHEET_DELAY,
		PP_EXE_LOCATION
	}
	
	/**
	 * This will return the default setting
	 * @param setting
	 * @return
	 */
	private static Object getDefaultSetting(Settings setting){
		switch(setting){
		case APPLICATION_ROOT:
			return DefaultSettings.ppinfscrRoot;
		case FOLDER_LOOKUP_DELAY:
			return DefaultSettings.datedFoldersLookupDelay;
		case PP_STATE_LOOKUP_DELAY:
			return DefaultSettings.ppexeStateLookupDelay;
		case PP_NEXT_SHEET_DELAY:
			return DefaultSettings.ppNextActionDelay;
		case PP_EXE_LOCATION:
			return DefaultSettings.ppexeLocation;
		default:
			return null;
		}
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static long getLong(Settings setting){
		String line = getLine(setting);
		if(line != null){
			return (long) Long.valueOf(line);
		}else{
			return (long) getDefaultSetting(setting);
		}
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static int getInt(Settings setting){
		String line = getLine(setting);
		if(line != null){
			return (int) Integer.valueOf(line);
		}else{
			return (int) getDefaultSetting(setting);
		}
	}
	
	/**
	 * This will return the value of the given setting
	 * @param setting
	 * @return
	 */
	public static String getString(Settings setting){
		String line = getLine(setting);
		if(line != null){
			return line;
		}else{
			return (String) getDefaultSetting(setting);
		}
	}
	
	/**
	 * This will read out the setting line from the settings
	 * @param setting
	 * @return
	 */
	private static String getLine(Settings setting){
		IOHandler io = new IOHandler();
		String line = io.readSetting(setting);
		return line;
	}
}
