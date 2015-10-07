package net.coldbyte.ppinfscr.control;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.interfaces.IfKillable;
import net.coldbyte.ppinfscr.interfaces.IfPPBot;
import net.coldbyte.ppinfscr.io.IOHandler;
import net.coldbyte.ppinfscr.settings.UserSettings;
import net.coldbyte.ppinfscr.settings.UserSettings.Settings;
import net.coldbyte.ppinfscr.util.Helper;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public abstract class PPBot implements IfPPBot, IfKillable{
	private final IOHandler io;
	private final Output out;
	private final UserSettings uS;
	private final Helper helper;
	private boolean killtoggle;
	private PPBot inst;
	private Timer mysrvTimer;
	public enum PPBotState{
		READY, BUSY, INIT, ERROR
	}
	private PPBotState mystate = PPBotState.INIT;
	private List<File> fileQuerys = new ArrayList<File>();

	/**
	 * This is a simple Power Point bot for windows
	 * which should help to display and control the latest files
	 */
	public PPBot(){
		this.out = Output.getInstance();
		this.io = new IOHandler();
		this.uS = new UserSettings();
		this.helper = new Helper();
		this.killtoggle = false;
		this.inst = this;
		
		this.mysrvTimer = new Timer();
		startPPStatus(this.mysrvTimer);
		out.cInf("Started PowerPoint status checker thread");
		startClicker(this.mysrvTimer);
		out.cInf("Started PowerPoint clicker thread");
	}
	
	/**
	 * This will click through the slides
	 * @param t
	 */
	private void startClicker(Timer t){
		TimerTask mysrv = new TimerTask(){
			private Robot mybot;
			@Override
			public void run() {
				if(inst.mystate == PPBotState.BUSY){
					try {
						out.cInf("Show next slide");
						this.mybot = new Robot();
						this.mybot.keyPress(KeyEvent.VK_RIGHT);
					} catch (AWTException e) {
						out.cErr("Cannot perform a click/keystroke - AWTException", e);
						e.printStackTrace();
					}
				}
			}
		};
		t.schedule(mysrv, 0, this.uS.ppNextActionDelay);
	}
	
	
	/**
	 * This will check the power point state from time to time
	 */
	private void startPPStatus(Timer t){
		TimerTask mysrv = new TimerTask(){
			
			private ProcessBuilder pbTskl = new ProcessBuilder(helper.getPPProcessCmd());
			private Process processTskl;
			private BufferedReader stdinTskl, stderrTskl;
			
			private ProcessBuilder pbPP;
			private Process processPP; 
			private BufferedReader stdinPP, stderrPP;
			
			private File openedFile;
			
			@Override
			public void run() {
				if(!killtoggle){
					try {
						this.processTskl = this.pbTskl.start();
						this.stdinTskl = new BufferedReader(new InputStreamReader(this.processTskl.getInputStream())); //dont remove it will block
						this.stderrTskl = new BufferedReader(new InputStreamReader(this.processTskl.getErrorStream())); //dont remove it will block
						String outputTskl = this.stdinTskl.readLine();
						this.processTskl.destroy();
						if(outputTskl != null){
							if(inst.mystate != PPBotState.BUSY){
								stateChanged(inst.mystate, PPBotState.BUSY);
								inst.mystate = PPBotState.BUSY;
							}
							
						}
						if(outputTskl == null){
							if(inst.mystate != PPBotState.READY){
								stateChanged(inst.mystate, PPBotState.READY);
								inst.mystate = PPBotState.READY;
							}
						}
						if(inst.fileQuerys.size() > 0){
							if(inst.mystate == PPBotState.BUSY){
								this.processPP.destroy();
								io.removeAll(this.openedFile);
							}
							if(inst.mystate == PPBotState.READY){
								this.openedFile = inst.fileQuerys.get(fileQuerys.size() -1);
								inst.fileQuerys = new ArrayList<File>();//remove all queries
								try{
									String cmds = helper.getPPStartupCmd(openedFile.getAbsolutePath());
									this.pbPP = new ProcessBuilder(cmds);
									this.processPP = this.pbPP.start();
									this.stdinPP = new BufferedReader(new InputStreamReader(this.processPP.getInputStream())); //dont remove it will block
									this.stderrPP = new BufferedReader(new InputStreamReader(this.processPP.getErrorStream())); //dont remove it will block
								} catch (IOException e) {
									out.cErr("Cannot start PowerPoint - IOExeption", e);
									e.printStackTrace();
								}
							}
						}
					} catch (IOException e) {
						if(inst.mystate != PPBotState.ERROR){
							stateChanged(inst.mystate, PPBotState.ERROR);
							inst.mystate = PPBotState.ERROR;
						}
						out.cErr("Cannot start tasklist - IOExeption", e);
						e.printStackTrace();
					}
					
					if(inst.mystate == PPBotState.INIT){
						inst.mystate = PPBotState.READY;
					}
				}
			}
		};
		t.schedule(mysrv, 0, this.uS.getLong(Settings.PP_STATE_LOOKUP_DELAY));
	}
	
	/**
	 * Use this to properly exit the listener thread
	 */
	public void killThread(){
		this.killtoggle = true;
		this.mysrvTimer.cancel();
		this.killPowerPoint();
	}
	
	private void killPowerPoint(){
		try {
			ProcessBuilder kPP = new ProcessBuilder(this.helper.getPPKillCmd());
			Process processKPP = kPP.start();
			BufferedReader stdinKPP = new BufferedReader(new InputStreamReader(processKPP.getInputStream())); //dont remove it will block
			BufferedReader stderrKPP = new BufferedReader(new InputStreamReader(processKPP.getErrorStream())); //dont remove it will block
		} catch (IOException e) {
			out.cErr("Cannot kill PowerPoint process - Exception", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * This will open the given file in PowerPoint presentation mode
	 * @param f
	 */
	protected void openNew(File f){
		File tmp = io.copyToTmp(f, true);
		this.fileQuerys.add(tmp); //Thread will handle this and remove it at the end
	}
}
