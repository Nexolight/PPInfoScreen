package net.coldbyte.ppinfscr.settings;

/**
 *
 * (C) 2015 - Lucy von K�nel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class DefaultSettings {
	
	/**
	 * These are the default settings which can be set by the user
	 */
	public DefaultSettings(){
		
	}

	protected static final String	ppinfscrRoot	= "";
	protected static final String	ppexeLocation	= "";
	protected static final long datedFoldersLookupDelay 	= 5000; //ms
	protected static final long ppexeStateLookupDelay 		= 3000; //ms
	protected static final long ppNextActionDelay			= 8000; //ms
	
}
