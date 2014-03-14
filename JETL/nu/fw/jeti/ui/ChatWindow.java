package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;

import nu.fw.jeti.backend.roster.NormalJIDStatus;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.groupchat.GroupchatWindow;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 */

public class ChatWindow extends JFrame  
{
	private JPanel jPanel1 = new JPanel();
	private ChatSplitPane chatSplitPane;
	private JButton jButton1 = new JButton();
	private JID to;
	private Backend backend;
	private String thread;
	private String toName;
	private JLabel lblComposing = new JLabel();
	private String type;

	public ChatWindow(Backend backend, Message message)
	{
		this(backend,message.getFrom(),message.getThread());
	}

	public ChatWindow(Backend backend, JID to, String thread)
	{
		toName = to.getUser();
		this.backend = backend;
		this.thread = thread;
		this.to = to;
	
		String me = backend.getMyJID().getUser();
		toName+="/" + to.getResource();
		JMenuBar menuBar =new JMenuBar(); 
		JMenu menu = new JMenu();
		addToMenu(menu);
		I18N.setTextAndMnemonic("menu", menu);
		chatSplitPane = new ChatSplitPane(backend,to,toName,me,thread,false,this,menu);
		menuBar.add(menu);
	    setJMenuBar(menuBar);
		type = "images";
		init(Presence.NONE);
	}

	public ChatWindow(Backend backend, JIDStatus jidStatus, String thread)
	{
		this.backend = backend;
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu = new JMenu();
		addToMenu(menu);
		I18N.setTextAndMnemonic("menu", menu);
		menuBar.add(menu);
		toName = jidStatus.getNick();
		to = jidStatus.getCompleteJID();
		type = jidStatus.getType();
		String me = backend.getMyJID().getUser();
		int status = jidStatus.getShow();
		this.thread = thread;
		chatSplitPane = new ChatSplitPane(backend,jidStatus.getJID(),toName,me,thread,false,this,type,menu);
		init(status);
		
	}
	
	private void addToMenu(JMenu menu)
	{
	 JMenuItem item = new JMenuItem();
     I18N.setTextAndMnemonic("main.main.rostermenu.Local_Time",
                             item, true);
     item.addActionListener(new java.awt.event.ActionListener()
     {
         public void actionPerformed(ActionEvent e)
         {
             backend.send(new InfoQuery(to, "get", new IQTime()));
             
         }
     });
     menu.add(item);

     item = new JMenuItem();
     I18N.setTextAndMnemonic("main.main.rostermenu.Local_Version",
                             item, true);
     item.addActionListener(new java.awt.event.ActionListener()
     {
         public void actionPerformed(ActionEvent e)
         {
             backend.send(new InfoQuery(to, "get", new IQVersion()));
         }
     });
     menu.add(item);
       
     Map menuItems = backend.getMain().getRosterMenuItems();
     if (menuItems != null)
		{
			for (Iterator i = menuItems.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				item = new JMenuItem((String) entry.getKey());
				final RosterMenuListener listener = (RosterMenuListener) entry.getValue();
				item.addActionListener(new java.awt.event.ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						listener.actionPerformed(new NormalJIDStatus(to,toName) , null);
					}
				});
				menu.add(item);
			}
		}
	}

	private void init(int status) 
	{
		lblComposing.setIcon(StatusIcons.getImageIcon("keys"));
		lblComposing.setVisible(false);
		setIconImage(StatusIcons.getStatusIcon(status,type).getImage());
		I18N.setTextAndMnemonic("Send",jButton1);
		jButton1.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jButton1_actionPerformed(e);
			}
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) 
			{
				backend.getMain().removeChatWindow(ChatWindow.this);
				chatSplitPane.close();
				Preferences.putInteger("jeti","chatHeight",getHeight());
				Preferences.putInteger("jeti","chatWidth",getWidth());
				Preferences.putInteger("jeti","chatDivider",chatSplitPane.getDividerLocation());
				
				ChatWindow.this.dispose();  
			}
		});
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		jPanel1.setLayout(new GridLayout(1,3));
		jPanel1.add(lblComposing);
		jPanel1.add(jButton1);
		jPanel1.add(Box.createHorizontalStrut(0));
		this.getContentPane().add(chatSplitPane , BorderLayout.CENTER);
		setSize(Preferences.getInteger("jeti","chatWidth",300),Preferences.getInteger("jeti","chatHeigth",350));
		chatSplitPane.setDividerLocation(Preferences.getInteger("jeti","chatDivider",200));
	}
	
	public void exit()
	{
		chatSplitPane.close();
		dispose();
	}

	
	public boolean compareJID(JID jid)
	{
		return to.equals(jid);
	}

	public String getThread()
	{
		return thread;
	}

	public void appendMessage(Message message)
	{
		String name;
		//		msn hack
		if (to.equals(message.getFrom()))
			name = toName;
		else
			name = message.getFrom().getUser();

		Runnable updateAComponent = new Runnable()
		{
			public void run()
			{
				//lblComposing.setText("");
				lblComposing.setVisible(false);
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
		chatSplitPane.appendMessage(message,name);
	}

	public void appendPresenceChange(Presence presence)
	{
		final int show = presence.getShow();
		final String status = presence.getStatus();

		Runnable updateAComponent = new Runnable()
		{
			public void run()
			{
				//lblComposing.setText("");
				lblComposing.setVisible(false);
				setIconImage(StatusIcons.getStatusIcon(show, type).getImage());
			}
		};
		SwingUtilities.invokeLater(updateAComponent);

		String text;
		if (show == Presence.UNAVAILABLE)
		{
			text = " " + I18N.gettext("main.chat.is_currently_offline");
			setTitle(MessageFormat.format(I18N.gettext("main.chat.{0}_(offline)"),new Object[]{toName}));
		}
		else
		{
			text = " " + I18N.gettext("main.chat.changed_status_to:") + " " + Presence.toLongShow(show);
			if (status != null)
				text += '\n' + I18N.gettext("main.chat.with_status_message:") + " " + status;
			setTitle(toName);
		}
		
		chatSplitPane.appendSystemMessage(toName + text); 
	}

	public void composingID(String id)
	{
		if (id == null)id = "";
		chatSplitPane.composingID(id);
	}

	public void composing(JID jid, String type)
	{
		if (type != null)
		{
			Runnable updateAComponent = new Runnable()
			{
				public void run()
				{
					lblComposing.setVisible(true);
				}
			};
			SwingUtilities.invokeLater(updateAComponent);
		}
		else
		{
			Runnable updateAComponent = new Runnable()
			{
				public void run()
				{
					lblComposing.setVisible(false);
				}
			};
			SwingUtilities.invokeLater(updateAComponent);
		}
	}

	void jButton1_actionPerformed(ActionEvent e)
	{
		chatSplitPane.send(); 
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
