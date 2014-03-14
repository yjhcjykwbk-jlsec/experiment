package nu.fw.jeti.applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import nu.fw.jeti.backend.LoginInfo;
import nu.fw.jeti.backend.Start;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.ui.AddContact;
import nu.fw.jeti.ui.LoginStatusWindow;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

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
 *
 *	Created on 14-nov-2003
 */

/**
 * @author E.S. de Boer
 *
 */
public class Jeti extends JApplet
{
	private Start start;
	public static JLabel from;
	private static Jeti applet;
	private URL exitURL;
	public static String groupchatRoom;
	public static String chatTO;
	public static String groupchatServer;
	public static LoginInfo loginInfo;
	public static boolean randomName=false;
	private Backend backend;
	private boolean started=false;
	private SecureMethodRunner secureMethodInvoker; 
		
	public void init()
	{
		applet = this;
		splash();
		Thread t = new Thread()
		{
			public void run()
			{
				String server = getParameter("SERVER");
				String portText = getParameter("PORT");
				boolean ssl = Boolean.valueOf(getParameter("SSL")).booleanValue();
				String user = getParameter("USER");
				String password = getParameter("PASSWORD");
				String resource = getParameter("RESOURCE");
				String host = getParameter("HOST");
				groupchatRoom = getParameter("GROUPCHATROOM");
				groupchatServer = getParameter("GROUPCHATSERVER");
				chatTO = getParameter("CHATTO");
				String language = getParameter("LANGUAGE");
				String country = getParameter("COUNTRY");
				try
				{
                                    exitURL = new URL(getDocumentBase(),
                                                      getParameter("EXITPAGE"));
				} catch (MalformedURLException e)
				{
					try
					{
						exitURL = new URL("http://jeti.jabberstudio.org");
					} catch (MalformedURLException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(server!=null)
				{
					if(resource == null) resource = "JetiApplet";
					else if(resource.equals("random"))
					{
						resource = "JetiRandom" +  System.currentTimeMillis();
						randomName = true;
					}
					int port;
					try
					{
						port = Integer.parseInt(portText);
					}
					catch (NumberFormatException ex)
					{
						if(ssl) port = 5223;
						else port = 5222;
					}
					loginInfo = new LoginInfo(server,host,user,password,resource,port,ssl);
				}
				Start.programURL = getCodeBase();
				Start.applet = true;
				final JPanel panel = new JPanel(new BorderLayout());
				start = new Start(getCodeBase().toString(),panel);
				backend = start.getBackend();
				secureMethodInvoker = new SecureMethodRunner(backend);
				secureMethodInvoker.start();
				backend.addExtensionHandler("jeti:prefs",new PreferencesHandler());
				backend.addListener(StatusChangeListener.class,new StatusChangeListener()
				{
					public void connectionChanged(boolean online)
					{//get preferences
						System.out.println("get pref");
						if(online)
						{
							backend.send(new InfoQuery("get",new IQPrivate(new JetiPrivatePreferencesExtension())));
							if(chatTO!=null)
				    		{
				    			backend.getMain().startChat(new JID(chatTO));
				    		}
						}
					}
					
					public void ownPresenceChanged(int show, String status)	{}
					public void exit(){}
				});
				new Popups(Jeti.this);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						setContentPane(panel);
						validate();
						//start.initPopups();
						
					}
				});
				started=true;
			}
		};
		t.start();
		
		
	}
	
	private void splash()
	{ 
		JLabel label = new JLabel("Jeti is Loading"+"...");
		label.setFont(new java.awt.Font("Serif", 1, 28)); 
		label.setBackground(new Color(150,150,255));
		getContentPane().setBackground(new Color(150,150,255));
		getContentPane().add(label,BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.setBackground(new Color(255,255,150));
		from = new JLabel("(c) 2001-2004 E.S. de Boer");
		from.setBackground(new Color(255,255,150));
		panel.add(from);
		JLabel version = new JLabel("Version: " + Start.VERSION);
		version.setBackground(new Color(255,255,150));
		panel.add(version);
		getContentPane().add(panel,BorderLayout.SOUTH); 
	}
		
	public void destroy()
	{
		if(secureMethodInvoker!=null)secureMethodInvoker.stopRunning();
		start.close();
	}
	
	public static void exit()
	{
		applet.getAppletContext().showDocument(applet.exitURL);
	}
	
	public boolean isReady()
	{
		System.out.println("ready " + started);
		return started;
	}
	
	public boolean isLoggedIn()
	{
		if(!started)return false;
		return backend.isLoggedIn();
	}
	
	public void openChat(String jid)
	{
		System.out.println(jid);
		try
		{
			JID j = JID.checkedJIDFromString(jid);
			secureMethodInvoker.addData(new Object[]{"openChat",j});
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		
		//backend.getMain().startChat(j);
	}
	
	public void addContact(String jid)
	{
		try
		{
			JID j = JID.checkedJIDFromString(jid);
			secureMethodInvoker.addData(new Object[]{"addContact",j});
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		//new AddContact(j, null, backend).show();
		
	}
	
	public void login(String username,String server,String password)
	{
		if(backend.isLoggedIn())
		{
			JID j =backend.getMyJID();
			if(j.getDomain().equals(server)&& j.getUser().equals(username))
			{
				return;
			}
		}
		secureMethodInvoker.addData(new Object[]{"login",username,server,password});
		
//		if(username==null || password==null)
//		{
//			new nu.fw.jeti.applet.LoginWindow(backend).show();
//		}
//		else new LoginStatusWindow(new LoginInfo(server,loginInfo.getHost(),username,password,
//				loginInfo.getResource(),loginInfo.getPort(),loginInfo.isSSl()),backend,1);
	}
	
//	public void login(String username,String server,String password,String resource, String port,String ssl)
//	{
//		if(username==null || password==null)
//		{
//			new nu.fw.jeti.applet.LoginWindow(backend).show();
//		}
//		else
//		{
//			boolean ssl = Boolean.valueOf(ssl).booleanValue();
//			new LoginStatusWindow(new LoginInfo(server,loginInfo.getHost(),username,password,
//				resource,port,ssl),backend,1);
//		}
//	}
	
	
	
	public static Class getPlugin(String name)
	{//keyboardflash does not work //filetranser requires home.read permision
		try
        {
            return Class.forName("nu.fw.jeti.plugins."+name+".Plugin");
		}
        catch (ClassNotFoundException e)
		{
			System.err.println(MessageFormat.format(I18N.gettext("main.error.{0}_plugin_not_found"), new Object[]{name}));
			return null;
		}
	}
	
	public static void getPrefPanel(String name,Map loadedPreferencePanels)
	{
		Class prefPanel =null;
		try
		{
			if(name.equals("emoticons")) prefPanel = nu.fw.jeti.plugins.emoticons.PrefPanel.class;
			else if(name.equals("xhtml")) prefPanel = nu.fw.jeti.plugins.xhtml.PrefPanel.class;
			else return;
		}
		catch (Error e)
		{
			System.out.println("no preferences panel");
			return;
		}
		loadedPreferencePanels.put(name,prefPanel);
	}
	
	class SecureMethodRunner extends Thread
	{
		
		private Backend backend;
		private LinkedList queue = new LinkedList();
		private volatile boolean isRunning=true;

	    SecureMethodRunner(Backend backend)
		{
	    	this.backend = backend;
		}
		
	    public void addData(Object method)
	    {
	    	synchronized(queue)
			{
				queue.addLast(method);
				queue.notifyAll();
			}
	    }
	    
	    public void stopRunning()
	    {
	    	isRunning = false;
			synchronized(queue){queue.notifyAll();}
	    }
	    

		public void run()
		{
			Object[] method;
			while (isRunning)
			{
				synchronized (queue)
				{
					if (queue.isEmpty())
					{
						try
						{
							queue.wait();
						} catch (InterruptedException e)
						{//bug when thrown? called when interrupted
							e.printStackTrace();
							return;
						}
						continue;
					}
					method = (Object[]) queue.removeFirst();
					
				}
				String m = (String)method[0];
				if(m.equals("openChat")) backend.getMain().startChat((JID)method[1]);
				else if (m.equals("addContact")) new AddContact((JID)method[1], null, backend).show();
				else if (m.equals("login")) login((String)method[1],(String)method[2],(String)method[3]);
			}
		}
		
		public void login(String username,String server,String password)
		{
			if(username==null || password==null)
			{
				new nu.fw.jeti.applet.LoginWindow(backend).show();
			}
			else new LoginStatusWindow(new LoginInfo(server,loginInfo.getHost(),username,password,
					loginInfo.getResource(),loginInfo.getPort(),loginInfo.isSSl()),backend,1);
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
