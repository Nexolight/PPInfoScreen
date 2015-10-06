package net.coldbyte.ppinfscr.control;

import net.coldbyte.ppinfscr.ui.WindowManager;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class Output {
	
	private static Output singleOutput = new Output();
	private WindowManager wm;
	
	/**
	 * Block direct call
	 */
	private Output(){
		
	}
	/**
	 * Get the singleton instance
	 * @return
	 */
	public static Output getInstance(){
		return singleOutput;
	}

	/**
	 * Set the window manager for use with console
	 * @param wm
	 */
	protected void setWM(WindowManager wm){
		singleOutput.wm = wm;
	}
	
	/**
	 * This will wrap the given string into HTML error string
	 * @param input
	 * @return
	 */
	private String wrapERR(String input){
		return "<span style=\"color:#b22b2b;font-family:Verdana;font-size:11;font-weight:bold;\">"+input+"</span>";
	}
	
	/**
	 * This will wrap the given string into HTML Info string
	 * @param input
	 * @return
	 */
	private String wrapINF(String input){
		return "<span style=\"color:#31602b;font-family:Verdana;font-size:11;font-weight:bold;\">"+input+"</span>";
	}
	
	/**
	 * This will wrap the given string into HTML Warn string
	 * @param input
	 * @return
	 */
	private String wrapWarn(String input){
		return "<span style=\"color:#a2a32b;font-family:Verdana;font-size:11;font-weight:bold;\">"+input+"</span>";
	}
	
	private String newLine(String input){
		return input + "<br>";
	}
	
	
	
	public void cOut(Object input){
		System.out.println(input);
		if(singleOutput.wm != null){
			singleOutput.wm.appendToConsole(newLine(wrapINF(input.toString())));
		}
	}
	
	/**
	 * This will only print to the internal console not the visual one
	 * @param input
	 */
	public void silentOut(Object input){
		System.out.println(input);
	}
	
	
}
