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
 *	Created on 26-dec-2003
 */
 
package nu.fw.jeti.plugins;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.UIManager;
import javax.xml.parsers.SAXParser;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.URLClassloader;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class PluginsInfo
{
	private static URLClassloader classLoader;//change back to private
	private static Map loadedPlugins = new HashMap();
	public static Map loadedPreferencePanels = new HashMap();
	private static Backend backend;
	private static Map pluginInstances = new HashMap();
			
	public PluginsInfo(Backend backend,SAXParser parser,Start start)
	{
		PluginsInfo.backend = backend;
		
		if(!Start.applet)
 		{
			classLoader = new URLClassloader(new URL[]{Start.programURL}, getClass().getClassLoader());
			UIManager.put("ClassLoader", classLoader);
		}		
		new PluginData(parser);		
		
		
		//new test.QueryServers(backend);
		
		//new nu.fw.jeti.plugins.pubsub.Plugin(backend);

		//		try
//		{
	//		nu.fw.jeti.plugins.vcard.Plugin.init(backend);

	//	nu.fw.jeti.plugins.filetransfer.Plugin.init(backend);
	//	loadedPreferencePanels.put("filetransfer",nu.fw.jeti.plugins.filetransfer.PrefPanel.class);
		
	//		nu.fw.jeti.plugins.sound.Plugin.init(backend);
		//	loadedPreferencePanels.put("sound",nu.fw.jeti.plugins.sound.PrefPanel.class);
			
			//		}
//		catch (Exception e)
//		{
//			// TOD Auto-generated catch block
//			e.printStackTrace();
//		}
		
	//	nu.fw.jeti.plugins.ibb.Plugin.init(backend);
		
	//	nu.fw.jeti.plugins.groupchat.Plugin.init(backend);
		
//		nu.fw.jeti.plugins.openpgp.Plugin.init(backend);
//		loadedPlugins.put("openpgp", nu.fw.jeti.plugins.openpgp.Plugin.class);
		
		//new nu.fw.jeti.plugins.logtoserver.Plugin(backend);
		//new nu.fw.jeti.plugins.servertolog.Plugin(backend);
//		
//		loadedPlugins.put("emoticons", nu.fw.jeti.plugins.emoticons.Plugin.class);
//		try
//		{
//			new nu.fw.jeti.plugins.emoticons.Plugin(backend);
//		} catch (IOException e)
//		{
//			// 
//			e.printStackTrace();
//		}
				
		//loadedPreferencePanels.put("alertwindow",nu.fw.jeti.plugins.alertwindow.PrefPanel.class);
		
		//start.LoadPlugins(urlString);
		for (Iterator i = Preferences.getPlugins().iterator(); i.hasNext();)
		{
			Object[] temp = (Object[]) i.next();
			if (temp[2] == null)continue; //missing plugin
			if (((Boolean) temp[1]).booleanValue())
			{
				start.setSplashText((String) temp[0]);
				loadPlugin((String) temp[0]);
			}
		}
	}
		
	//translator hackje vervang door translator interfaces as in io
	private static Translator translator;
	
	public static void setTranslator(Translator translator)
	{
		PluginsInfo.translator = translator;
	}
	
	public static Translator getTranslator()
	{
		return translator;
	}
	

	public static boolean isPluginLoaded(String name)
	{
		return loadedPlugins.containsKey(name);
	}

	public static void loadPlugin(String name)
	{
		Class loadedClass =null;
		if (!Start.applet)
		{//non applet version
			URL url =null;// new URL[1];
			try
			{
				//if (Start.programURL != null)
					url = new URL(Start.programURL, "plugins/" + name + ".jar");
				//else url = new URL(Start.localURL, "plugins/" + name + ".jar");
			}
			catch (MalformedURLException e)
			{}
			//System.out.println(url);
			//URLClassLoader loader = new URLClassLoader(url, parentClassLoader);
			classLoader.addURL(url); 
			try
			{
				loadedClass = classLoader.loadClass("nu.fw.jeti.plugins." + name + ".Plugin");
			}
			catch (ClassNotFoundException e)
			{
				System.err.println(MessageFormat.format(I18N.gettext("main.error.{0}_plugin_not_found"), new Object[]{name}));
				return;
			}
		}
		else loadedClass = nu.fw.jeti.applet.Jeti.getPlugin(name);
		
		
		try
		{//init plugin
			Method m = loadedClass.getMethod("init",new Class[]{Backend.class});
			m.invoke(null,new Object[]{backend});
		}catch(Exception e)
		{
			e.printStackTrace();
            //System.err.println(MessageFormat.format(I18N.gettext("main.error.{0}_plugin_failed_to_load"), new Object[]{name}));
			return;
		}
		
		
//		if (!loadOnFirstUse)
//		{
//			
//			try
//			{
//				Constructor c = loadedClass.getConstructor(new Class[] { Class.forName("nu.fw.jeti.jabber.Backend")});
//				addInstance(name, new WeakReference(c.newInstance(new Object[] { backend })));
//			}
//			catch (InvocationTargetException ie)
//			{
//				ie.printStackTrace();
//				//System.out.println("Error while initializing " + name + " : " + ie.getCause().getMessage());
//				return;
//			}
//			catch (Exception e2)
//			{
//				e2.printStackTrace();
//				return;
//			}
//		}
		
		loadedPlugins.put(name, loadedClass);
		if(!Start.applet)
		{	
			try
			{
				loadedPreferencePanels.put(name, classLoader.loadClass("nu.fw.jeti.plugins." + name + ".PrefPanel"));
			}
			catch (ClassNotFoundException e)
			{
                            // Do nothing
			}
		}
		else nu.fw.jeti.applet.Jeti.getPrefPanel(name,loadedPreferencePanels);
		//System.out.println(name + "loaded");
	}
	
	public static void unloadPlugin(String name)
	{
		Class loadedClass = (Class)loadedPlugins.remove(name);
                if (loadedClass == null) {
                    return;
                }
		System.out.println("removing "+ name);
		loadedPreferencePanels.remove(name);
		List list = (List) pluginInstances.remove(name);
		try
		{//unload cleaning
			Method m = loadedClass.getMethod("unload",new Class[]{Backend.class});
			m.invoke(null,new Object[]{backend});
		}catch(NoSuchMethodException e){System.out.println(name + " has no remove");}
		catch(Exception e)
		{
			e.printStackTrace();
		}
				
		if (list == null) return;
		for (Iterator i = list.iterator(); i.hasNext();)
		{//unload loaded plugins
			Plugins plugin = (Plugins)((WeakReference)i.next()).get();
			if (plugin !=null)plugin.unload();
		}
		System.out.println(name +" removed");
	}
	
	
	/**
	 * Gets a single instance of a plugin, the plugin has to have the static getInstance() method
	 * @param name name of the plugin
	 * @return instance of the plugin
	 */
	public static Object getPluginInstance(String name)
	{
		Object o = null;
		try
		{
			Class loadedClass = (Class) loadedPlugins.get(name);
			try
			{
				Method m = loadedClass.getMethod("getInstance",null);
				o = m.invoke(null,null);
			}catch(NoSuchMethodException e){System.out.println(name + " has no instance method");}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception ie)
		{
			ie.printStackTrace();
			//System.out.println("Error while initializing " + name + " : " + ie.getCause().getMessage());
			return null;
		}
		return o;
	}
	
	public static Plugins newPluginInstance(String name)
	{
		Object o = null;
		try
		{
			o = ((Class) loadedPlugins.get(name)).newInstance();
			addInstance(name,o);
		}
		catch (Exception ie)
		{
			ie.printStackTrace();
			//System.out.println("Error while initializing " + name + " : " + ie.getCause().getMessage());
			return null;
		}
		return (Plugins) o;
	}

	private static void addInstance(String name, Object object)
	{
		List list = (List) pluginInstances.get(name);
		if (list == null)
		{
			list = new LinkedList();
			pluginInstances.put(name, list);
		}
		list.add(new WeakReference(object));
	}
	
	public static String getAbout()
	{
		StringBuffer buffer = new StringBuffer();
		for(Iterator i = loadedPlugins.entrySet().iterator();i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			String text = null;
			try
			{
				 text = (String) ((Class)entry.getValue()).getField("ABOUT").get(null);
			}
			catch (Exception e1)
			{}
			if(text!=null) buffer.append("\n - " + (String)entry.getKey() + '\n' + text);
		 }
		 return buffer.toString(); 
	} 
	
	//remove plugins
	public void exit()
	{
		String[] temp=new String[loadedPlugins.size()];
		loadedPlugins.keySet().toArray(temp);
		for(int i=0;i<temp.length;i++)
		{
			unloadPlugin(temp[i]);
		}
		System.out.println("plugins unloaded");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
