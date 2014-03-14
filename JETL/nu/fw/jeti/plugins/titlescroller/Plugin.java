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
 
package nu.fw.jeti.plugins.titlescroller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.Timer;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Notifiers;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, ActionListener, Notifiers
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION ="titlescroller.Scrolls_the_window_title_of_chat_windows_on_new_messages";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "titlescroller";
	public final static String ABOUT = "by E.S. de boer";
	public final static String PARENT = "Notifiers";
	
	private Timer timer;
	private StringBuffer tekst;
	private int teller;
	private int currentChar;
	private long maxScrollTime;
	private long startTime;
	private JFrame frame;
	private String title;

    private WindowFocusListener focusListener = new WindowFocusListener() {
        public void windowGainedFocus(WindowEvent e) {
            stop();
        }
        public void windowLostFocus(WindowEvent e)
        {}
    };

	public static void init(Backend backend){}
	
	public void unload()
	{
		if(frame != null) {
            frame.removeWindowFocusListener(focusListener);
            frame.setTitle(title);
        }
		timer.stop();
		timer = null;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	

	public void init(JFrame frame, String title)
	{
		this.frame = frame;
		this.title =title;
		maxScrollTime = Preferences.getInteger("titlescroller", "scrolltime", 120);
		maxScrollTime*=1000;
		timer = new Timer(Preferences.getInteger("titlescroller", "scrollspeed", 200),this);
		frame.addWindowFocusListener(focusListener);
	}
	

	public void start(String title)
	{
		if(!frame.isFocused())
		{	
			timer.stop();
			tekst = new StringBuffer(title);
			startTime = System.currentTimeMillis();
			currentChar =0;
			teller =0;
			timer.restart();
		}
	}

	public void stop()
	{
		timer.stop();
		frame.setTitle(title);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(maxScrollTime >0 && System.currentTimeMillis()-startTime >= maxScrollTime) 
		{
			stop();
		}
		else
		{	
			frame.setTitle(tekst.substring(currentChar));
			currentChar++;
			if (currentChar>tekst.length()) currentChar=0;
			teller++;
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
