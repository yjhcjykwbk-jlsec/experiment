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
 *	Created on 23-okt-2004
 */
 
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author E.S. de Boer
 *
 */
public class XMPPError extends Extension
{
	int errorCode;
	String type;
	List errors = new LinkedList();
	
	public XMPPError(String type, int errorCode)
	{
		this.errorCode= errorCode;
		this.type=type;
	}
	
	public void addError(XMPPErrorTag error)
	{
		 errors.add(error);
	}
	
	public String getType()
	{
		return type;
	}
	
	public int code()
	{
		return errorCode;
	}

    public Iterator getXMPPErrors() {
        return errors.iterator();
    }

	public void appendToXML(StringBuffer xml)
	{
	    xml.append("<error");
		appendAttribute(xml,"code",String.valueOf(errorCode));
		appendAttribute(xml,"type",type);
		xml.append(">");
		for(Iterator i = errors.iterator();i.hasNext();)
		{
			((XMPPErrorTag)i.next()).appendToXML(xml);
		}
		xml.append("</error>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
