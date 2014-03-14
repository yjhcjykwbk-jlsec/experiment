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

package nu.fw.jeti.backend;

import org.xml.sax.helpers.*;
import org.xml.sax.*;
import java.util.*;

/**
 * @author E.S. de Boer
 */

public class ProfileInfoHandler extends DefaultHandler
{
	private Map profiles;
	private StringBuffer text = new StringBuffer();
	private String name;
	private String server;
    private String host;
	private String username;
	private String password;
	private String resource;
	private boolean ssl;
	private int port;
	private boolean proxy = false;
	private boolean hideStatusWindow=false;
	private String proxyServer;
	private String proxyUsername;
	private String proxyPassword;
	private String proxyPort;
	private int priority;

	public ProfileInfoHandler(Map profiles)
	{
		this.profiles = profiles;
	}

	public void startDocument()
	{}

	public void endDocument()
	{}

	public void startElement(String namespaceURI, String sName, String qName,Attributes attrs)
	{
		if (qName.equals("profile"))
		{
			name = attrs.getValue("name");
			server = attrs.getValue("server");
			username = attrs.getValue("username");
			password = attrs.getValue("password");
			if(password != null) password = decrypt(password);
			resource = attrs.getValue("resource");
			ssl = Boolean.valueOf(attrs.getValue("ssl")).booleanValue();
			try
			{
				priority = Integer.parseInt(attrs.getValue("priority"));
			}
			catch (NumberFormatException ex)
			{
				priority=0;
			}
			hideStatusWindow = Boolean.valueOf(attrs.getValue("hideStatusWindow")).booleanValue();
			try
			{
				port = Integer.parseInt(attrs.getValue("port"));
			}
			catch (NumberFormatException ex)
			{
				if (ssl) port = 5223;
				else port = 5222;
			}
            host = attrs.getValue("host");
			proxy=false;
		}
		else if (qName.equals("proxy"))
		{
			proxy=true;
			proxyServer = attrs.getValue("proxyServer");
			proxyUsername = attrs.getValue("proxyUsername");
			proxyPassword = attrs.getValue("proxyPassword");
			if(proxyPassword != null && !proxyPassword.equals("")) proxyPassword = decrypt(proxyPassword);
			proxyPort = attrs.getValue("proxyPort");
		}
	}
	
	private String decrypt(String in)
	{
		Random r = new Random(Integer.parseInt(in.substring(0,2) + in.substring(in.length()-2,in.length()),16));
		StringBuffer decrypted = new StringBuffer();
		for(int i=2;i<in.length()-2;i+=4)
		{
			int digit = Integer.parseInt(in.substring(i,i+4),16) - r.nextInt(100);
			decrypted.append((char)digit);
		}
		return decrypted.toString();
	}

	public void endElement(String namespaceURI, String sName,String qName) 
	{
        if(qName.equals("profile")) {
            profiles.put(name,
                         new LoginInfo(server, host, username, password,
                                       resource, port, ssl,priority,proxy,
                                       hideStatusWindow, proxyServer,
                                       proxyUsername, proxyPassword,
                                       proxyPort));
        }
		text = new StringBuffer();
	}

	public void characters(char buf[], int offset, int Len)
	{
		text.append(buf, offset, Len);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
