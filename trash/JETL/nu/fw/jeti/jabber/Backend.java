/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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
 */
package nu.fw.jeti.jabber;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JWindow;

import nu.fw.jeti.events.*;

import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.backend.*;
import nu.fw.jeti.backend.roster.*;

/**
 * <p>Title: im</p>
 * <p>Description: </p>

 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class Backend
{
    private Map eventListeners;
	private Map presenceListeners;
	private int identifier;
	private Connect connect;
	private Roster roster;
	private Server server;
	private Jeti main;
	private JFrame mainFrame;
	private Container mainWindow;
	private Start start;
	private IQTimerQueue iqTimerQueue;
	
	
    public Backend(Start start)
    {
		eventListeners = new HashMap(10);
		iqTimerQueue = new IQTimerQueue();
		connect = new Connect(this,iqTimerQueue);
		server =new Server(this);
		roster = new Roster(this,server);
		this.start = start;
    }

	public void setMain(Jeti main,Window window, JFrame frame)
	{
		this.main = main;
		this.mainWindow = window;
		this.mainFrame = frame;
	}

	public Container getMainWindow()
	{//TODO bit of a hack, think of something nicer
		if(mainWindow==null)
		{
			mainWindow=main.getTopLevelAncestor();
		}
		return mainWindow;
	}
	
	public JFrame getMainFrame(){return mainFrame;}
	
	public Jeti getMain(){return main;}
/*
	public void setBackend(JabberBackend backend)
	{
	    this.backend = backend;
	}
*/
	public void login(LoginInfo loginInfo)
	{
		connect.login(loginInfo);
	}
	
	
	public void autoLogin(LoginInfo loginInfo,int tries)
	{
		connect.autoLogin(loginInfo,tries);
	}
	
	public void abortLogin()
	{
		connect.abort();
	}

	public JID getMyJID()
	{
		return Connect.getMyJID();
	}

	public synchronized String getIdentifier()
	{
		return "JETI_" + identifier++;
	}

	public String createThread()
	{
		return  Integer.toHexString("JETI".hashCode()) + Long.toHexString(System.currentTimeMillis());
	}

	public String getAccountInfo()
	{
		return connect.getAccountInfo();
	}
	
	
	
	public Map getAvailableTransports()
	{
		return server.getAvailableTransports();
	}

	public void changeStatus(int show,String status)
	{
		connect.changeStatus(show, status);
	}

	public void rosterLoaded()
	{
		connect.connected();
	}

	public void disconnect()
	{
		connect.disconnect();
	}
	
	public boolean isLoggedIn()
	{
		return connect.isLoggedIn();
	}

	public void streamError()
	{
		connect.streamError();
	}

	public void exit()
	{
		if(!Start.applet)
		{
			System.out.println("This window will close in one minute");
			Timer t = new Timer(true);
			t.schedule(new TimerTask()
			{
				public void run()
				{
					System.exit(0);
				}
			},10000);
		}
				
		//end connection
		connect.exit();
		
		Frame[] frames = Frame.getFrames();
		for(int i =0;i<frames.length;i++)
		{//remove all opened jeti frames
			String name = frames[i].getClass().getName();
			System.out.println(name);
			if(name.startsWith("nu.fw.jeti"))frames[i].dispose();
			//frames[i].dispose();
			if(frames[i].isDisplayable())System.out.println(name);
		}
		
		//unload plugins
		start.exit();
		//remove references'
//		start=null;
//		connect=null;
//		roster=null;
//		server=null;
//		main=null;
//		mainFrame=null;
//		mainWindow =null;
		eventListeners.clear();
		if(presenceListeners!=null)presenceListeners.clear();
		
	}

	/**
	 *	send packets
	 *	please send messages that need to be logged with sendMessage
	 *  @param packet
	 */
	public void send(Packet packet)
	{
		connect.send(packet);
	}
	
	public void send(InfoQuery query, IQResultListener listener,int timeout)
	{
		iqTimerQueue.add(query,listener,timeout);
		send(query);
	}

	/**
	 * needed to log own send messages
	 * @param message
	 */
	public void sendMessage(Message message)
	{
		//if(isr
		for(Iterator j = getListeners(OwnMessageListener.class);j.hasNext();)
		{
			((OwnMessageListener)j.next()).sendMessage(message);
		}
		connect.send(message);
	}

//	public void browse(JID jid, BrowseListener listener)
//	{
//		connect.browse(jid,listener);
//	}
	
	public void getItems(JID jid, DiscoveryListener listener, boolean useCache)
	{
		connect.getItems(jid,listener,useCache);
	}
	
	public void getItems(JID jid, DiscoveryListener listener)
	{
		connect.getItems(jid,listener);
	}
	
	public void getInfo(JID jid, DiscoveryListener listener)
	{
		connect.getInfo(jid,listener);
	}
	
	public void getItems(JID jid,String node, DiscoveryListener listener)
	{
		connect.getItems(jid,node,listener);
	}
	
	public void getInfo(JID jid,String node, DiscoveryListener listener)
	{
		connect.getInfo(jid,node,listener);
	}
	
	
//	//muc hack
//	public void browseNotCached(JID jid, BrowseListener listener)
//	{
//		connect.browseNotCached(jid,listener);
//	}

	public static JIDStatus getJIDStatus(JID jid)
	{
		return Roster.getJIDStatus(jid);
	}

	public String[] getAllGroups()
	{
		return roster.getAllGroups();
	}

	/**
	 * makes a new account
	 * (the current connection will be logged off)
	 * @param name Username (may be null)
	 * @param password User password (may be null)
	 */
	public void newAccount(String server,String name,String password)
	{
		disconnect();
		new NewAccount(server,connect,name,password);
	}
	
	
	public void addExtensionHandler(String namespace,ExtensionHandler handler)
	{
		connect.getHandlers().addExtensionHandler(namespace,handler);
	}
	
	public void removeExtensionHandler(String namespace)
	{
		connect.getHandlers().removeExtensionHandler(namespace);
	}
	
	/**
	 * Add a listener for a specific presence
	 * @param jid The JID of which the presence is to be monitored (resource is not considered) 
	 * @param listener The class that is interested in presence events
	 */
	public synchronized void addPresenceListener(JID jid, PresenceListener listener)
	{
		 if(presenceListeners == null)presenceListeners = new HashMap(); 
		 presenceListeners.put(jid,listener); 
	}
	
	/**
	 * removes a presencelistener
	 * @param jid The JID of which presence is monitored
	 */
	public synchronized void removePresenceListener(JID jid)
	{
		presenceListeners.remove(jid);
		if (presenceListeners.isEmpty()) presenceListeners = null;
	}
	
	/**
	 * Gets the presenceListener which is monitoring this JID 
	 * @param jid The JID of which the presenceListener is requested
	 * @return PresenceListener
	 */
	public synchronized PresenceListener getPresenceListener(JID jid)
	{
		if(presenceListeners == null) return null;
		return (PresenceListener)presenceListeners.get(jid);
	}
	
	public synchronized void addListener(Class type, JETIListener listener)
	{//class meegeven?
		LinkedList list = (LinkedList) eventListeners.get(type.getName());
		if(list == null)
		{
			list = new LinkedList();
		    eventListeners.put(type.getName(),list);
		}
		list.add(listener);
	}

	public synchronized void removeListener(Class type, JETIListener listener)
	{
		LinkedList list = (LinkedList) eventListeners.get(type.getName());
		if(list == null) return;
		list.remove(listener);
		if(list.isEmpty()) eventListeners.remove(type.getName());//good idea?
	}

	public synchronized Iterator getListeners(final Class type)
	{
		LinkedList listeners = (LinkedList)eventListeners.get(type.getName());
		if(listeners == null)
		{//no listeners so return a empty iterator, if happens use isRegisteredListener(class type)
			return new Iterator()
			{//empty iterator
				public Object next()
				{
					throw new NoSuchElementException();
				}
				public boolean hasNext()
				{
					//System.out.println(type + " listener not registerd");
					//throw new RuntimeException();
					return false;
				}
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		return new LinkedList(listeners).iterator();//no concurrentmodexcep
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
