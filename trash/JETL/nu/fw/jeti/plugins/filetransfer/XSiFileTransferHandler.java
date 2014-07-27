// Created on 20-okt-2004
package nu.fw.jeti.plugins.filetransfer;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.elements.XMPPError;
import nu.fw.jeti.jabber.elements.XMPPErrorTag;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 */
public class XSiFileTransferHandler extends ExtensionHandler
{//TODO range
	private String name;
	private String hash;
	private String date;
	private String description;
	private long size;
	private int length;
	private long offset;
		
	public void startHandling(Attributes attr)
	{
		name=attr.getValue("name");
		hash=attr.getValue("hash");
		date=attr.getValue("date");
		try
		{
			size=Long.parseLong(attr.getValue("size"));
		}
		catch(NumberFormatException e)
		{
			size=0;
			Log.xmlReceivedError("Si profile, size is not a number");
		}
		length=0;
		offset=0;
	}
	
	public void startElement(String name,Attributes attr)
	{
		if(name.equals("range"))
		{
			try
			{
				length=Integer.parseInt(attr.getValue("length"));
				offset=Long.parseLong(attr.getValue("offset"));
			}
			catch(NumberFormatException e)
			{
				size=0;
				Log.xmlReceivedError("Si profile, range is not a number");
			}
		}
	}
		
	public void endElement(String name)
	{
		if(name.equals("desc"))description = getText();
		clearCurrentChars();
	}

	public Extension build()
	{
		return new XSiFileTransfer(name,hash,date,size,description,length,offset);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
