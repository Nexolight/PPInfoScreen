package net.coldbyte.ppinfscr.control;

import java.util.Timer;
import java.util.TimerTask;
import net.coldbyte.ppinfscr.interfaces.IfHealthListener;
import net.coldbyte.ppinfscr.interfaces.IfKillable;
import net.coldbyte.ppinfscr.io.IOHandler;

/**
*
* (C) 2015 - Lucy von Känel
* Licensed under the WTFPL v2 licence
* See COPYING.txt
*
*/
public abstract class HealthListener implements IfHealthListener, IfKillable{
	
	private final IOHandler io;
	private final HealthListener inst;
	private Output out;
	private boolean killtoggle;
	private Timer mysrvTimer;
	
	/**
	 * Use this class to keep the program structure clean
	 */
	public HealthListener(){
		this.out = Output.getInstance();
		this.io = new IOHandler();
		this.inst = this;
		this.killtoggle = false;
		this.mysrvTimer = new Timer();
		createHealthService(this.mysrvTimer);
	}

	
	/**
	 * This will return a service which will make sure that the program structure stays
	 * intact while executing
	 * @return
	 */
	private void createHealthService(Timer t){
		TimerTask mysrv = new TimerTask(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		//t.scheduleAtFixedRate(mysrv, 0, DefaultSettings.datedFoldersLookupDelay);
	}
	
	/**
	 * Use this to properly exit the listener thread
	 */
	public void killThread(){
		this.killtoggle = true;
		this.mysrvTimer.cancel();
	}

}
