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
 *  30-12-2004
 */

package nu.fw.jeti.images;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.XMLDataFile;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PluginsHandler;
import nu.fw.jeti.util.Preferences;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * @author E.S. de Boer
 */
public class Icons extends XMLDataFile  
{
	private List iconList;//all rostericons/emoticons
	private SAXParser parser;
	private String iconType;

	public Icons(String type)
	{
		iconList = Preferences.getPlugable(type);
		iconType = type;
	}

	private void loadParser()
	{
		try{parser = SAXParserFactory.newInstance().newSAXParser();}
		catch (FactoryConfigurationError ex){ex.printStackTrace();}
		catch (SAXException ex){ex.printStackTrace();}
		catch (ParserConfigurationException ex){ex.printStackTrace();}
	}
		
	public Icons(SAXParser parser,String type) throws IOException
	{
		//if(Start.programURL == null && !new File(Start.path + "plugins" + File.separator + "rostericons").exists()) throw new IOException("no rostericons");
		iconType = type;
		iconList =Preferences.getPlugable(iconType);
		this.parser = parser;
		InputStream  data = null;
		data = getClass().getResourceAsStream("/" + iconType+".xml");
		if(data==null)
		{
			try
	        {
				data = (new URL(Start.programURL + "plugins/" + iconType + "/" + iconType+ ".xml")).openStream();
			}
			//else data = new FileInputStream(Start.path + "plugins" + File.separator + "rostericons" + File.separator + "rostericons.xml");
			catch (IOException ex)
			{
				if (new File(Start.path + "plugins" + File.separator + iconType).exists()) scanRosterIcons();
				else throw new IOException("no " + iconType);
			}
		}
		if(data != null)
		{
			try
			{
				parser.parse(new InputSource(new InputStreamReader(data)),new PluginsHandler(iconList));
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
		for(Iterator i = iconList.iterator();i.hasNext();)
		{//remove rostericons whitout description /deleted plugins still in preferences
			Object[] object = (Object[])i.next();
			if(object[2] == null)
			{
				System.out.println(object[0] + "not found or no description");
				i.remove();
			}
			else if(object[3] == null)
			{
				object[3] = getType((String)object[0]);
			}
		}
		
		//rostericons = new HashMap();
		//backend.Start.loadPics("rostericons",this);
		/*
		for (Iterator i = iconList.iterator();i.hasNext();)
		{
			Object[] temp = (Object[])i.next();
			if(((Boolean)temp[1]).booleanValue()) loadEmoticon((String)temp[4]);
		}
		*/
		parser = null;
			//System.out.println(smilies);
			//makeSmilieList();
	}

	public void scanRosterIcons()
	{
		List oldrostericons = new ArrayList(iconList);
		iconList.clear();
		searchRosterIcons();
		//System.out.println(this.toString());
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(Start.path + "plugins" + File.separator + iconType + File.separator + iconType +".xml"));
			writer.write(this.toString());
			writer.close();
			//new PrintWriter(new BufferedOutputStream(new FileOutputStream(Start.path +"plugins/plugins.xml"))).write(this.toString());
		}
		catch (IOException ex2)
		{
			ex2.printStackTrace();
		}
		for(Iterator i = oldrostericons.iterator();i.hasNext();)
		{
			Object[] oldPlugin =(Object[])i.next();
			for(Iterator j = iconList.iterator();j.hasNext();)
			{	
				Object[] newPlugin =(Object[])j.next();
				if(oldPlugin[0].equals(newPlugin[0]))
				{
					newPlugin[1] = oldPlugin[1];
					if(!oldPlugin[3].equals(""))newPlugin[3] = oldPlugin[3];
					break;
				}
			}
		}
	}

	private void searchRosterIcons()
	{//voeg samen met plugindata?
		String urlString = Start.path + "plugins" + File.separator + iconType;
		
		//urlString += dir + "/";
		//System.out.println(urlString);
		File path = new File(urlString);
		File file[] = path.listFiles();
		if(file == null)
		{
			//System.out.println("no rostericons");
			return;
		}
		for (int tel=0;tel<file.length;tel++)
		{
			File currentFile = file[tel];
			//if(currentFile.toString().endsWith(".jisp") || currentFile.toString().endsWith(".jar") ||currentFile.toString().endsWith(".zip"))
			if(currentFile.isFile())
			{
				//System.out.println(currentFile);
				getRosterIconInfo(currentFile);
			}
		}
	}

	private void getRosterIconInfo(File file)
	{
		//System.out.println(file);
		try
		{
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			URL urlJar = new URL("jar:"+file.toURL() +"!/" + name + "/");
			//System.out.println(urlJar);
			Object[] data = new Object[6];
			if(parser ==null) loadParser();
			try{
				parser.parse(new URL(urlJar,"icondef.xml").openStream(),new RosterIconsHandler(urlJar,data));
				//parser.parse(stream,new rostericonsHandler(urlJar,data));
			} catch(ZipException e)
			{//no zip file so skip
				return;
			}
//			boolean found = false;
//			for(Iterator i = iconList.iterator();i.hasNext();)
//			{
//				Object[] temp = (Object[])i.next();
//				if(data[0].equals(temp[0]))
//				{
//					temp[2] = data[2];
//					temp[4] = file.getName();
//					found = true;
//					break;
//				}
//			}
			//if(!found)
			{//new plugin
				//System.out.println("data 0" + data[0]);
				data[1] = Boolean.TRUE;
				//temp[2] = data[2];
				data[3] = getType(file.getName());
				data[4] = file.getName();
				iconList.add(data);
			}
		}
		catch (IOException ex)
		{
			System.err.println(ex.getMessage());
		}catch (SAXException ex)
		{
			ex.printStackTrace();
		}
	}
	
	private String getType(String name)
	{
		name = name.toLowerCase();
		if(name.indexOf("msn")!=-1) return "msn";
		if(name.indexOf("icq")!=-1) return "icq";
		if(name.indexOf("aim")!=-1) return "aim";
		if(name.indexOf("aol")!=-1) return "aim";
		if(name.indexOf("jabber")!=-1) return "jabber";
		if(name.indexOf("yahoo")!=-1) return "yahoo";
		if(name.indexOf("gadu")!=-1) return "gadu-gadu"; 
		if(name.indexOf("sms")!=-1) return "sms";
		if(name.indexOf("smtp")!=-1) return "smtp";
		if(name.indexOf("mail")!=-1) return "smtp";
		return "unknown";
	}


	public void appendToXML(StringBuffer xml)
	{
		appendHeader(xml);
		appendOpenTag(xml,"<plugins>");
		for(Iterator i = iconList.iterator();i.hasNext();)
		{
			appendOpenTag(xml,"<plugin>");
			Object[] temp = (Object[]) i.next();
			appendElement(xml,"name",(String)temp[0]);
			appendElement(xml,"description",(String)temp[2]);
			appendElement(xml,"version",(String)temp[3]);
			appendElement(xml,"min_jeti_version",(String)temp[4]);//hackje
			appendCloseTag(xml,"</plugin>");
		}
		appendCloseTag(xml,"</plugins>");
	}


}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
