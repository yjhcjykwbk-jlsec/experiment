package nu.fw.jeti.plugins.servertolog;

import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.InfoQuery;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class Plugin implements StatusChangeListener
{
	public final static String NAME = "servertolog";
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "gets the logs from the server";
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String ABOUT = "by E.S. de Boer";
	private static Plugin plugin;
	private Backend backend;

	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}
	
	public Plugin(Backend backend)
	{
		this.backend = backend;
		backend.addExtensionHandler("jeti:serverlog",new ServerLogHandler());
		backend.addListener(StatusChangeListener.class,this);
	}

	public void unload()
	{}

	public static void unload(Backend backend)
	{
		backend.removeExtensionHandler("jeti:serverlog");
		backend.removeListener(StatusChangeListener.class,plugin);
	}
			
	
	/*-----------------StatusChangeEvents---------------------------*/
	public void connectionChanged(boolean online)
	{
		if (online) backend.send(new InfoQuery("get",new IQPrivate(new ServerLogExtension())));
	}
	
	public void exit() {}

	public void ownPresenceChanged(int a,String b) {}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
