package net.coldbyte.ppinfscr.settings;

import java.util.List;

public class DefaultSettings {
	
	public static final long	slidePause 		= 10000;
	public static final long	checkForNew		= 5000;
	
	public static final String	ppinfscrRoot	= "Z:/";
	public static final String	ppinfscrSources = ppinfscrRoot 		+ "/InformationScreen/";
	public static final String 	ppinfscrOptSet	= ppinfscrRoot 		+ "/OptionalSettings/";
	public static final String 	ppinfscrSysdir	= ppinfscrRoot 		+ "/SystemStuff_DontTouch/";
	public static final String	ppinfscrTmpldir = ppinfscrSources 	+ "/01.01.2015_1830/";
	public static final String	ppexeLocation	= "\"C:\\Program Files (x86)\\Microsoft Office\\Office14\\POWERPNT.EXE\"";
	
	public static final String 	ppinfscrOptSetFile 	= ppinfscrOptSet + "optionalSettings.inf";
	public static final String 	ppinfscrOptTmpl 	= ppinfscrTmpldir + "example.pptx";
	public static final String	ppinfscrSysdirTmpPP	= ppinfscrSysdir + "currently_displayed";
	
	public static final String 	datedFoldersRegex	= "^[0-3]{0,1}[0-9]{1}\\.[0-1]{0,1}[0-9]{1}\\.[2-3]{1}[0-9]{3}_[0-2]{0,1}[0-9]{1}[0-5]{0,1}[0-9]{1}$";
	public static final String	datedFoldersFormat	= "dd.MM.yyyy_HHmm";
	public static final String	validPPTRegex		= "^.*\\.(ppt|pptx)$";
	
	public static final String[] requiredDirs		= {	ppinfscrSources, 
														ppinfscrOptSet, 
														ppinfscrSysdir, 
														ppinfscrOptSetFile,
														ppinfscrTmpldir};
	
	public static final String[] requiredFiles			= {};
	
	public static final long datedFoldersLookupDelay 	= 5000; //ms
	public static final long ppexeStateLookupDelay 		= 3000; //ms
	
	public static final String ppexeStartupCmds 		= ppexeLocation + " -S [pptfile]"; //[pptfile] will be replaced automatically
	public static final String[] tasklistCmds 			= {"cmd.exe", "/C", "tasklist.exe | find [ppexe] /I"}; //[ppexe] will be replaced automatically

	public static final long ppNextActionDelay			= 8000; //ms
	
}
