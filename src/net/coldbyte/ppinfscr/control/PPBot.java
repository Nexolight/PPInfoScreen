package net.coldbyte.ppinfscr.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.interfaces.IfPPBot;
import net.coldbyte.ppinfscr.settings.DefaultSettings;
import net.coldbyte.ppinfscr.ui.Output;
import net.coldbyte.ppinfscr.util.Helper;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public abstract class PPBot implements IfPPBot{
	
	private final Output out = new Output(this.getClass().getName());
	private boolean killtoggle = false;
	private Timer mysrvTimer;

	/**
	 * This is a simple Power Point bot for windows
	 * which should help to display and control the latest files
	 */
	public PPBot(){
		this.mysrvTimer = new Timer();
		startPPStatus(this.mysrvTimer);
	}
	
	
	/**
	 * This will check the power point state from time to time
	 */
	private void startPPStatus(Timer t){
		TimerTask mysrv = new TimerTask(){

			@Override
			public void run() {
				if(!killtoggle){
					
				}
			}
			
		};
		t.schedule(mysrv, 0, DefaultSettings.ppexeStateLookupDelay);
	}
	
	/**
	 * Use this to properly exit the listener thread
	 */
	protected void killThread(){
		this.killtoggle = true;
		this.mysrvTimer.cancel();
	}
	
	/**
	 * This will open the given file in PowerPoint presentation mode
	 * @param f
	 */
	protected void openNew(File f){
		try{
			String cmd = Helper.getPPStartupCmd(f.getAbsolutePath());
			out.cOut(cmd);
			ProcessBuilder pb = new ProcessBuilder(cmd);
			//final Map<String,String> env = pb.environment();
			final Process process = pb.start();
			//final BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
			//final BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		} catch (IOException e) {
			out.cOut("Cannot start PowerPoint - IOExeption");
			e.printStackTrace();
		}
	}
}
