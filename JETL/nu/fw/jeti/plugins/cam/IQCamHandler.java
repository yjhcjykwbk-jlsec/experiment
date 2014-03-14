// Created on 14-sep-2003
package nu.fw.jeti.plugins.cam;



import org.xml.sax.Attributes;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.util.Log;

/**
 * @author E.S. de Boer
 *
 */
public class IQCamHandler extends ExtensionHandler
{
	private IQXOOB oob;
	private int refresh;


	public void startHandling(Attributes attr)
	{
		oob = null;
		try {
			refresh = Integer.parseInt(attr.getValue("refresh"));
		}
		catch (NumberFormatException e)
		{
			Log.notParsedXML("IQcam refresh is not a number");
		}
	}

//	public void endElement(String name)
//	{
//		if (name.equals("url")) url = getText();
//		else if (name.equals("desc")) description  = getText();
//		else nu.fw.jeti.util.Log.notParsedXML("OOB" + name + getText());
//		clearCurrentChars();
//	}

	public void addExtension(Extension extension)
	{//overide for extension embedded in extension
		if(extension instanceof IQXOOB )
		{
			oob = (IQXOOB)extension; 
		}
	    else Log.notParsedXML("Extension not supported by IQcam");
	}


	public Extension build()
	{
		if(refresh <1) refresh = 60;
		return new IQCam(oob,refresh); 
	}

	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
