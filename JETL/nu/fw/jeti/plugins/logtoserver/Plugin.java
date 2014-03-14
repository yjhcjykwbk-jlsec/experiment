package nu.fw.jeti.plugins.logtoserver;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.events.ChatEndedListener;
import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.OwnMessageListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 * @version 1.0
 */
//TODO change to newmessagelog layout??
public class Plugin implements MessageListener, StatusChangeListener, OwnMessageListener//, ChatEndedListener, Plugins
{
	public final static String NAME = "logtoserver";
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = I18N.gettext("logtoserver.logs_messages_to_the_server,_get_them_back_with_servertolog");
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String ABOUT = "by E.S. de Boer";
	private Map openStreamJIDS = new HashMap();
	private Date date = new Date();
	private DateFormat dateFormat;
	private static Plugin plugin;
	private List logs= new LinkedList();
	private Backend backend;
	private boolean logFetched=false;

	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}

	public Plugin(Backend backend)
	{
		this.backend = backend;
		backend.addListener(ChatEndedListener.class, this);
		backend.addExtensionHandler("jeti:serverlog",new ServerLogHandler(this));
		dateFormat = DateFormat.getTimeInstance();
	}

	public void unload()
	{}

	public static void unload(Backend backend)
	{
		backend.removeListener(MessageListener.class, plugin);
		backend.removeListener(OwnMessageListener.class, plugin);
		backend.removeListener(StatusChangeListener.class, plugin);
		backend.removeExtensionHandler("jeti:serverlog");
	}
	
	public void addLogs(List serverLogs)
	{
		logFetched = true;
		logs.addAll(serverLogs);
		backend.send(new InfoQuery("set",new IQPrivate(new ServerLogExtension(logs))));
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
		//JIDStatus jidStatus = Backend.getJIDStatus(message.getTo());
		PrintWriter log = getLog(message.getTo());
		if (log != null) log.println(dateFormat.format(date) + I18N.gettext(" I  say: ") + message.getBody());
	}

	//public void onGroupchat(MessageEvent e){}

	private void add(Message e)
	{
		date.setTime(System.currentTimeMillis());
		JID jid = e.getFrom();
		PrintWriter log = getLog(jid);
		String nick = jid.getUser();
		if (e.getType().equals("groupchat")) nick = jid.getResource();
		else
		{
			JIDStatus jidStatus = Backend.getJIDStatus(jid);
			if (jidStatus != null) nick = jidStatus.getNick();
		}
		if (log != null) log.println(dateFormat.format(date) + " " + nick + I18N.gettext(" says: ") + e.getBody());
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
			String file = Start.path + "newlogs" + File.separator + to.toStringNoResource() + ".txt";
			try
			{
				log = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file, true)), true);
			}
			catch (IOException e2)
			{
				nu.fw.jeti.util.Popups.errorPopup(MessageFormat.format(I18N.gettext("{0} could not be opend in write mode"), new Object[] { new String(file) }), I18N.gettext("Logfile Error"));
				openStreamJIDS.put(to, "");
				return null;
			}
			openStreamJIDS.put(to, log);
			log.println();
			log.println("----------------" + date.toString() + "----------------");
			log.println();
		}
		return log;
	}
	
	private void fetchLog()
	{
		backend.send(new InfoQuery("get",new IQPrivate(new ServerLogExtension())));
	}

	public void chatEnded(final Document doc, final JID jid,Date startDate)
	{
		Log log = new Log(jid,date.toString());
		logs.add(log);
		Element e = doc.getRootElements()[0];
		for (int i = 1; i < e.getElementCount(); i++)
		{
			parseElement(e.getElement(i), log);
		}
		if(logFetched)backend.send(new InfoQuery("set",new IQPrivate(new ServerLogExtension(logs))));
		else fetchLog();
		
	}
	

	private void parseElement(Element elem, Log log)
	{
		AttributeSet set = elem.getAttributes();
		if (set.isDefined("time")) log.write(set.getAttribute("time") + " ");
		if (elem.getName().equals("paragraph"))
		{
			if (elem.getStartOffset() != 0)
			{//only <br/>?
				//wordList.add(new
				// Word("\n",(SimpleAttributeSet)attr.clone()));
				log.newLine();
			}
		}
		if (elem.getName().equals("content"))
		{
			if (elem.getElementCount() > 0)
			{
				for (int i = 0; i < elem.getElementCount(); i++)
				{
					parseElement(elem.getElement(i), log);
				}
			}
			else
			{
				try
				{
					int offset = elem.getEndOffset() - elem.getStartOffset();
					String text = elem.getDocument().getText(elem.getStartOffset(), offset);
					//remove linebreaks
					if (text.charAt(text.length() - 1) == '\n')
					{
						//System.out.println("enter");
						text = text.substring(0, text.length() - 1);
						//System.out.println(text);
					}
					//addToWordList(text, attr, wordList);
					log.write(text);
				}
				catch (BadLocationException ble)
				{
					ble.printStackTrace();
				}
			}
		}
		else if (elem.getElementCount() > 0)
		{
			for (int i = 0; i < elem.getElementCount(); i++)
			{
				parseElement(elem.getElement(i), log);
			}
		}
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

	public void ownPresenceChanged(int a, String b)
	{}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
