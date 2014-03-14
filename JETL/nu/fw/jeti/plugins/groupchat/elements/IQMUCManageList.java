package nu.fw.jeti.plugins.groupchat.elements;

import java.awt.event.WindowEvent;

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
public abstract class IQMUCManageList extends IQMUC implements IQMUCInterface
{
    protected String instance;
    protected String attribute;
    protected String value;

    protected IQMUCManageList(Backend backend, JID roomJID,
                            boolean aff, int value) {
        super(backend, roomJID);
        initInstance(aff, value);

        if (aff) {
            attribute = "affiliation";
            this.value = XMUCUser.getProtocolStringAffiliation(value);
        } else {
            attribute = "role";
            this.value = XMUCUser.getProtocolStringRole(value);
        }
    }

    private void initInstance(boolean aff, int value) {
        if (aff && (XMUCUser.OWNER == value || XMUCUser.ADMIN == value)) {
            instance = "owner";
        } else {
            instance = "admin";
        }
    }

    public void timeout() {};
}	

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
