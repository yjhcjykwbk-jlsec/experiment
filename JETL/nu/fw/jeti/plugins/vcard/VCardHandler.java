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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 */
public class VCardHandler extends ExtensionHandler
{
	private Map personal = new HashMap(20);
	private Map business = new HashMap(20);
	private List homeTels = new LinkedList();
	private List workTels = new LinkedList();
	//private List list = new LinkedList();
	private static int UNKNOWN=0;
	private static int HOME=1;
	private static int WORK=2;
	private static int PHOTO=3;
	private static int LOGO=4;
	private int current=UNKNOWN;
	private boolean homeAdresKnown=false;
	private String[] telephoneNumber;
	private String[] photo;
	private String[] logo;

	public void startHandling(Attributes attr)
	{
		//list.clear();
		personal.clear();
		business.clear();
		homeTels.clear();
		workTels.clear();
	}
	
	public void startElement(String name,Attributes attr)
	{
		if(name.equals("TEL")) telephoneNumber = new String[3];
		else if(name.equals("HOME"))
		{
			current=HOME;
		}
		else if(name.equals("WORK"))
		{
			current=WORK;
		}
		else if(name.equals("VOICE")	|| name.equals("FAX")
				|| name.equals("PAGER")	|| name.equals("MSG")
				|| name.equals("CELL")	|| name.equals("VIDEO")
				|| name.equals("BBS")	|| name.equals("MODEM")
				|| name.equals("ISDN")	|| name.equals("PCS")){
			if(telephoneNumber!=null)telephoneNumber[1]=name;
		}
		else if(name.equals("PREF"))
		{
			if(telephoneNumber!=null)telephoneNumber[2]="PREF";
		}
		else if (name.equals("PHOTO"))
		{
			photo= new String[2];
			current=PHOTO;
			photo[0] = attr.getValue("TYPE");
		}
		else if (name.equals("LOGO"))
		{
			logo= new String[2];
			current=LOGO;
			logo[0] = attr.getValue("TYPE");
		}
	}

	public void endElement(String name)
	{
		if (name.equals("NUMBER")){
			if(telephoneNumber!=null)telephoneNumber[0]=getText();
		}
		else if (name.equals("TEL")){
			if(current==WORK){
				workTels.add(telephoneNumber);
			}
			else homeTels.add(telephoneNumber);
			telephoneNumber=null;
			current=UNKNOWN;
		}
		else if (name.equals("ADR") || name.equals("EMAIL")){
			current=UNKNOWN;
		}
			
		if(!getText().equals(""))
		{
			if (name.equals("FAMILY") 		|| name.equals("GIVEN")
					|| name.equals("MIDDLE")|| name.equals("NICKNAME")
					|| name.equals("BDAY") 	|| name.equals("JABBERID")
					|| name.equals("LAT") 	|| name.equals("LON")
					|| name.equals("URL") 	|| name.equals("DESC")
					|| name.equals("FN")){
				// TODO add sound photo and key
				personal.put(name, getText());
			} else if (name.equals("TITLE") 	|| name.equals("ROLE")
					|| name.equals("ORGNAME") 	|| name.equals("ORGUNIT")){
				// TODO add logo
				business.put(name, getText());
			}
			else if (name.equals("POBOX") 
					|| name.equals("EXTADR") 	|| name.equals("STREET") 
					|| name.equals("LOCALITY")	|| name.equals("REGION")
					|| name.equals("PCODE")		|| name.equals("CTRY")){
				//only one work/home adres 
				if(current==WORK) business.put(name,getText());
				else if (current==HOME) personal.put(name,getText());
				else if (!homeAdresKnown){
					//put in personal if unknown adres
					personal.put(name,getText());
				}
			}
			else if (name.equals("USERID")){
				//only one work/home email
				if(current==WORK) business.put("EMAIL",getText());
				else personal.put("EMAIL",getText());
			}
			else if (name.equals("BINVAL")){
				if(current==PHOTO)
				{
					photo[1]=getText();
					personal.put("PHOTO", photo);
				}
				else if (current==LOGO)
				{
					logo[1]= getText();
					business.put("LOGO",logo);
				}
				current=UNKNOWN;
			}
		}
		clearCurrentChars();
	}

	public Extension build()
	{
		return new VCard(personal,business,homeTels,workTels);
	}

}
/*
 * Overrides for emacs Local variables: tab-width: 4 End:
 */
