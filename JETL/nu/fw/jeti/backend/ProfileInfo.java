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
 */

package nu.fw.jeti.backend;

import java.io.*;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author E.S. de Boer
 */

public class ProfileInfo extends XMLDataFile
{
	private HashMap profiles = new HashMap();

	public ProfileInfo()
	{
		this(null);
	}

	public ProfileInfo(SAXParser parser)
	{
		if (parser == null) parser = initSax();
		InputStream data = null;
		try
		{
			data = new FileInputStream(Start.path + "profiles.xml");
		} catch (IOException ex)
		{// preferences xml not found so make one
			// System.out.println(this.toString());
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						Start.path + "profiles.xml"));
				writer.write(this.toString());
				writer.close();
			} catch (IOException ex2)
			{// only if disk full?,should not happen
				ex2.printStackTrace();

			}
		}
		if (data != null)
		{
			try
			{
				parser.parse(data, new ProfileInfoHandler(profiles));
			} catch (SAXException ex)
			{
				ex.printStackTrace();
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
			try
			{
				data.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private SAXParser initSax()
	{
		try
		{
			return SAXParserFactory.newInstance().newSAXParser();
		} catch (FactoryConfigurationError ex)
		{
			ex.printStackTrace();
		} catch (SAXException ex)
		{
			ex.printStackTrace();
		} catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Object[] getProfilesList()
	{
		return profiles.keySet().toArray();
	}

	public LoginInfo getProfile(String name)
	{
		return (LoginInfo) profiles.get(name);
	}

	public void setProfile(String name, LoginInfo info)
	{
		profiles.put(name, info);
	}

	public void remove(String name)
	{
		profiles.remove(name);
		save();
	}

	public void save()
	{
		StringBuffer xml = new StringBuffer();
		appendToXML(xml);
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(Start.path + "profiles.xml"), "UTF8"));
			writer.write(xml.toString());
			writer.close();
		} catch (IOException ex2)
		{
			ex2.printStackTrace();
		}

	}

	public void appendToXML(StringBuffer xml)
	{

		Random r = new Random();
		appendHeader(xml);
		appendOpenTag(xml, "<profiles>");
		for (Iterator i = profiles.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			String name = (String) entry.getKey();

			LoginInfo temp = (LoginInfo) entry.getValue();
			appendOpenTag(xml, "<profile");
			appendAttribute(xml, "name", name);
			appendAttribute(xml, "server", temp.getServer());
			String password = temp.getPassword();
			if (password != null)
			{
				appendAttribute(xml, "password", encrypt(password, r));
			}
			appendAttribute(xml, "username", temp.getUsername());
			appendAttribute(xml, "resource", temp.getResource());
			appendAttribute(xml, "port", Integer.toString(temp.getPort()));
			appendAttribute(xml, "host", temp.getHost());
			appendAttribute(xml, "ssl", Boolean.toString(temp.isSSl()));
			appendAttribute(xml, "priority", Integer.toString(temp
					.getPriority()));
			appendAttribute(xml, "hideStatusWindow", Boolean.toString(temp
					.hideStatusWindow()));
			if (temp.useProxy())
			{
				xml.append('>');
				appendOpenTag(xml, "<proxy");
				appendAttribute(xml, "proxyServer", temp.getProxyServer());
				appendAttribute(xml, "proxyUsername", temp.getProxyUsername());
				password = temp.getProxyPassword();
				if (password != null && !password.equals(""))
				{
					appendAttribute(xml, "proxyPassword", encrypt(password, r));
				}
				appendAttribute(xml, "proxyPort", temp.getProxyPort());
				appendCloseSymbol(xml);
				appendCloseTag(xml, "</profile>");
			} else appendCloseSymbol(xml);
		}
		appendCloseTag(xml, "</profiles>");
	}

	private String encrypt(String password, Random r)
	{
		int seed = r.nextInt(65536);
		r.setSeed(seed);

		char passwords[] = password.toCharArray();
		StringBuffer encrypted = new StringBuffer();
		String hexSeed = "0000".concat(Integer.toHexString(seed));
		hexSeed = hexSeed.substring(hexSeed.length() - 4, hexSeed.length());
		encrypted.append(hexSeed.substring(0, 2));
		String lang;
		for (int tel = 0; tel < passwords.length; tel++)
		{
			int pass = passwords[tel];
			lang = "0000".concat(Integer.toHexString(pass + r.nextInt(100)));
			encrypted.append(lang.substring(lang.length() - 4, lang.length()));
		}
		encrypted.append(hexSeed.substring(2, 4));
		return encrypted.toString();
	}
}
/*
 * Overrides for emacs Local variables: tab-width: 4 End:
 */
