// Created on 11-sep-2003
package nu.fw.jeti.plugins.idle;

import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.ui.StatusButton;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, StatusChangeListener 
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION ="idle.Sets_status_to_away_when_no_activity";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "idle";
	public final static String ABOUT = "by E.S. de Boer, uses idletrack from GAIM";
	
	private volatile boolean idle=false;//is idle?
	//private boolean enable=false;//use idle?
	private boolean sleeping=true;//is sleeping?
	private Backend backend;
	private int show;
	private String status;
	private IdleTimerThread thread;
	private static Plugin plugin;
	
	public static void init(Backend backend) throws InstantiationException
	{
		plugin = new Plugin(backend);
	}
	
	public Plugin(Backend backend) throws InstantiationException
	{
		this.backend = backend;
		try {
			if( IdleTimer.IdleInit())
			{
				backend.addListener(StatusChangeListener.class,this);
				thread = new IdleTimerThread(); 
				thread.start();
			}
			else throw new InstantiationException("Idle library error");
		}
		catch (Throwable e)
		{//error notify?
			throw new InstantiationException("Idle library error");
		}  
	}

	public void unload() {}
	
	public static void setParameters(int minutesAway,String awayMessage,int minutesXA,String xaMessage)
	{
		plugin.thread.setParameters(minutesAway, awayMessage,minutesXA,xaMessage);
	}
	
	
	public static void unload(Backend backend)
	{
		if(plugin.thread!=null) plugin.thread.interrupt(); 
		IdleTimer.IdleTerm();  
		backend.removeListener(StatusChangeListener.class,plugin);
		plugin = null;
	}
		
	public void ownPresenceChanged(int show, String status)
	{
		if(show == Presence.AVAILABLE || show == Presence.FREE_FOR_CHAT)
		{
			if(!idle) 
			{
				this.show = show;
				this.status = status;
				//enable = true;//enable if show op available
				if(sleeping)
				{
					thread.awake();
					sleeping =false; 
				}
			}
		}
		else if (!sleeping && !idle)
		{
			thread.sleep();
			sleeping = true;
		}
	}
	
	public void connectionChanged(boolean online)
	{
		if (!online)
		{
			thread.sleep();
			sleeping = true;
		}
	}
	
	public void exit(){}
	
	class IdleTimerThread extends Thread
	{
        private final static int SLEEP_TIME_ACTIVE = 60000;
        private final static int SLEEP_TIME_AWAY   = 10000;
		private long lastActivity;
		private volatile int idleAwayTime= Preferences.getInteger("idle","minutesAway", 5)*60000;
		private volatile String awayMessage =Preferences.getString("idle","awayMessage", "Idle");
		private volatile int idleXATime = Preferences.getInteger("idle","minutesXA", 20)*60000;
		private volatile String xaMessage = Preferences.getString("idle","XAMessage", "Idle");
		private long lastMoved;
		private volatile boolean sleep=true;
		private boolean xa=false;
		private int sleepTime=SLEEP_TIME_ACTIVE;
		
		public void setParameters(int minutesAway,String awayMessage,int minutesXA,String xaMessage)
		{
			idleAwayTime = minutesAway*60000;
			this.awayMessage = awayMessage;
			idleXATime = minutesXA*60000;
			this .xaMessage = xaMessage;
		}

		public void run()
		{
			while(!isInterrupted())
			{
				long activity = IdleTimer.IdleGetLastInputTime();
				if(sleep)
				{
					synchronized(this)
					{
						try
						{
							if(sleep)wait();
						}
						catch (InterruptedException e1)
						{
							return;
						}
					}	
				}
				if(activity != lastActivity)
				{//set available
					lastActivity = activity; 
					lastMoved = System.currentTimeMillis();
					if(idle)
					{ 
						StatusButton.changeStatus(show,status);
						idle = false;
						xa=false;
						sleepTime=SLEEP_TIME_ACTIVE;
					}
				}
				else if(!idle)
				{//set idle
					if(System.currentTimeMillis() >= lastMoved + idleAwayTime) 
					{
						sleepTime=SLEEP_TIME_AWAY;
						idle = true;
						StatusButton.changeStatus(Presence.AWAY,awayMessage);
					}
				}
				else if(!xa)
				{//set xa idle
					if(System.currentTimeMillis() >= lastMoved + idleXATime)
					{
						xa =true;
						StatusButton.changeStatus(Presence.XA,xaMessage);
					}
				}
				if(!isInterrupted())
				{
					try
					{
						sleep(sleepTime);
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
			}
			System.out.println("out of loop");
		}
		
		public void sleep()
		{
			sleep = true;
		}
		
		public synchronized void awake()
		{
			//System.out.println("waki waki");
			sleep = false;
			notifyAll(); 
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
