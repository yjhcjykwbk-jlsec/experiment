// Created on 20-okt-2004
package nu.fw.jeti.plugins.filetransfer;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.elements.XMPPErrorTag;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 */
public class IQSiHandler extends ExtensionHandler
{
	private String mimeType;
	private String id;
	private String profile;
	private XData form;
	private XSiFileTransfer siprofile;
	
	public void startHandling(Attributes attr)
	{
		mimeType=attr.getValue("mime-type");
		id=attr.getValue("id");
		profile=attr.getValue("profile");
		form=null;
		siprofile=null;
	}
	
	public void addExtension(Extension extension)
	{
		if(extension instanceof XData)
		{
			form = (XData)extension;
		}
		else if(extension instanceof XSiFileTransfer)siprofile = (XSiFileTransfer)extension;
		else Log.notParsedXML("Si Extension not known" + extension);
	}
//	public void endElement(String name)
//	{
//		System.out.println(name);
//		data = getText();
//		clearCurrentChars();
//	}

	public Extension build()
	{
		if(!getName().equals("si"))return new XMPPErrorTag(getName(),"http://jabber.org/protocol/si");
		return new IQSi(id,profile,mimeType,form,siprofile);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
