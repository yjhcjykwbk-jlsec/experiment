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
public class IQMUCOwnerDestroy extends IQMUC implements IQMUCInterface
{
    private JID room;
    private String reason;

    public IQMUCOwnerDestroy(Backend backend, JID roomJID, String reason) {
        super("owner", backend, roomJID, null);
        this.room = roomJID;
        this.reason = reason;
        send("set", this);
    }

    public void execute(InfoQuery iq,Backend backend, IQMUC muc)
    {
        if (iq.getType().equals("error")) {
            Popups.errorPopup(iq.getErrorDescription(), "Destroy failed");
        }
    }

    public void timeout() {};

    public void appendToXML(StringBuffer xml) {
        xml.append("<query xmlns= 'http://jabber.org/protocol/muc#owner'>");
        xml.append("<destroy");
        appendAttribute(xml,"jid",room);
        xml.append(">");
        appendElement(xml, "reason", reason);
        xml.append("</destroy>");
        xml.append("</query>");
    }
}	

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
