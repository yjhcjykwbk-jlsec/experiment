// Created on 26-apr-2003
package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 */
public class XMUC extends Extension implements XExtension
{
	public XMUC()
	{}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns= 'http://jabber.org/protocol/muc'");
		xml.append("/>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
