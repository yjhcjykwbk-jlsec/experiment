package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author Martin Forssen
 *
 */
public class XMUCPassword extends Extension implements XExtension
{
    private String password;

    public XMUCPassword(String password) {
        this.password = password;
    }
	
    public void appendToXML(StringBuffer xml)
    {
        xml.append("<x xmlns= 'http://jabber.org/protocol/muc'>");
        appendElement(xml, "password", password);
        xml.append("</x>");
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
