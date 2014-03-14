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

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;

/**
 * @author E.S. de Boer
 *
 */
public class IQSi extends Extension implements IQExtension
{
	private String mimeType;
	private String id;
	private String profile;
	private XData form;
	private XSiFileTransfer siprofile;

	public IQSi(XData form, XSiFileTransfer siprofile)
	{
		this.form = form;
		this.siprofile = siprofile;
	}
	
	public IQSi(String id, String profile,String mimeType, XData form, XSiFileTransfer siprofile)
	{
		this.mimeType = mimeType;
		this.id = id;
		this.profile = profile;
		this.form = form;
		this.siprofile = siprofile;
	}
		
	public XData getXDataForm()
	{
		return form;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getMimeType()
	{
		return mimeType;
	}
	
	public String getProfile()
	{
		return profile;
	}
	
	public XSiFileTransfer getSiprofile()
	{
		return siprofile;
	}
	
	
	public void execute(InfoQuery iq, Backend backend)
	{
		if(iq.getType().equals("set"))
		{
			new GetFileWindow(backend,iq);
		}
	}
	
	public void appendToXML(StringBuffer xml)
	{
		xml.append("<si xmlns='http://jabber.org/protocol/si'");
		appendAttribute(xml,"id",id);
		appendAttribute(xml,"mime-type",mimeType);
		appendAttribute(xml,"profile",profile);
		xml.append(">");
		if(siprofile!=null)
		{
			siprofile.appendToXML(xml);
		}
		if(form!=null)
		{
			xml.append("<feature xmlns='http://jabber.org/protocol/feature-neg'>");
			form.appendToXML(xml);
			xml.append("</feature>");
		}
		xml.append("</si>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
