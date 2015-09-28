package net.coldbyte.ppinfscr.ui;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public class Output {
	
	private String classname;
	
	/**
	 * Use this class to write console output. It's basically a wrapper for future use
	 */
	public Output(String classname){
		this.classname = classname;
	}
	
	/**
	 * Just use print atm
	 * @param input
	 */
	public void cOut(Object input){
		System.out.println(input);
	}
}
