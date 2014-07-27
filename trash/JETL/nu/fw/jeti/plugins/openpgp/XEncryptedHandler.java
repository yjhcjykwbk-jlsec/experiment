// Created on 29-sep-2004
package nu.fw.jeti.plugins.openpgp;



import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 */
public class XEncryptedHandler extends ExtensionHandler
{
	private String signed;

	public void startHandling(Attributes attr)
	{
		signed=null;
	}
		
	public Extension build()
	{
		signed = getUntrimmedText().toString();
		System.out.println("sign" + signed);
		clearCurrentChars();
		return new XEncrypted(signed); 
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
