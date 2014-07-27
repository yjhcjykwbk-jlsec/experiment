package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQPrivate;
import nu.fw.jeti.jabber.elements.IQXExtension;

import org.xml.sax.Attributes;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQPrivateHandler extends ExtensionHandler
{
	private Extension extension;

	public void startHandling(Attributes attr)
	{
		reset();
	}

	private void reset()
	{
		extension = null;

	}

	public void addExtension(Extension extension)
	{
		this.extension = extension;
	}

	public Extension build()
	{
		Extension e = new IQPrivate((IQXExtension)extension);
		reset();
		return e;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
