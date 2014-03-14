/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2001 E.S. de Boer  
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
 *  Created on 3-jan-2005
 */

package nu.fw.jeti.plugins.emoticons;

import javax.swing.ImageIcon;

/**
 * @author E.S. de Boer
 *
 */
public class Emoticon implements Comparable
{
	private ImageIcon icon;
	private String emoticon;
	
	public Emoticon(String emoticon,ImageIcon icon)
	{
		this.emoticon = emoticon;
		this.icon = icon;
	}
	
	public ImageIcon getIcon()
	{
		return icon;
	}
	
	public int compareTo(Object o)
	{
		String to =((Emoticon)o).toString();
		if(to.length()==emoticon.length())return 0;
		if(to.length()>emoticon.length())return 1;
		return -1;
	}
	
	public int hashCode()
	{
		return icon.hashCode();
	}
	
	public boolean equals(Object o)
	{
		return icon.equals(((Emoticon)o).icon);
	}
	
	public String toString()
	{
		return emoticon;
	}
}
