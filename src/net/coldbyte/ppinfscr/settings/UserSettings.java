package net.coldbyte.ppinfscr.settings;

import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.GUISettings;

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
	
	public static String ppinfscrSetDir 		= System.getProperty("user.home").replace("\\", "/") + "/.PPInfoScreen/";
	public static String ppinfscrDefaultStruct	= ppinfscrSetDir + "Structure";
	public static String ppinfscrSetFile_OUT 	= ppinfscrSetDir + "settings.ini";
	
	public String ppinfscrRoot					= getString(Settings.APPLICATION_ROOT);
	public String ppinfscrSources 				= ppinfscrRoot 		+ "/InformationScreen/";
	public String ppinfscrOptSet				= ppinfscrRoot 		+ "/Settings/";
	public static String ppinfscrDatadir		= ppinfscrSetDir 	+ "/data/";
	public String ppinfscrTmpldir 				= ppinfscrSources 	+ "/01.01.2015_1830/";
	public String ppexeLocation					= getString(Settings.PP_EXE_LOCATION);
	
	public String ppinfscrSetFile_JAR		= "/templates/settings.ini";
	public String ppinfscrOptSetFile_JAR	= "/templates/optsettings.ini";
	public String ppinfscrOptTmpl_JAR		= "/templates/example.pptx";
	
	public String ppinfscrOptSetFile_OUT 	= ppinfscrOptSet + "optsettings.ini";
	public String ppinfscrOptTmpl_OUT 		= ppinfscrTmpldir + "example.pptx";
	public static String jacobDLLx86		= "jacob-1.18-x86";
	public static String jacobDLLx64		= "jacob-1.18-x64";
	
	public static String datedFoldersRegex	= "^[0-3]{0,1}[0-9]{1}\\.[0-1]{0,1}[0-9]{1}\\.[2-3]{1}[0-9]{3}_[0-2]{0,1}[0-9]{1}[0-5]{0,1}[0-9]{1}$";
	public static String datedFoldersFormat	= "dd.MM.yyyy_HHmm";
	public static String validPPTRegex		= "^.*\\.(ppt|pptx)$";
	
	public String[] requiredDirs			= {	ppinfscrSetDir,
												ppinfscrDefaultStruct,
												ppinfscrSources, 
												ppinfscrOptSet, 
												ppinfscrDatadir, 
												ppinfscrTmpldir};
	
	public String[] requiredFiles			= {};
	
	public long datedFoldersLookupDelay 	= getLong(Settings.FOLDER_LOOKUP_DELAY);
	public long ppexeStateLookupDelay 		= getLong(Settings.PP_STATE_LOOKUP_DELAY); //ms
	
	public static String ppexeStartupCmds 	= " -S [pptfile]"; //[pptfile] will be replaced automatically
	public static String[] tasklistCmds 	= {"cmd.exe", "/C", "tasklist.exe | find [ppexe] /I"}; //[ppexe] will be replaced automatically

	public long ppNextActionDelay			= getLong(Settings.PP_NEXT_SHEET_DELAY); //ms
	
	
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
	private Object getDefaultSetting(Settings setting){
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
	public long getLong(Settings setting){
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
	public int getInt(Settings setting){
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
	public String getString(Settings setting){
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
	private String getLine(Settings setting){
		IOHandler io = new IOHandler();
		String line = io.readSetting(setting);
		return line;
	}
	
	/**
	 * This will build and return a new configuration file as string
	 * and includes the changes in the given instance
	 * @return
	 */
	public String getNewSettingsContent(GUISettings changedSettings){
		String output = new String();
		output += Settings.APPLICATION_ROOT.name() + " = " + changedSettings.getApplicationRoot() + "\r\n";
		output += Settings.FOLDER_LOOKUP_DELAY.name() + " = " + changedSettings.getFolderLookupDelay() + "\r\n";
		output += Settings.PP_STATE_LOOKUP_DELAY.name() + " = " + changedSettings.getPpStateLookupDelay() + "\r\n";
		output += Settings.PP_NEXT_SHEET_DELAY.name() + " = " + changedSettings.getPpNextSheetDelay() + "\r\n";
		output += Settings.PP_EXE_LOCATION.name() + " = " + changedSettings.getPpExeLocation();
		return output;
	}
}
