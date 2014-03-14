package nu.fw.jeti.plugins.messagelog;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.Document;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.roster.JIDStatusGroup;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.backend.roster.Roster;
import nu.fw.jeti.events.ChatEndedListener;
import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.OwnMessageListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class Plugin implements MessageListener, StatusChangeListener, OwnMessageListener, ChatEndedListener, Plugins
{
	public final static String NAME = "messagelog";
	public final static String VERSION = "2.0";
	public final static String DESCRIPTION = "messagelog.message_logger";
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String ABOUT = "by E.S. de Boer";
	private Map openStreamJIDS = new HashMap();
	private Date date = new Date();
	private DateFormat dateFormat;
	private static Plugin plugin;

	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}

	public Plugin(Backend backend)
	{
		File file = new File(Start.path + "logs" + File.separator);
		if (!file.exists())
		{
			file.mkdir();
		}
		backend.getMain().addToRosterMenu(I18N.gettext("messagelog.Show_MessageLog"),new RosterMenuListener ()
		{
			public void actionPerformed(JIDStatus jidStatus,JIDStatusGroup group)
			{
				if (jidStatus instanceof PrimaryJIDStatus || group ==null)
				{
					new MessageLogWindow(jidStatus);
				}
				else new MessageLogWindow(group.searchPrimaryJIDStatus(jidStatus.getNick()));
			}
		});
		backend.addListener(MessageListener.class,this);
		backend.addListener(OwnMessageListener.class,this);
		backend.addListener(StatusChangeListener.class,this);
		backend.addListener(ChatEndedListener.class, this);
		dateFormat = DateFormat.getTimeInstance();
	}

	public void unload()
	{
		exit();//close logs
	}

	public static void unload(Backend backend)
	{
		backend.removeListener(MessageListener.class,plugin);
		backend.removeListener(OwnMessageListener.class,plugin);
		backend.removeListener(StatusChangeListener.class,plugin);
		backend.getMain().removeFromRosterMenu(I18N.gettext("messagelog.Show_MessageLog"));
	}

	synchronized public void message(Message e)
	{// synchronized ivm send + received message threads
		add(e);
	}

	/*
	 * synchronized public void onChat(MessageEvent e) {// synchronized ivm send +
	 * received message threads add(e); }
	 */
	synchronized public void sendMessage(Message message)
	{// synchronized ivm send + received message threads
		date.setTime(System.currentTimeMillis());
		//message.getThread();
		//JIDStatus jidStatus = Backend.getJIDStatus(message.getTo());
		PrintWriter log =getLog(message.getTo());
		if(log!=null)log.println(dateFormat.format(date) + " " + I18N.gettext("messagelog.I_ say:") + " " +  message.getBody());
	}
	
	private void add(Message e)
	{
		date.setTime(System.currentTimeMillis());
		JID jid = e.getFrom();
		PrintWriter log = getLog(jid);
		if(log==null)return;
        if (e.getType().equals("error")) 
        {
        	log.println(dateFormat.format(date) + " "  + I18N.gettext("main.error.Error_in_chat:") + " " + e.getBody());
		}
		else
		{
			String nick = jid.getUser();
			if (e.getType().equals("groupchat")) nick = jid.getResource();
			else
			{
				JIDStatus jidStatus = Backend.getJIDStatus(jid);
				if (jidStatus != null) nick = jidStatus.getNick();
			}
			log.println(dateFormat.format(date) + " " + nick + " " +  I18N.gettext("messagelog.says:")+ " " + e.getBody());
		}
	}

	private PrintWriter getLog(JID to)
	{
		PrintWriter log = null;
		if (openStreamJIDS.containsKey(to))
		{
			Object o = openStreamJIDS.get(to);
			if (o instanceof String) return null;
			log = (PrintWriter) o;
		}
		else
		{
			//if(jidStatus == null) return null; //save only persons in roster
			String name = null;
			JIDStatus jidStatus = Backend.getJIDStatus(to);
			if (jidStatus != null)
			{
				if(jidStatus.getType().equals("unknown") || jidStatus.getType().equals("jabber"))name = to.toStringNoResource();
				else name = to.getUser() + "." + jidStatus.getType(); 
			}
			else name = to.toStringNoResource();
			
			String file = Start.path + "logs" + File.separator + name + ".txt";
			try
			{
				log = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file, true)), true);
			}
			catch (IOException e2)
			{
				nu.fw.jeti.util.Popups.errorPopup(MessageFormat.format(I18N.gettext("messagelog.{0}_could_not_be_opened_in_write_mode"),new Object[] { new String(file) }), I18N.gettext("messagelog.Logfile_Error"));
				openStreamJIDS.put(to,"");
				return null;
			}
			openStreamJIDS.put(to, log);
			log.println();
			log.println("----------------" + date.toString() + "----------------");
			log.println();
		}
		return log;
	}

	public void chatEnded(JID jid)
	{
		PrintWriter w =(PrintWriter)openStreamJIDS.remove(jid);
		if(w!=null)w.close();
//		Thread worker = new Thread()
//		{
//			public void run()
//			{
//				String name = null;
//				JIDStatus jidStatus = Backend.getJIDStatus(jid);
//				if (jidStatus != null)
//				{
//					if(jidStatus.getType().equals("unknown") || jidStatus.getType().equals("jabber"))name = jid.toStringNoResource();
//					else name = jid.getUser() + "." + jidStatus.getType(); 
//				}
//				else name = jid.toStringNoResource();
//
//				BufferedWriter log = null;
//				String file = Start.path + "newlogs" + File.separator + name + ".txt";
//				try
//				{
//					log = new BufferedWriter(new FileWriter(file, true));
//				}
//				catch (IOException e2)
//				{
//					nu.fw.jeti.util.Popups.errorPopup(file + " could not be opend in write mode", "Logfile Error");
//					return;
//				}
//				try
//				{
//					log.newLine();
//					log.write("----------------" + date.toString() + "----------------");
//					log.newLine();
//					Element e = doc.getRootElements()[0];
//					for (int i = 1; i < e.getElementCount(); i++)
//					{
//						parseElement(e.getElement(i), log);
//					}
//					log.close();
//				}
//				catch (IOException e2)
//				{
//					nu.fw.jeti.util.Popups.errorPopup(file + " could not be written", "Logfile Error");
//					return;
//				}
//			}
//		};
//		worker.start();
	}

	/*-----------------StatusChangeEvents---------------------------*/
	public void connectionChanged(boolean online)
	{
		if (!online) exit();
	}

	public void exit()
	{//offline close all open printstreams and clear cache
		for (Iterator i = openStreamJIDS.values().iterator(); i.hasNext();)
		{
			Object o = i.next();
			if (o instanceof String) continue;
			((PrintWriter) o).close();
		}
		openStreamJIDS.clear();
	}

	public void ownPresenceChanged(int a, String b)	{}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
