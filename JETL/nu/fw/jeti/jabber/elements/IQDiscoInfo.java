/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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


//Created on 6-aug-2004
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.jabber.Backend;

/**
 * @author E.S. de Boer
 * implements the http://jabber.org/protocol/disco#info'/ spec
 */
public class IQDiscoInfo extends Extension implements IQExtension, DiscoveryInfo
{
	private String node;
	private List identities;
	private List features;
	

	public IQDiscoInfo()
	{}
	
	public IQDiscoInfo(String node)
	{
		this.node = node;
	}

	public IQDiscoInfo(String node,List identities,List features)
	{
		this.node = node;
		this.identities = identities;
		this.features = features;
	}

	public String getNode()
	{
		return node;
	}

	public Iterator getIdentities()
	{
		if (identities == null)return null;
		return identities.iterator();
	}
	
	public Iterator getFeatures()
	{
		if (features == null)return null;
		return features.iterator();
	}
	
	public boolean hasFeatures()
	{
		return features!=null;
	}
	
	//discovery implementations
	//use the first identity, browse can not have more identities
	public String getName()
	{
		if (identities == null)return null;
		return ((DiscoIdentity)identities.get(0)).getName();
	}

	//discovery implementations
	//use the first identity, browse can not have more identities
	public String getCategory()
	{
		if (identities == null)return null;
		return ((DiscoIdentity)identities.get(0)).getCategory();
	}

	//discovery implementations
	//use the first identity, browse can not have more identities
	public String getType()
	{
		if (identities == null)return null;
		return ((DiscoIdentity)identities.get(0)).getType();
	}
	

//	public boolean hasChildItems()
//	{
//		return identities != null;
//	}
	
	public void execute(InfoQuery iq,Backend backend){}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns= 'http://jabber.org/protocol/disco#info'");
		appendAttribute(xml, "node", node);
		if (identities == null && features==null)
		{ //short cut
			xml.append("/>");
			return;
		}
		xml.append('>');
		if (identities != null)
		{
			for (Iterator i = identities.iterator(); i.hasNext();)
			{
				((DiscoIdentity) i.next()).appendToXML(xml);
			}
		}
		if(features!=null)
		{
			for (Iterator i = features.iterator(); i.hasNext();)
			{
				xml.append("<feature var='" + i.next() +"'/>");
			}
		}
		xml.append("</query>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
