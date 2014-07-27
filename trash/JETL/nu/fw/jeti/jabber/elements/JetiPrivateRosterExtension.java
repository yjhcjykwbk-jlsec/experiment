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
 *
 *	Created on 21-nov-2003
 */
 
package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.Backend;

/**
 * @author E.S. de Boer
 *
 */
public class JetiPrivateRosterExtension extends Extension implements IQXExtension
{
    private String[] openGroups ;
	private String xmlVer;
	public final static String XML_VERSION ="v1";

	public JetiPrivateRosterExtension(){}

	public JetiPrivateRosterExtension(String[] openGroups)
	{
		this.openGroups = openGroups;
		xmlVer = XML_VERSION;
	}
	
	public JetiPrivateRosterExtension(String[] openGroups, String xmlVersion)
	{
		this.openGroups = openGroups;
		xmlVer = xmlVersion;
	}

    public String[] getOpenGroups()
    {
	    return openGroups;
    }
    
    public boolean isCorrectVersion()
    {
	    return XML_VERSION.equals(xmlVer);
	}


	public String getXmlVersion()
    {
	    return xmlVer;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			backend.getMain().openGroups(this);
		}
		else if (iq.getType().equals("error"))
		{
			System.err.println(iq.getErrorDescription());
		}
	}
	

	public void appendToXML(StringBuffer retval)
	{
		retval.append("<jeti xmlns='jeti:rosterprefs'");
		if (openGroups == null)
		{ //short cut
			retval.append("/>");
			return;
		}
		appendAttribute(retval,"xmlVersion",xmlVer);
		retval.append(">");
		if(openGroups != null)
		{
			for (int i=0;i<openGroups.length;i++)
			{
				appendElement(retval,openGroups[i],true);
			}
		}
		retval.append("</jeti>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
