// Created on 14-sep-2003
package nu.fw.jeti.plugins.cam;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.jabber.elements.InfoQuery;

/**
 * @author E.S. de Boer
 *
 */
public class IQCam extends Extension implements IQExtension
{
	private IQXOOB oob;
	private int refresh;

	public IQCam(IQXOOB oob,int refresh)
	{
		this.oob = oob;
		this.refresh = refresh;
	}

	public IQXOOB getOob()
	{
		return oob;
	}

	public int getRefesh()
	{
		return refresh;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("set"))
		{
			//if(Start.isPluginLoaded("cam"))
			//{
		//		((nu.fw.jeti.plugins.Cam)Start.newPluginInstance("cam")).init(cam.getOob(),cam.getRefesh());
		//	} 
			new nu.fw.jeti.plugins.cam.Plugin().init(getOob(),getRefesh());
		}
		else if (iq.getType().equals("error"))
		{
			System.err.println(iq.getErrorDescription());
		}
	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns='http://jeti.tk/cam'");
		appendAttribute(xml,"refresh",String.valueOf(refresh));
		xml.append(">");
		oob.appendToXML(xml);
		xml.append("</query>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
