// Created on 9-okt-2004
package nu.fw.jeti.plugins.openpgp;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 */
public class XEncrypted extends Extension implements XExtension
{
	private String encrypted;

	public XEncrypted(String crypted)
	{
		this.encrypted = crypted;
	}

	public String getCrypted()
	{
		return encrypted;
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns='jabber:x:encrypted'>");
		xml.append(encrypted);
		xml.append("</x>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
