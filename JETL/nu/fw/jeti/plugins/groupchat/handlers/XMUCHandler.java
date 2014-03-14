package nu.fw.jeti.plugins.groupchat.handlers;

import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.groupchat.elements.IQMUC;
import nu.fw.jeti.plugins.groupchat.elements.XMUCUser;
import nu.fw.jeti.util.Log;


/**
 * @author Martin Forssen
 */
public abstract class XMUCHandler extends ExtensionHandler
{
    private XData xdata;
    private List items;
    private String reason;
    private int errorCode;
    private String errorDescription;

    public void startHandling(org.xml.sax.Attributes  attr) {
        reset();
    }

    private void reset() {
        xdata = null;
        items = null;
    }


    public void startElement(String name, org.xml.sax.Attributes attr) {
        if (name.equals("item")) {
            if(items==null) {
                items = new LinkedList();
            }
            try {
                JID jid = JID.checkedJIDFromString(attr.getValue("jid"));
                items.add(new XMUCUser(attr.getValue("affiliation"),
                                       attr.getValue("role"),
                                       null, jid, 0, null));
            } catch (InstantiationException e) {
                Log.xmlParseException(e);
            }
        } else if (name.equals("error")) {
            try {
                errorCode = Integer.parseInt(attr.getValue("code"));
            } catch (NumberFormatException e) {
                Log.notParsedXML("XMUC#Handler " + name + getText());
            }
        }
    }

    public void endElement(String name) {
        if (name.equals("reason")) {
            reason = getText();
        } else if (name.equals("error")) {
            errorDescription = getText();
        }
        clearCurrentChars();
    }

    public void addExtension(Extension extension) {
        if(extension instanceof XData) {
            xdata = (XData) extension;
        }
    }
  
    abstract public String getInstance();
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
        Extension e;
        if (xdata != null) {
            e = new IQMUC(getInstance(), xdata);
        } else {
            e = new IQMUC(getInstance(), items, errorCode, errorDescription);
        }
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
