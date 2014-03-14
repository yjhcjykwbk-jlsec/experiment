/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 */

// Created on 20-okt-2004
package nu.fw.jeti.plugins.filetransfer;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 */
public class XSiFileTransfer extends Extension implements XExtension
{
	private String name;
	private String hash;
	private String date;
	private String description;
	private long size;
	private int length;
	private long offset;

	public XSiFileTransfer(String name, String hash, String date, long size,String description,int length,long offset)
	{
		this.name = name;
		this.hash = hash;
		this.date = date;
		this.size = size;
		this.description=description;
	}
	
	public String getHash()
	{
		return hash;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public long getOffset()
	{
		return offset;
	}
	
	public long getSize()
	{
		return size;
	}
		
	public void appendToXML(StringBuffer xml)
	{
		xml.append("<file xmlns='http://jabber.org/protocol/si/profile/file-transfer'");
		appendAttribute(xml,"name",name);
		appendAttribute(xml,"hash",hash);
		appendAttribute(xml,"date",date);
		appendAttribute(xml,"size",String.valueOf(size));
		xml.append(">");
		appendElement(xml,"desc",description);
		if(length>0)
		{
			xml.append("<range");
			appendAttribute(xml,"lengt",String.valueOf(length));
			appendAttribute(xml,"offset",String.valueOf(offset));
			xml.append("/>");
		}
		xml.append("</file>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
