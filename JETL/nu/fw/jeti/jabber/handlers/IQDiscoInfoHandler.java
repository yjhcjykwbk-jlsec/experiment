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

package nu.fw.jeti.jabber.handlers;

import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.DiscoIdentity;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQBrowseBuilder;
import nu.fw.jeti.jabber.elements.IQDiscoInfo;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 */
// created on 6-aug-2004
public class IQDiscoInfoHandler extends ExtensionHandler
{
	private String node;
	private List features;
	private List identities;

	public IQDiscoInfoHandler(){}

	public void startHandling(Attributes attr)
	{
		features=null;
		identities=null;
		node = attr.getValue("node");
		//if(getName().equals("item")) parseStartElement(attr.getValue("category"),attr);
		//else parseStartElement(getName(),attr);
	}

	public void startElement(String name,Attributes attr)
	{
		if (name.equals("feature"))
		{
			if(features==null)features = new LinkedList();
			features.add(attr.getValue("var"));
		}
		else if(name.equals("identity"))
		{
			if(identities==null)identities = new LinkedList();
			identities.add(new DiscoIdentity(attr.getValue("category"),attr.getValue("type"),attr.getValue("name")));
		}
	}

	public Extension build()
	{
		return new IQDiscoInfo(node,identities,features);
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
