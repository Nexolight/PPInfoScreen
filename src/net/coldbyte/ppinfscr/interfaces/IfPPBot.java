package net.coldbyte.ppinfscr.interfaces;

import java.io.File;

import net.coldbyte.ppinfscr.control.PPBot.PPBotState;

/**
 *
 * (C) 2015 - Lucy von Känel
 * Licensed under the WTFPL v2 licence
 * See COPYING.txt
 *
 */
public interface IfPPBot {
	/**
	 * This will be called whenever the state of the PPBot has changed
	 */
	public void stateChanged(PPBotState oldstate, PPBotState newstate);
}
