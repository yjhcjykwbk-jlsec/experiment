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

// Created on 21-nov-2004
package nu.fw.jeti.plugins.vcard;

import java.util.List;
import java.util.Map;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;

/**
 * @author E.S. de Boer
 *
 */
public class VCard extends Extension implements IQExtension
{
	private Map personal;
	private Map business;
	private List homeTels;
	private List workTels;

	public VCard()
	{
	}
	
	public VCard(Map personal, Map business, List homeTels, List workTels)
	{
		this.personal = personal;
		this.business = business;
		this.homeTels = homeTels;
		this.workTels = workTels;
	}
		
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			String nick;
			JID jid = iq.getFrom();
			if(jid.equals(backend.getMyJID()))
			{
				new VCardEdit(personal,business,homeTels,workTels,backend);
			}
			else
			{
				JIDStatus j = backend.getJIDStatus(jid);
				if(j!=null) nick = j.getNick();
				else nick = jid.toStringNoResource();
				new VCardDisplay(personal,business,homeTels,workTels,nick);
			}
		}
		else if (iq.getType().equals("error"))
		{
			System.err.println(iq.getErrorDescription());
		}
	}

	public void appendToXML(StringBuffer xml)
	{
		if(personal==null)
		{
			xml.append("<vCard xmlns='vcard-temp'/>");
			return;
		}
		else xml.append("<vCard xmlns='vcard-temp'>");
		appendPersonal(xml);
		appendBusiness(xml);
		//appendAttribute(xml,"refresh",String.valueOf(refresh));
		//xml.append(">");
		//oob.appendToXML(xml);
		xml.append("</vCard>");
	}
	
	private void appendPersonal(StringBuffer xml)
	{
		appendPersonalX(xml,"FN");
		xml.append("<N>");
		appendPersonalX(xml,"FAMILY");
		appendPersonalX(xml,"GIVEN");
		appendPersonalX(xml,"MIDDLE");
		xml.append("</N>");
		appendPersonalX(xml,"NICKNAME");
		appendPersonalX(xml,"BDAY");
		appendPersonalX(xml,"JABBERID");
		if(personal.get("LON")!=null)
		{
			xml.append("<GEO>");
			appendPersonalX(xml,"LAT");
			appendPersonalX(xml,"LON");
			xml.append("</GEO>");
		}
		String[] photo = (String[]) personal.get("PHOTO");
		if(photo!=null)
		{
			xml.append("<PHOTO>");
			appendAttribute(xml,"VALUE",photo[0]);
			appendElement(xml,"BINVAL",photo[1]);
			xml.append("</PHOTO>");
		}
		xml.append("<ADR>");
		xml.append("<HOME/>");
		appendPersonalX(xml,"POBOX");
		appendPersonalX(xml,"EXTADR");
		appendPersonalX(xml,"STREET"); 
		appendPersonalX(xml,"LOCALITY");
		appendPersonalX(xml,"REGION");
		appendPersonalX(xml,"PCODE");
		appendPersonalX(xml,"CTRY");
		xml.append("</ADR>");
		//		TODO tel
		String email =(String) personal.get("EMAIL");
		if(email!=null)
		{
			xml.append("<EMAIL>");
			xml.append("<HOME/>");
			appendElement(xml,"USERID",email);
			xml.append("</EMAIL>");
		}
		
		appendPersonalX(xml,"URL");
		appendPersonalX(xml,"DESC");
				
	}
		
	private void appendPersonalX(StringBuffer xml,String x)
	{
		appendElement(xml,x,personal.get(x));
	}
	
	private void appendBusiness(StringBuffer xml)
	{
		appendBusinessX(xml,"TITLE");
		appendBusinessX(xml,"ROLE");
		appendBusinessX(xml,"ORGNAME"); 	
		appendBusinessX(xml,"ORGUNIT");
		
		String[] logo = (String[]) business.get("LOGO");
		if(logo!=null)
		{
			xml.append("<LOGO>");
			appendAttribute(xml,"VALUE",logo[0]);
			appendElement(xml,"BINVAL",logo[1]);
			xml.append("</LOGO>");
		}
		
		xml.append("<ADR>");
		xml.append("<WORK/>");
		appendBusinessX(xml,"POBOX");
		appendBusinessX(xml,"EXTADR");
		appendBusinessX(xml,"STREET"); 
		appendBusinessX(xml,"LOCALITY");
		appendBusinessX(xml,"REGION");
		appendBusinessX(xml,"PCODE");
		appendBusinessX(xml,"CTRY");
		xml.append("</ADR>");
		//TODO tel
		String email =(String) business.get("EMAIL");
		if(email!=null)
		{
			xml.append("<EMAIL>");
			xml.append("<WORK/>");
			appendElement(xml,"USERID",email);
			xml.append("</EMAIL>");
		}
	}
	
	private void appendBusinessX(StringBuffer xml,String x)
	{
		appendElement(xml,x,business.get(x));
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
