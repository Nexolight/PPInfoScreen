package net.coldbyte.ppinfscr.settings;

import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.models.GUISettings;

/**
 *
 * (C) 2015 - Lucy von K�nel
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
	public static String ppinfscrDatadir		= ppinfscrSetDir + "data/";
	public static String ppinfscrSetFile_OUT 	= ppinfscrSetDir + "settings.ini";
	
	private String ppinfscrRoot					= getString(Settings.APPLICATION_ROOT);
	private String ppinfscrTmpldir 				= ppinfscrRoot 	+ "/01.01.2015_1830/";
	
	public static String ppinfscrSetFile_JAR	= "/templates/settings.ini";
	public static String ppinfscrOptTmpl_JAR	= "/templates/example.pptx";
	
	public String ppinfscrOptTmpl_OUT 		= ppinfscrTmpldir + "example.pptx";
	public static String jacobDLLx86		= "jacob-1.18-x86";
	public static String jacobDLLx64		= "jacob-1.18-x64";
	
	public static String datedFoldersRegex	= "^[0-3]{0,1}[0-9]{1}\\.[0-1]{0,1}[0-9]{1}\\.[2-3]{1}[0-9]{3}_[0-2]{0,1}[0-9]{1}[0-5]{0,1}[0-9]{1}$";
	public static String datedFoldersFormat	= "dd.MM.yyyy_HHmm";
	public static String validPPTRegex		= "^.*\\.(ppt|pptx|odp)$";
	public static String validPPEXENameRegex= "^(POWERPNT.EXE|powerpnt.exe|PPTVIEW.EXE|pptview.exe|SIMPRESS.EXE|simpress.exe)$";
	public static String isMicrosoftOSPRegex	= "^(POWERPNT.EXE|powerpnt.exe)$";
	public static String isMicrosoftOSPFParam	= "-S";
	public static String isMicrosoftPPVPRegex	= "^(PPTVIEW.EXE|pptview.exe)$";
	public static String isMicrosoftPPVPFParam	= "-F";
	public static String isOpenOfficePRegex 	= "^(SIMPRESS.EXE|simpress.exe)$";
	public static String isOpenOfficePFParam	= "-invisible -nologo -show";
	
	public static String[] requiredStatic	= {	ppinfscrSetDir, ppinfscrDatadir};
	public String[] requiredDynamic			= { ppinfscrRoot, ppinfscrTmpldir};
	
	public String[] requiredFiles			= {};
	
	public static String[] taskkillCmds		= {"cmd.exe", "/C", "taskkill /F /IM [processname]"}; //[ppprocessname] will be replaced automatically
	public static String ppexeStartupCmds 	= " [fullscreencmd] [pptfile]"; //[fullscreencmd] will be replaced by replaces depending on the used presentation software and [pptfile] will be replaced automatically
	public static String[] tasklistCmds 	= {"cmd.exe", "/C", "tasklist.exe | find [ppexe] /I"}; //[ppexe] will be replaced automatically
	
	public static long thirdPartyPlayPlayPPDelay 	= 8000; //unused - The delay for the fix command
	public static String[] thirdPartyPlayPPFix		= {""}; //unused - some command which will be executed to show the presentation in fullscreen
	
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
	 * Returns the GUI settings which can be non default
	 * @return
	 */
	public GUISettings getGUISettings(){
		GUISettings currentSettings = new GUISettings(	
				getString(Settings.APPLICATION_ROOT),
				getLong(Settings.FOLDER_LOOKUP_DELAY),
				getLong(Settings.PP_STATE_LOOKUP_DELAY), 
				getLong(Settings.PP_NEXT_SHEET_DELAY), 
				getString(Settings.PP_EXE_LOCATION)
		);
		return currentSettings;
	}
	
	/**
	 * Returns the default gui settings
	 * @return
	 */
	public GUISettings getDefaultGUISettings(){
		GUISettings currentSettings = new GUISettings(	
				DefaultSettings.ppinfscrRoot,
				DefaultSettings.datedFoldersLookupDelay,
				DefaultSettings.ppexeStateLookupDelay, 
				DefaultSettings.ppNextActionDelay, 
				DefaultSettings.ppexeLocation
		);
		return currentSettings;
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
