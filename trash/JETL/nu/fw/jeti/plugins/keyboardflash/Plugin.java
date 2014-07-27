// Created on 6-sep-2003
package nu.fw.jeti.plugins.keyboardflash;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 */

public class Plugin implements MessageListener,Plugins
{
	public final static String VERSION = "0.2";
	public final static String DESCRIPTION = "keyboardflash.Flashes_the_scrollock_led_on_new_message";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "keyboardflash";
	public final static String ABOUT = "by E.S. de Boer";
	public final static String PARENT = "Notifiers";
	private static Plugin plugin;
	private Robot robot;
	private boolean blinking = false; 
	
	public Plugin (Backend backend) throws AWTException
	{
		backend.addListener(MessageListener.class,this);
		robot = new Robot();
	}
	
	public static void init(Backend backend) throws AWTException
    {
    	plugin = new Plugin(backend);
	}

	public void unload(){}
	
	public static void unload(Backend backend)
	{
		backend.removeListener(MessageListener.class,plugin);
		plugin = null;
		//backend.addListener(PresenceListener.class,this);
	}

//	public void presenceChanged(final Presence presence)
//	{
//		Runnable updateAComponent = new Runnable() {
//			public void run()
//			{
//				lblName.setText(presence.getFrom().getUser());
//				label.setText(presence.getStatus());
//				place();
//			}
//		};
//		SwingUtilities.invokeLater(updateAComponent);
//	}

	public void message(final Message e)
	{
		if(!blinking)
		{
			blinking = true;
			Thread thread = new Thread()
			{
				private int runs;
				public void run()
				{
					while(runs < 16)
					{
						robot.keyPress(KeyEvent.VK_SCROLL_LOCK);
						robot.keyRelease(KeyEvent.VK_SCROLL_LOCK);
						try
						{
							Thread.sleep(1000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						runs++;
					}
					blinking = false;
				}
			};
			thread.start();
		} 
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
