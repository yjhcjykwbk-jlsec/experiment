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

package nu.fw.jeti.plugins;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.XMLDataFile;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class PluginData extends XMLDataFile
{
	private List plugins;
	//private List enabledPlugins;

	public PluginData(){plugins = Preferences.getPlugins();}

    public PluginData(SAXParser parser)
    {
		plugins = Preferences.getPlugins();
		InputStream  data = null;
		data = getClass().getResourceAsStream("/plugins.xml");
		if(data==null)
		{
			try
	        {
				//if(Start.programURL != null)
				{
					//loadremote plugins
					data = (new URL(Start.programURL + "plugins.xml")).openStream();
				}
	            //else data = new FileInputStream(Start.path +"plugins.xml");
				//data = new URL(local  + "plugins.xml").openStream();
			}
	        catch (IOException ex)
	        {//plugins xml not found so make one
				scanPlugins();
			}
		}
		if(data != null)
		{
			try
			{
				parser.parse(new InputSource(new InputStreamReader(data)),new PluginsHandler(plugins));
			}
			catch (SAXException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			for(Iterator i = plugins.iterator();i.hasNext();)
			{//remove plugins whitout description /deleted plugins still in preferences
				Object[] object = (Object[])i.next();
				if(object[2] == null) i.remove();//was 3
			}
		}
    }

	public void scanPlugins()
	{
		List oldPlugins = new ArrayList(plugins);
		plugins.clear();
        searchPlugins(Start.path + "plugins");
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(Start.path +"plugins.xml"), "UTF8"));
			writer.write(this.toString());
			writer.close();
		}
		catch (Exception ex2)
		{
                    System.err.println("Failed to save 'plugins.xml': " + ex2.getMessage());
		}
		for(Iterator i = oldPlugins.iterator();i.hasNext();)
		{
			Object[] oldPlugin =(Object[])i.next();
			for(Iterator j = plugins.iterator();j.hasNext();)
			{	
				Object[] newPlugin =(Object[])j.next();
				if(oldPlugin[0].equals(newPlugin[0]))
				{
					newPlugin[1] = oldPlugin[1];
					break;
				}
			}
		}
	}

	private void searchPlugins(String dir)
	{
            try {
        File path = new File(dir);
		File file[] = path.listFiles();
		if(file == null)
		{
			System.err.println(MessageFormat.format(I18N.gettext("main.error.{0}_contains_no_plugins"),new Object[]{dir}));
			return;
		}
		for (int tel=0;tel<file.length;tel++)
		{
			File currentFile = file[tel];
			if(currentFile.toString().endsWith(".jar"))
			{
				getPluginInfo(currentFile);
			}
		}
            } catch (Exception e) {
                // Ignore
            }
	}

	private void getPluginInfo(File file)
	{
		URL url[] = new URL[1];
		try{
			url[0] = file.toURL();
		}catch(MalformedURLException e){}
		//System.out.println(url[0]);
		String name =file.getName();
		name = name.substring(0,name.length()-4);
		//System.out.println(name);
		Class pluginClass=null;
		try
		{
			URLClassLoader loader = new URLClassLoader(url,this.getClass().getClassLoader());
			pluginClass = loader.loadClass("nu.fw.jeti.plugins." + name + ".Plugin");
		}
		catch(Exception e)
		{
			return;
		}
		try
		{
			String name2 = pluginClass.getField("NAME").get(null).toString();
			boolean found = false;
			for(Iterator i = plugins.iterator();i.hasNext();)
			{
				Object[] temp = (Object[])i.next();
				if(name2.equals(temp[0]))
				{
					fillPluginsDef(temp,pluginClass ,name2);
					found = true;
					break;
				}
			}
			if(!found)
			{//new plugin
				Object[] temp = new Object[6];
				fillPluginsDef(temp,pluginClass ,name2);
				temp[1] = Boolean.FALSE;
				plugins.add(temp);
			}
			//pluginClass.getMethod("getInfo",new Class[]{}).invoke();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void fillPluginsDef(Object[] temp,Class pluginClass,String name) throws Exception
	{
		temp[0]=name;
		temp[2]=pluginClass.getField("DESCRIPTION").get(null).toString();
		temp[3]=pluginClass.getField("VERSION").get(null).toString();
		temp[4]=pluginClass.getField("MIN_JETI_VERSION").get(null).toString();
		try 
		{
			temp[5]=pluginClass.getField("PARENT").get(null).toString();
		}
		catch (NoSuchFieldException e) {}
		
	}

	public List getPlugins()
	{
		return plugins;
	}

//	private static void readJAR(URL url)
//	{
//		try
//		{
//			URL urlJar = new URL("jar:"+url+"!/");
//			JarFile jarFile = new JarFile(  ""  );
//			//BufferedReader data =new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("info.cfg"))));
//
//			BufferedReader data = null;
//			try
//			{
//				data =new BufferedReader(new InputStreamReader(new URL(urlJar + "info.cfg").openStream()));
//			}
//			catch (IOException ex)
//			{
//				data =new BufferedReader(new InputStreamReader(new URL(urlJar + "icondef.xml").openStream()));
//			}
//		}
//		catch(IOException e)
//		{
//			//System.out.println(currentFile.getName() + " is not a valid jar file");
//			System.out.println(e.toString());
//		}
//	}

	public void appendToXML(StringBuffer xml)
	{
		appendHeader(xml);
		appendOpenTag(xml,"<plugins>");
		for(Iterator i = plugins.iterator();i.hasNext();)
		{
			appendOpenTag(xml,"<plugin>");
			Object[] temp = (Object[]) i.next();
			appendElement(xml,"name",(String)temp[0]);
			appendElement(xml,"description",(String)temp[2]);
			appendElement(xml,"version",(String)temp[3]);
			appendElement(xml,"min_jeti_version",(String)temp[4]);
			appendElement(xml,"parent",((String)temp[5]));
			appendCloseTag(xml,"</plugin>");
		}
		appendCloseTag(xml,"</plugins>");
	}

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Arguments: dir_to_scan outfile");
            System.exit(1);
        }
        new Preferences();
        PluginData data = new PluginData();
        data.plugins.clear();
        data.searchPlugins(args[0]);
        try {
        	BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(args[1]), "UTF8"));
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write file: " + e);
            System.exit(1);
        }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
