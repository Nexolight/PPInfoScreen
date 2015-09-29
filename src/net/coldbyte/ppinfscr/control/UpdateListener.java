package net.coldbyte.ppinfscr.control;

import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.interfaces.IfUpdateListener;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public abstract class UpdateListener implements IfUpdateListener{
	
	protected final TimerTask timertask;
	protected final Timer timer;
	
	/**
	 * Use this class to keep track of updates in the program structure
	 */
	public UpdateListener(){
		this.timertask = createUpdateService();
		this.timer = new Timer();
	}
	
	
	/**
	 * This will return a service which will keep looking for updated
	 * files and or updated structure
	 * @return
	 */
	private TimerTask createUpdateService(){
		TimerTask mysrv = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		return mysrv;
	}
}
