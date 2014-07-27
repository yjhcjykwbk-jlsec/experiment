// Created on 9-okt-2004
package nu.fw.jeti.plugins.openpgp;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 */
public class XSigned extends Extension implements XExtension
{
	private String signed;

	public XSigned(String signed)
	{
		this.signed = signed;
	}

	public String getSigned()
	{
		return signed;
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns='jabber:x:signed'>");
		xml.append(signed);
		xml.append("</x>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
