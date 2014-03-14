package nu.fw.jeti.plugins.groupchat.elements;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.XDataCallback;
import nu.fw.jeti.jabber.XDataPanel;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author Martin Forssen
 */
public class IQMUCManage extends Extension implements IQExtension
{
    public void execute(InfoQuery iq,Backend backend)
    {
        System.err.println("Execute");
    }

    public void appendToXML(StringBuffer xml)
    {
        xml.append("<query xmlns= 'http://jabber.org/protocol/muc#owner'>");
        xml.append("<item");
        appendAttribute(xml,"affiliation","owner");
        xml.append("/>"); 
        xml.append("</query>");
    }
}	

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
