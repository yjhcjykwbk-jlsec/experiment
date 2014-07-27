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
 *	Created on 14-aug-2003
 */
 
package nu.fw.jeti.applet;



import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQXExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class JetiPrivatePreferencesExtension extends Extension implements IQXExtension
{
    private Preferences preferences;
	private String xmlVer;
	public final static String XML_VERSION ="v1";

	public JetiPrivatePreferencesExtension(){}
	
	public JetiPrivatePreferencesExtension(Preferences p)
	{
		preferences = p;
		xmlVer = XML_VERSION;
	}

//	public JetiPrivatePreferencesExtension()
//	{
//		this.openGroups = openGroups;
//		xmlVer = XML_VERSION;
//	}
	  
    
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
			
		}
		else if (iq.getType().equals("error"))
		{
			System.out.println(iq.getErrorDescription());
		}
	}
	

	public void appendToXML(StringBuffer retval)
	{
		retval.append("<jeti xmlns='jeti:prefs'");
		if (preferences == null)
		{ //short cut
			retval.append("/>");
			return;
		}
		appendAttribute(retval,"xmlVersion",xmlVer);
		retval.append(">");
		if(preferences != null)
		{
			preferences.appendToXML(retval);
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
