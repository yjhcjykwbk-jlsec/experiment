package nu.fw.jeti.jabber.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.util.I18N;

import org.xml.sax.Attributes;

/**
 * Created on 1-mrt-2003
 * @author E.S. de Boer
 *
 */
public class IQXOOBHandler extends ExtensionHandler
{
	private String url;
	private String description;


	public void startHandling(Attributes attr)
	{
		url = null;
		description = null;
	}

	public void endElement(String name)
	{
		if (name.equals("url")) url = getText();
		else if (name.equals("desc")) description  = getText();
		else nu.fw.jeti.util.Log.notParsedXML("OOB" + name + getText());
		clearCurrentChars();
	}



	public Extension build() throws InstantiationException
	{
		try
		{
			return new IQXOOB(new URL(url),description);
		}
		catch (MalformedURLException e)
		{
			throw new InstantiationException(I18N.gettext("main.error.invalid_url_in_OOB_packet")); 
		}
	}

	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
