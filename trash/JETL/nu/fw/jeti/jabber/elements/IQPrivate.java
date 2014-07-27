package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.Backend;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQPrivate extends Extension implements IQExtension
{
	private IQXExtension extension;

	// public IQPrivate(){}

	public IQPrivate(IQXExtension extension)
	{
		this.extension = extension;
	}

//	public Extension getExtension()
//	{
//		return extension;
//	}

	public void execute(InfoQuery iq, Backend backend)
	{
		if(extension!=null)extension.execute(iq,backend);
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns=\"jabber:iq:private\"");
		if (extension == null)
		{ //short cut
			xml.append("/>");
			return;
		}
		xml.append('>');
		((Extension)extension).appendToXML(xml);
		xml.append("</query>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
