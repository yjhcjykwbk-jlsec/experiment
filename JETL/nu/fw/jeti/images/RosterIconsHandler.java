/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2002 E.S. de Boer  
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
 *  
 * 
 */

package nu.fw.jeti.images;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.xml.sax.helpers.DefaultHandler;

//29-12-2004

/**
 * @author E.S. de Boer
 */
public class RosterIconsHandler extends DefaultHandler
{
	private StringBuffer text = new StringBuffer();
	private Map iconSet = new HashMap();
	private Map rosterIcons;
	private Icon icon;
	private String name;
	private String sort;
	private String type;	
	private URL path;
	private Object[] data;

	public RosterIconsHandler(URL path,Map icons,String type)
    {
		this.type=type;
		this.path = path;
		this.rosterIcons = icons;
	}

	public RosterIconsHandler(URL path,Object[] data)
	{
		this.path = path;
		this.data = data;
	}

	public void endDocument()
	{
		if(data == null)
		{
			rosterIcons.put(type,iconSet);
		}
		else data[0] = name;
	}

	public void endElement(String namespaceURI,String sName,String qName)
	{
		String text = this.text.toString().trim();
		if(qName.equals("name")) name = text;
		if(data!=null)
		{
			if(qName.equals("description")) data[2] = text;
			//else if(qName.equals("version")) data[3] = text;
		}
		else
		{
			if(qName.equals("x")) sort = text;
			if(qName.equals("graphic")) readImage(text);//old
			else if(qName.equals("object")) readImage(text);//new
			else if(qName.equals("icon"))
			{
				iconSet.put(sort,icon);
			}
		}
		this.text = new StringBuffer();
	}

	public void characters(char buf[], int offset, int Len)
	{
		   text.append(buf, offset, Len);
	}

	private void readImage(String resource)
	{
		URL picURL = null;
		if(path==null)picURL = getClass().getClassLoader().getResource("msn_messenger-6.0/"+resource);
		else
		{
	        try
	        {
	            picURL = new URL(path + resource);
	        }
	        catch (MalformedURLException ex)
	        {
				//System.err.println(resource +" not found");
	        	//webstart
	        }
		}
		if(picURL ==null) System.err.println(resource +" not found");
		else icon =new ImageIcon(picURL);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
