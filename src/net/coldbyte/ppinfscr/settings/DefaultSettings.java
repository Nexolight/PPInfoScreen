package net.coldbyte.ppinfscr.settings;

public class DefaultSettings {
	
	public static final long	slidePause 		= 10000;
	public static final long	checkForNew		= 5000;
	
	public static final String	ppinfscrRoot	= "Z:/";
	public static final String	ppinfscrSources = ppinfscrRoot + "/InformationScreen/";
	public static final String 	ppinfscrOptSet	= ppinfscrRoot + "/OptionalSettings/";
	public static final String 	ppinfscrSysdir	= ppinfscrRoot + "/SystemStuff_DontTouch/";
	public static final String	ppinfscrTmpldir = ppinfscrSources + "/01.01.2015_1830/";
	public static final String	ppexeLocation	= System.getenv("ProgramFiles") + "/Microsoft Office/Office14/POWERPNT.EXE";
	
	public static final String 	ppinfscrOptSetFile 	= ppinfscrOptSet + "optionalSettings.inf";
	public static final String 	ppinfscrOptTmpl 	= ppinfscrTmpldir + "example.pptx";
	public static final String	ppinfscrSysdirTmpPP	= ppinfscrSysdir + "currently_displayed";
	
	public static final String 	regexDatedFolders	= "^[0-3]{0,1}[0-9]{1}\\.[0-1]{0,1}[0-9]{1}\\.[2-3]{1}[0-9]{3}";
	
	public static final String[] requiredDirs		= {	ppinfscrSources, 
														ppinfscrOptSet, 
														ppinfscrSysdir, 
														ppinfscrOptSetFile,
														ppinfscrTmpldir};
	
	public static final String[] requiredFiles		= {};
}
