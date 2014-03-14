package nu.fw.jeti.jabber.handlers;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQRegister;
import nu.fw.jeti.jabber.elements.XData;

import org.xml.sax.Attributes;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQRegisterHandler extends ExtensionHandler
{
	private boolean remove=false;
	private Map fields;
	private XData xdata;//xdata

	public void startHandling(Attributes attr)
	{
		reset();
	}

	private void reset()
	{
		remove=false;
		fields =null;
		xdata = null;
	}

	public void endElement(String name)
	{//parse everything
		if("remove".equals(name)) remove =true;
		else
		{
			if(fields == null) fields = new LinkedHashMap(14);
			fields.put(name,getText());
		}
		//else util.Log.notParsedXML("iq:auth " + name + getText());
		clearCurrentChars();
	}
	
	public void addExtension(Extension extension)
	{
		if(extension instanceof XData) xdata = (XData) extension;
	}

	public Extension build()
	{
		Extension e = null;
		if(xdata != null)
		{
			e = new IQRegister(remove,fields,xdata);
		}
		else e = new IQRegister(remove,fields);
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
