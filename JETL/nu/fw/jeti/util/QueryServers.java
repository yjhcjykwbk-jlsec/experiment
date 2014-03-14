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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 8-aug-2004
 */
 
package nu.fw.jeti.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.DiscoItem;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author E.S. de Boer
 *
 */
public class QueryServers
{
	private static List servers;
		
	public static List getServers()
	{
		if(servers==null) readServerXML();
		return servers;
	}
	
	private static void readServerXML()
	{
		SAXParser parser=null;
		try{parser = SAXParserFactory.newInstance().newSAXParser();}
		catch (FactoryConfigurationError ex){ex.printStackTrace();}
		catch (SAXException ex){ex.printStackTrace();}
		catch (ParserConfigurationException ex){ex.printStackTrace();}
		InputStream  data = null;
		try
		{//TODO add path for local servers.xml
			data = (new URL(Start.programURL + "servers.xml")).openStream();
		}
		catch (IOException ex)
		{
			//ex.printStackTrace();
			data = QueryServers.class.getResourceAsStream("/servers.xml");
		}
		List items = new LinkedList();
		if(data != null)
		{
			try
			{
				parser.parse(data,new DiscoItemsHandler(items));
			}
			catch (SAXException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		servers = items;
	}
	

    static class DiscoItemsHandler extends DefaultHandler
    {
        private String node;
        private List items;
	
        public DiscoItemsHandler(List items)
        {
            this.items = items;
        }
	
        public void startHandling(Attributes attr)
        {
            node = attr.getValue("node");
        }

        public void startElement(String uri, String localName, String qName, Attributes attr) 
        {
            if(qName.equals("item"))
            {
                if(items==null)items = new LinkedList();
                try
                {
                    JID jid = JID.checkedJIDFromString(attr.getValue("jid"));
                    items.add(new DiscoItem(jid,attr.getValue("name"),attr.getValue("node"),attr.getValue("action")));
                } catch (InstantiationException e)
                {
                    Log.xmlParseException(e);
                }
            }
        }
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
