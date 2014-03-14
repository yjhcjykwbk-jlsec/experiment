/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 *
 *	Created on 11-jan-2004
 */
 
package nu.fw.jeti.plugins.titleflash;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.NativeUtils;
import nu.fw.jeti.plugins.Notifiers;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins,Notifiers
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "titleflash.Flashes_the_titlebar_when_a_message_arrives";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "titleflash";
	public final static String ABOUT = "by E.S. de Boer";
	public final static String PARENT = "Notifiers";
	private static NativeUtils util;
	private Flash flash;
	private Thread flasher;
		
	public static void init(Backend backend) 
	{
	}
	
	public static void unload(Backend backend)
	{
		util = null;
	}

	public void unload() 
	{
		if(flash !=null) flash.stop();
		flasher = null;
	}
	
	private void loadWindowsUtils() throws InstantiationException
	{
		if(util==null)
		{
			if(PluginsInfo.isPluginLoaded("windowsutils"))
			{
				util =(NativeUtils)PluginsInfo.newPluginInstance("windowsutils");
			}
            if (util == null) {
                throw new InstantiationException("Windowsutils native code not loaded");
            }
		}
	}

	public void init(JFrame frame, String title)
	{
        try {
            loadWindowsUtils();
        } catch (InstantiationException e) {
            System.out.println("Titleflash: " + e.getMessage());
            return;
        }

		flash = new Flash(frame);
		frame.addWindowFocusListener(new WindowFocusListener()
		{
			public void windowGainedFocus(WindowEvent e)
			{
				if(flash !=null) flash.stop();
				flasher = null;
			}
			public void windowLostFocus(WindowEvent e)
			{}
		});	
	}
		
	public void setTitle(String title) {}
		
	public void start(String title)
	{
		if(flasher == null && util != null)
		{	
			flash.start();
			flasher = new Thread(flash);
			flasher.start();
		}
	}
		
	public void stop() {}

	static class Flash implements Runnable
	{
		//private int count =10;
		private int intratime = 1000;
		private int intertime = 100;
		//private int flashTime = 10000;
		//private long startTime;
		private JFrame frame;
		private volatile boolean flash = true;
		
		public Flash(JFrame frame)
		{
			this.frame = frame;
		}
		
		synchronized public boolean start()
		{
			if(frame.isFocused()) return false;
			//startTime = System.currentTimeMillis();
		    flash = true;
		    return true;
		}
		
		synchronized public void stop()
		{
		    flash = false;
			notifyAll();
		}
		
		public void run() {
			try {
				// flash on and off each time
				while(flash)
				{
					util.flash(frame,true);
					Thread.sleep(intratime);
					util.flash(frame,false);
					Thread.sleep(intertime);
				}
				// turn the flash off
				util.flash(frame,false);
			} catch (Exception ex) {
				System.out.println("Titleflash: " + ex.getMessage());
			}
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
