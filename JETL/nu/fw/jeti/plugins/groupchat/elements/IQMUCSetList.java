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
public class IQMUCSetList extends IQMUCManageList implements IQMUCInterface
{
    private Vector toChange;
    private JID jid;
    private String nick;
    private IQMUCSetListListener listener;
    private boolean aff;
    private int intValue;
    private String reason;

    public IQMUCSetList(Backend backend, JID roomJID,
                        boolean aff, int value, Vector toChange,
                        IQMUCSetListListener listener) {
        super(backend, roomJID, aff, value);
        this.toChange = toChange;
        this.listener = listener;
        this.aff = aff;
        this.intValue = value;
        send("set",this);
    }

    public IQMUCSetList(Backend backend, JID roomJID,
                        boolean aff, int value, JID jid, String reason) {
        super(backend, roomJID, aff, value);
        this.jid = jid;
        this.aff = aff;
        this.intValue = value;
        this.reason = reason;
        send("set",this);
    }

    public IQMUCSetList(Backend backend, JID roomJID,
                        boolean aff, int value, String nick, String reason) {
        super(backend, roomJID, aff, value);
        this.nick = nick;
        this.aff = aff;
        this.intValue = value;
        this.reason = reason;
        send("set",this);
    }

    public void execute(InfoQuery iq,Backend backend, IQMUC muc)
    {
        if (iq.getType().equals("result") && listener != null) {
            listener.listSetOk(toChange, aff, intValue);
        } else if (iq.getType().equals("error")) {
            Popups.errorPopup(muc.getErrorDescription(),
                              I18N.gettext("groupchat.Forbidden"));
        }
    }

    public void appendToXML(StringBuffer xml) {
        xml.append("<query xmlns='http://jabber.org/protocol/muc#"
                   + instance + "'>");
        if (toChange != null) {
            for (int i=0; i<toChange.size(); i++) {
                xml.append("<item");
                appendAttribute(xml, "jid", toChange.get(i));
                appendAttribute(xml, attribute, value);
                xml.append("/>");
            }
        } 
        if (jid != null || nick != null) {
            xml.append("<item");
            if (jid != null) {
                appendAttribute(xml, "jid", jid.toStringNoResource());
            } else {
                appendAttribute(xml, "nick", nick);
            }
            appendAttribute(xml, attribute, value);
            xml.append(">");
            appendElement(xml, "reason", reason);
            xml.append("</item>");
        }
        xml.append("</query>");
    }
}	

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
