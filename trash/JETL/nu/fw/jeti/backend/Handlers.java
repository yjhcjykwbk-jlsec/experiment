package nu.fw.jeti.backend;

import java.util.HashMap;

import nu.fw.jeti.jabber.handlers.*;

/**
 * @author E.S. de Boer
 */

public class Handlers
{
	private HashMap handlers;
	private InfoQueryHandler iqHandler;
	private PresenceHandler presenceHandler;
	private MessageHandler messageHandler;

//use class.forname so extensions could also be loaded
//use connect so they can load other things if they want, usefull?
//look at java language spec for loading proces ivm netwerk load
    public Handlers()
    {
		handlers = new HashMap(10);
		iqHandler = new InfoQueryHandler();
		presenceHandler  = new PresenceHandler();
		messageHandler = new MessageHandler();
		handlers.put("jabber:iq:auth",new IQAuthHandler());
		handlers.put("jabber:iq:roster",new RosterHandler());
		handlers.put("jabber:iq:time",new IQTimeHandler());
		handlers.put("jabber:iq:version",new IQVersionHandler());
		handlers.put("jabber:iq:register",new IQRegisterHandler());
		handlers.put("j2m:prefs",new JetiPrivateHandler());
		handlers.put("jeti:rosterprefs",new JetiPrivateRosterHandler());
		handlers.put("jabber:iq:private",new IQPrivateHandler());
		handlers.put("jabber:x:event",new XMessageEventHandler());
		handlers.put("jabber:x:delay",new XDelayHandler());
		handlers.put("jabber:iq:browse",new IQBrowseHandler());
		handlers.put("jabber:iq:last",new IQLastHandler());
		handlers.put("jabber:iq:oob",new IQXOOBHandler());
		handlers.put("http://jabber.org/protocol/disco#info",new IQDiscoInfoHandler());
		handlers.put("http://jabber.org/protocol/disco#items",new IQDiscoItemsHandler());
		handlers.put("jabber:x:data",new XDataHandler());
		handlers.put("urn:ietf:params:xml:ns:xmpp-stanzas",new XMPPErrorHandler());
		handlers.put("unknown",new UnknownExtensionHandler());
    }

	public PacketHandler getPacketHandler(String namespace)
	{//iq message & presence
		if ("iq".equals(namespace)) return iqHandler;
		else if ("presence".equals(namespace)) return presenceHandler;
		else if("message".equals(namespace)) return messageHandler;
	    else return null;
	}

	public synchronized ExtensionHandler getExtensionHandler(String namespace)
	{
		return (ExtensionHandler) handlers.get(namespace);
	}
	
	public synchronized void addExtensionHandler(String namespace,ExtensionHandler handler)
	{
		handlers.put(namespace,handler);
	}
	
	public synchronized void removeExtensionHandler(String namespace)
	{
		handlers.remove(namespace);
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
