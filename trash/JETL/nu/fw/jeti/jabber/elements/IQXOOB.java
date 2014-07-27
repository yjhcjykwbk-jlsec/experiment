package nu.fw.jeti.jabber.elements;

import java.net.URL;
import java.util.Iterator;



import nu.fw.jeti.events.ErrorListener;
import nu.fw.jeti.events.OOBListener;
import nu.fw.jeti.jabber.Backend;

/**
 * Created on 1-mrt-2003
 * @author E.S. de Boer
 *
 */
public class IQXOOB extends Extension implements IQExtension
{
	String description;
	URL url;

	public IQXOOB(URL url,String description)
	{
		this.description = description;
		this.url = url;
	}

	public URL getURL()
	{
		return url;
	}

	public String getDescription()
	{
		return description;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("set"))
		{
			for (Iterator j = backend.getListeners(OOBListener.class); j.hasNext();)
			{
				((OOBListener) j.next()).oob(iq.getFrom(),iq.getID(),this);
			}
			
		}
		else if (iq.getType().equals("error"))
		{
			for (Iterator j = backend.getListeners(ErrorListener.class); j.hasNext();)
			{
				((ErrorListener) j.next()).error(iq.getErrorCode(),iq.getErrorDescription());
			}
		}
	}

	public void appendToXML(StringBuffer xml)
	{
		/** @todo x */
		xml.append("<query xmlns=\"jabber:iq:oob\">");
		if (url != null)
			xml.append("<url>" + url  + "</url>");
		if (description != null)
			xml.append("<desc>" + description + "</desc>");
		xml.append("</query>");
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
