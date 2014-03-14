// Created on 28-apr-2003
package nu.fw.jeti.plugins.groupchat.handlers;



import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.groupchat.elements.XConference;

/**
 * @author E.S. de Boer
 *
 */
public class XConferenceHandler extends ExtensionHandler
{
	private JID jid;
//
	public void startHandling(org.xml.sax.Attributes  attr)
	{
		reset();
		try{
			jid = JID.checkedJIDFromString(attr.getValue("jid"));
		}catch (InstantiationException e) {nu.fw.jeti.util.Log.xmlParseException(e);}
	}

	private void reset()
	{
		jid = null;
	}

//	public void startElement(String name, Attributes attr)
//	{
//		if (name.equals("body"))
//			return;
//		body += "<" + name + " ";
//		for (int i = 0; i < attr.getLength(); i++)
//		{
//			body += attr.getQName(i) + "=\"" + attr.getValue(i) + "\"";
//		}
//		body += ">";
//	}
//
//	public void endElement(String name)
//	{
//		if (!name.equals("body"))
//			body += getText() + "</" + name + ">";
//		//		if("from".equals(name)) from = JID.jidFromString(getText());
//		//		else if("stamp".equals(name)) stamp = getText();
//		//		else util.Log.notParsedXML("x:delay " + name + getText());
//		clearCurrentChars();
//	}

	public Extension build()
	{
		Extension e = new XConference(jid);
		reset();
		return e;
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
