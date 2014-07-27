package nu.fw.jeti.jabber.handlers;

import org.xml.sax.Attributes;

import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Log;
import nu.fw.jeti.jabber.elements.Extension;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class UnknownExtensionHandler extends ExtensionHandler
{
	private StringBuffer xmlText = new StringBuffer();

	public void startHandling(Attributes attr)
	{
		xmlText = new StringBuffer();
		xmlText.append("<");
		xmlText.append(getName());
		if (attr != null) {
			String aName = null;
			for (int i = 0; i < attr.getLength(); i++) {
				aName = attr.getQName(i);
				xmlText.append(" ");
				xmlText.append(aName+"=\""+attr.getValue(i)+"\"");
			}
		}
		xmlText.append(">");
	}

	public void startElement(String name,Attributes attr)
	{
		xmlText.append("<");
		xmlText.append(name);
		if (attr != null) {
			String aName = null;
			for (int i = 0; i < attr.getLength(); i++) {
				aName = attr.getQName(i);
				xmlText.append(" ");
				xmlText.append(aName+"=\""+attr.getValue(i)+"\"");
			}
		}
		xmlText.append(">");
	}

	public void endElement(String name)
	{
		xmlText.append(getText());
		xmlText.append("</");
		xmlText.append(name);
		xmlText.append(">");

		clearCurrentChars();
	}

	public Extension build() throws InstantiationException
	{
		xmlText.append("</");
		xmlText.append(getName());
		xmlText.append(">");
		Log.notParsedXML(xmlText.toString());
		throw new InstantiationException(I18N.gettext("main.error.Unknown_Extension"));
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
