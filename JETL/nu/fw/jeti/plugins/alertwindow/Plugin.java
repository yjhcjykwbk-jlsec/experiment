package nu.fw.jeti.plugins.alertwindow;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.NativeUtils;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class Plugin extends Window implements PresenceListener,MessageListener,Plugins
{
	public final static String VERSION = "2";
	public final static String DESCRIPTION = "alertwindow.Shows_an_alert_window_when_a_message_arrives";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "alertwindow";
	public final static String ABOUT = "by E.S. de Boer";
	public final static String PARENT = "Notifiers";
	//msn alert window make with 1.4 focus things
	private Label lblName;
	private Label label;
	private Timer timer = new Timer(true);
	private TimerTask tt;
	private int time; 
	private static Plugin plugin;
	private NativeUtils util;
	private Point pntLowerRight;
	private int alpha;
	private Window owner;
	public static boolean supportsAlpha;
		
	public Plugin(Window owner) 
	{
		super(owner);
		this.owner = owner;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx =0;
		c.gridy=0;
		c.weightx=1;
		c.insets = new Insets(2,2,0,2);
		setBackground(Color.BLACK);
		lblName = new Label();
		lblName.setFont((Font)UIManager.get("TextArea.font"));
		add(lblName,c);
		label = new Label();
		label.setFont((Font)UIManager.get("TextArea.font"));
		c.gridx =0;
		c.gridy=1;
		c.weightx=1;
		c.insets = new Insets(0,2,2,2);
		add(label,c);
		enableInputMethods(false);
		setSize(1,1);
		setVisible(true);
		setFocusableWindowState(false);
		setFocusable(false);
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = env.getMaximumWindowBounds();
		pntLowerRight = new Point(bounds.x + bounds.width,bounds.y + bounds.height);
	}
	
    public static void init(Backend backend)
    {
    	plugin = new Plugin(new Frame());
    	backend.addListener(MessageListener.class,plugin);
		backend.addListener(PresenceListener.class,plugin);
		Color color = new Color(Preferences.getInteger("alertwindow","backgroundcolor",SystemColor.info.getRGB()));
		plugin.label.setBackground(color);
		plugin.lblName.setBackground(color);
		Color foreground = new Color(Preferences.getInteger("alertwindow","foregroundcolor",SystemColor.controlText.getRGB()));
		plugin.label.setForeground(foreground);
		plugin.lblName.setForeground(foreground);
		plugin.time =Preferences.getInteger("alertwindow","popuptime", 5)*1000;
	}
	
	private void loadWindowsUtils() 
	{
		if(util==null)
		{
			if(PluginsInfo.isPluginLoaded("windowsutils"))
			{
				util =(NativeUtils)PluginsInfo.newPluginInstance("windowsutils");
				supportsAlpha = util.supportsAlpha();
			 	if(util!=null)
				{
					try
					{//make window before always on top & alpha
						util.windowAlwaysOnTop(plugin,true);
						if(supportsAlpha)
						{	
							alpha = Preferences.getInteger("alertwindow","alpha", 100);
							if(alpha<100)util.setWindowAlpha(plugin,alpha);
						}
					}
					catch (Throwable ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else 
	 	{/* 2004-09-07 - Juergen Ulbts - added the else section for OS/2 and eCS to call the native windowAlwaysOnTop function */
			if ( System.getProperty("os.name").equals("OS/2") ) {
				util.windowAlwaysOnTop(plugin, true);
			}
		}
	}
	
    public void unload() 
    {
    	if(util!=null)
    	{
    		try{
    			if(util.supportsAlpha())util.setWindowAlpha(this,0);
    			util.windowAlwaysOnTop(this,false);
    		}catch(Exception e)
			{
    			//util already unloaded
    		}
    	}
       	dispose();
       	owner.dispose();
    }
    
	public static void unload(Backend backend)
	{
		backend.removeListener(MessageListener.class,plugin);
		backend.removeListener(PresenceListener.class,plugin);
		plugin.unload();
		plugin = null;
	}

	public void presenceChanged(final Presence presence)
	{
		loadWindowsUtils();
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				lblName.setText(getNick(presence.getFrom()));
				String status = presence.getStatus();
				if(status==null)status = Presence.toLongShow(presence.getShow());
				label.setText(status);
				place();
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
	}

	public void message(final Message e)
	{
		loadWindowsUtils();
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				JID jid = e.getFrom();
				String nick;
				if(e.getType().equals("groupchat")) nick = jid.getResource();
				else nick = getNick(jid); 
				lblName.setText(nick);
				label.setText(e.getBody());
				place();
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
	}
	
	private String getNick(JID jid)
	{
		String nick = jid.getUser();
		JIDStatus jidStatus = Backend.getJIDStatus(jid);
		if(jidStatus!=null) nick = jidStatus.getNick();
		return nick;
	}
	
	public void demo(Color color,Color foreground,final int alpha,final int time)
	{
		this.time = time *1000;
		label.setBackground(color);
		lblName.setBackground(color);
		label.setForeground(foreground);
		lblName.setForeground(foreground);
		plugin.time = this.time;
		plugin.label.setBackground(color);
		plugin.lblName.setBackground(color);
		plugin.label.setForeground(foreground);
		plugin.lblName.setForeground(foreground);
		plugin.alpha=alpha;
		loadWindowsUtils();
		if(util!=null && supportsAlpha)
		{
			try
			{
				util.setWindowAlpha(this,alpha);
				util.setWindowAlpha(plugin,alpha);
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
			}
		}
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				lblName.setText(I18N.gettext("alertwindow.This_is_a_Demonstration_PopupWindow"));
				label.setText(MessageFormat.format(I18N.gettext("alertwindow.Alpha_is_{0},_Time_is_{1}"),new Object[]{String.valueOf(alpha),String.valueOf(time)}));
				place();
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
	}

	private void place()
	{
		if(tt!=null) tt.cancel();
		tt = (new TimerTask()
		{
			public void run()
			{
				setSize(1,1);
				setLocation(pntLowerRight);
				if(util!=null && supportsAlpha && alpha <100)
				{//remove alpha because otherwise half of the window becomes black	
					try
					{
						util.setWindowAlpha(Plugin.this,100);
					}
					catch (Throwable ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		pack();
		//int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		//int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		//setLocation(screenX-getWidth(),screenY-getHeight()-40);
		setLocation(pntLowerRight.x-getWidth(),pntLowerRight.y-getHeight());
			
		//toFront();
		//show();
		
		timer.schedule(tt,time);
		if(util!=null && supportsAlpha && alpha<100)
		{	
			try
			{
				util.setWindowAlpha(this,alpha);
			}
			catch (Throwable ex)
			{
				ex.printStackTrace();
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
