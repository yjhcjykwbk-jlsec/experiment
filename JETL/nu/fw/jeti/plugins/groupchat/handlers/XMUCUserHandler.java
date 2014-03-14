// Created on 7-okt-2003
package nu.fw.jeti.plugins.groupchat.handlers;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUser;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 */
public class XMUCUserHandler extends ExtensionHandler
{
	private JID jid;
	private String nick;
	private String affiliation;
	private String role;
	private int statuscode;
    private String reason;
	
	public void startHandling(Attributes attr)
	{//no attrs
		jid=null;
		nick=null;
		affiliation = null;
		role = null;
		statuscode=0;
        reason = null;
	}

	public void startElement(String name,Attributes attr)
	{
		if(name.equals("item"))
		{
			try
			{
				jid = JID.checkedJIDFromString(attr.getValue("jid"));
			}
			catch (InstantiationException e)
			{
				Log.notParsedXML("XMUC#User " + name + getText());
			}
			affiliation = attr.getValue("affiliation");
			role = attr.getValue("role");
			nick = attr.getValue("nick");
		}
		else if(name.equals("status"))
		{
			try {
				statuscode = Integer.parseInt(attr.getValue("code"));
			}catch (NumberFormatException e)
			{
				Log.notParsedXML("XMUC#User " + name + getText());
			}
		}
		else Log.notParsedXML("XMUC#User " + name + getText());
	}

	public void endElement(String name)
	{
        if(name.equals("reason"))
		{
            reason = getText();
		}
		clearCurrentChars();
	}

	public Extension build()
	{
        return new XMUCUser(affiliation,role,nick,jid,statuscode,reason);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
