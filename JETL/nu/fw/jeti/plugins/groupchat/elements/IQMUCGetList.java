package nu.fw.jeti.plugins.groupchat.elements;

import java.awt.event.WindowEvent;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.XDataCallback;
import nu.fw.jeti.jabber.XDataPanel;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.plugins.groupchat.events.UserListener;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author Martin Forssen
 */
public class IQMUCGetList extends IQMUCManageList implements IQMUCInterface
{
    private UserListener listener;

    // Get data
    public IQMUCGetList(Backend backend, JID roomJID,
                           boolean aff, int value, UserListener listener) {
        super(backend, roomJID, aff, value);
        this.listener = listener;
        send("get",this);
    }

    public void execute(InfoQuery iq,Backend backend, IQMUC muc)
    {
        if (iq.getType().equals("result")
            && muc.getItems() != null) {
            listener.userResult(muc.getItems());
        } else if (iq.getType().equals("error")) {
            Popups.errorPopup(iq.getErrorDescription(),
                              "Code: " + iq.getErrorCode());
        }
    }

    public void appendToXML(StringBuffer xml) {
        xml.append("<query xmlns='http://jabber.org/protocol/muc#"
                   + instance + "'>");
        xml.append("<item");
        appendAttribute(xml, attribute, value);
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
