package net.coldbyte.ppinfscr.control;

import java.util.Timer;
import java.util.TimerTask;

import net.coldbyte.ppinfscr.interfaces.IfHealthListener;

/**
*
* (C) 2015 - Lucy von Känel
* Licensed under the WTFPL v2 licence
* See COPYING.txt
*
*/
public abstract class HealthListener implements IfHealthListener{
	
	protected final TimerTask timertask;
	protected final Timer timer;
	
	/**
	 * Use this class to keep the program structure clean
	 */
	public HealthListener(){
		this.timertask = createHealthService();
		this.timer = new Timer();
	}

	
	/**
	 * This will return a service which will make sure that the program structure stays
	 * intact while executing
	 * @return
	 */
	private TimerTask createHealthService(){
		TimerTask mysrv = new TimerTask(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		return mysrv;
	}

}
