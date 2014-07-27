package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.JID;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author Martin Forssen
 *
 */
public class XMUCUserInvite extends Extension implements XExtension
{
    private JID user;
    private String reason;

    public XMUCUserInvite(JID user, String reason) {
        this.user = user;
        this.reason = reason;
    }
	
    public void appendToXML(StringBuffer xml)
    {
        xml.append("<x xmlns='http://jabber.org/protocol/muc#user'>");
        xml.append("<invite");
        appendAttribute(xml, "to", user);
        xml.append(">");
        appendElement(xml, "reason", reason);
        xml.append("</invite>");
        xml.append("</x>");
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
