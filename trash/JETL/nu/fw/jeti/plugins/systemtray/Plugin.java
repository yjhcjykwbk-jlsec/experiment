package nu.fw.jeti.plugins.systemtray;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.ui.LoginWindow;
import nu.fw.jeti.ui.RegisterServices;
import nu.fw.jeti.ui.StatusButton;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

import com.jeans.trayicon.TrayIconException;
import com.jeans.trayicon.TrayIconPopup;
import com.jeans.trayicon.TrayIconPopupSimpleItem;
import com.jeans.trayicon.WindowsTrayIcon;

public class Plugin implements ActionListener, StatusChangeListener,Plugins
{
	private Jeti main;
	private WindowsTrayIcon trayIcon;
	private Backend backend;
	private int status;
	public final static String VERSION = "1.5";
	public final static String DESCRIPTION = "systemtray.shows_system_tray_icon";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "systemtray";
	public final static String ABOUT = "by E.S. de boer, uses TrayIcon by Jan Struyf";
	private static Plugin plugin;
	
	public static void init(Backend backend) throws TrayIconException,InterruptedException
	{
		plugin =  new Plugin(backend);
	}
	
	
	public Plugin(Backend backend) throws TrayIconException,InterruptedException
	{
		main = backend.getMain();
		this.backend = backend;
		//try
		{
			String appName = "TestTray";
			// Init the Tray Icon library given the name for the hidden window
			//WindowsTrayIcon.init();
			WindowsTrayIcon.initTrayIcon(appName);
			trayIcon = new WindowsTrayIcon(nu.fw.jeti.images.StatusIcons.getOfflineIcon().getImage() ,16,16);
			trayIcon.setPopup(makePopup());
			trayIcon.addActionListener(this);
			trayIcon.setVisible(true);
			//backend.addStatusChangeListener(this);
			backend.addListener(nu.fw.jeti.events.StatusChangeListener.class,this);
		}
	}

	public void unload() {}
	
	public static void unload(Backend backend)
	{
		backend.removeListener(nu.fw.jeti.events.StatusChangeListener.class,plugin);
		WindowsTrayIcon.cleanUp();
		plugin = null;
	}

	// Create the popup menu for each Tray Icon (on right mouse click)
	public TrayIconPopup makePopup()
	{
		// Make new popup menu
		TrayIconPopup popup = new TrayIconPopup();
//		TrayIconPopupSimpleItem item = new TrayIconPopupSimpleItem(ettext("&Chat...","systemtray"));
//
//		item.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				main.startChat();
//			}
//		});
//		popup.addMenuItem(item);

		TrayIconPopupSimpleItem item;
		TrayIconPopup subMenu = new TrayIconPopup(I18N.gettext("main.main.jetimenu.Account"));
		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.jetimenu.Login")+"...");
		item.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				LoginWindow.createLoginWindow(backend);
			}
		});
		subMenu.addMenuItem(item);
//		item = new TrayIconPopupSimpleItem(I18N.gettext("New Account...","systemtray"));
//		item.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//
//				String server = JOptionPane.showInputDialog(backend.getMainWindow(),text("Server: ","systemtray"),I18N.gettext("Create New Account","systemtray"),JOptionPane.QUESTION_MESSAGE);
//				if (server == null || server.equals("")) return;
//				backend.newAccount(server);
//			}
//		});
//		subMenu.addMenuItem(item);
		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.jetimenu.Manage_Services")+"...");
		item.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//new RegisterServices(backend.getRegisterServices(),backend).show();
				new RegisterServices(backend).show();
			}
		});
		subMenu.addMenuItem(item);
//		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.jetimenu.Manage_Services")+"...");
//		item.addActionListener(new java.awt.event.ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				JOptionPane.showMessageDialog(backend.getMainWindow(),backend.getAccountInfo(),I18N.gttext("About account","systemtray"),JOptionPane.QUESTION_MESSAGE);
//
//			}
//		});
//		subMenu.addMenuItem(item);

		popup.addMenuItem(subMenu);
		subMenu = new TrayIconPopup(I18N.gettext("systemtray.Status"));
		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.statusmenu.Change_message"));
		item.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String message = JOptionPane.showInputDialog(backend.getMainWindow(),I18N.gettext("main.main.statusmenu.Status_message:"));
				if (message == null) return;
				StatusButton.changeStatus(status,message);
			}
		});
		subMenu.addMenuItem(item);
	
		subMenu.addMenuItem(statusMenu(Presence.FREE_FOR_CHAT));
		subMenu.addMenuItem(statusMenu(Presence.AVAILABLE));
		subMenu.addMenuItem(statusMenu(Presence.DND));
		subMenu.addMenuItem(statusMenu(Presence.AWAY));
		subMenu.addMenuItem(statusMenu(Presence.XA));

		
		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.presencebutton.Invisible"));
		item.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StatusButton.changeStatus(Presence.INVISIBLE,null);
			}
		});
		subMenu.addMenuItem(item);
		popup.addMenuItem(subMenu);



		/*
		// Add show, about, submenu, separator & exit item
		TrayIconPopupSimpleItem item = new TrayIconPopupSimpleItem("&Show");
		// Each menu item can have it's own ActionListener
		item.addActionListener(new RestoreListener(true));
		popup.addMenuItem(item);
		item = new TrayIconPopupSimpleItem("&About");
		item.addActionListener(new AboutListener());
		popup.addMenuItem(item);
		// Create a submenu with title enable and items check 1 & check 2
		TrayIconPopup sub = new TrayIconPopup("&Enable");
		// Create and add two checkbox menu items
		TrayIconPopupCheckItem chk = new TrayIconPopupCheckItem("Check &1");
		sub.addMenuItem(chk);
		chk = new TrayIconPopupCheckItem("Check &2");
		sub.addMenuItem(chk);
		// Add submenu to the main menu
		popup.addMenuItem(sub);
		// Add a separator
		TrayIconPopupSeparator sep = new TrayIconPopupSeparator();
		popup.addMenuItem(sep);
		*/
		// Add exit item
		item = new TrayIconPopupSimpleItem(I18N.gettext("main.main.jetimenu.Exit"));
		item.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				main.close();
			}
		});
		popup.addMenuItem(item);
		return popup;
	}


//	private TrayIconPopup statusMenu(String shortStatus)
//	{
//		String longStatus="Available";
//		if(shortStatus.equals("chat")) longStatus ="Free for Chat";
//		else if(shortStatus.equals("away")) longStatus ="Away";
//		else if(shortStatus.equals("dnd")) longStatus ="Do not Disturb";
//		else if(shortStatus.equals("xa")) longStatus ="Extended Away";
//
//		TrayIconPopup subMenu = new TrayIconPopup (longStatus);
//
//		//subMenu.setIcon(StatusIcons.getStatusIcon(shortStatus));
//		java.util.List messages = nu.fw.jeti.util.Preferences.getMessages(shortStatus);
//		for(java.util.Iterator it=messages.iterator();it.hasNext();)
//		{
//			final String messageText=(String) it.next();
//			TrayIconPopupSimpleItem menuItem = new TrayIconPopupSimpleItem(messageText);
//			menuItem.status = shortStatus;
//			menuItem.addActionListener(new java.awt.event.ActionListener()
//			{
//				public void actionPerformed(ActionEvent e)
//				{
//					String shortStatus = ((TrayIconPopupSimpleItem)e.getSource()).status;
//					/*
//					String shortStatus = null;
//					if(longStatus.equals("Free for Chat")) shortStatus = "chat";
//					else if(longStatus.equals("Away")) shortStatus = "away";
//					else if(longStatus.equals("Do not Disturb")) shortStatus = "dnd";
//					else if(longStatus.equals("Extended Away")) shortStatus = "xa";
//					*/
//					//if(!connected) backend.login();//make all send check
//					backend.changeStatus(shortStatus,messageText);
//				}
//			});
//			subMenu.addMenuItem(menuItem);
//		}
//		return subMenu;
//	}
	
	private TrayIconPopup statusMenu(final int status)
	{
		TrayIconPopup subMenu = new TrayIconPopup (Presence.toLongShow(status));
		List messages = Preferences.getStatusMessages(status);
		for (Iterator it = messages.iterator(); it.hasNext();)
		{
			final String messageText = (String) it.next();
			TrayIconPopupSimpleItem menuItem = new TrayIconPopupSimpleItem(messageText);
			menuItem.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					StatusButton.changeStatus(status, messageText);
				}
			});
			subMenu.addMenuItem(menuItem);
		}
		return subMenu;
	}
	

	public void actionPerformed(ActionEvent e)
	{
		Container w = backend.getMainWindow();
		w.show();
		//if(w instanceof Window)((Window) w).toFront();
	}

	/*-----------------StatusChangeEvents---------------------------*/
	public void connectionChanged(boolean online)
	{
		try{
		if(!online)trayIcon.setImage(nu.fw.jeti.images.StatusIcons.getOfflineIcon().getImage(),16,16);
		}catch(Exception e){e.printStackTrace();}
	}

	public void exit()
	{
		unload(backend);
	}

	public void ownPresenceChanged(int status, String message)
	{
		try{
			this.status = status;
		    trayIcon.setImage(nu.fw.jeti.images.StatusIcons.getStatusIcon(status).getImage(),16,16);
		}catch(Exception e){e.printStackTrace();}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
