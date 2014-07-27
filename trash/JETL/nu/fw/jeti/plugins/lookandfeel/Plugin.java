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
 */

package nu.fw.jeti.plugins.lookandfeel;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.URLClassloader;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class Plugin implements Plugins
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "lookandfeel.Changes_Look_and_Feel";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "lookandfeel";
	public final static String ABOUT = "Wrapper for skins see www.javootoo.com";
	public final static String PARENT = "Skins";
	
	public static void init(Backend backend)
	{
		String name = Preferences.getString("lf", "currentLF",null);
		if (name == null)
			return;
		String file = null;
		try
		{
			file = (String) loadLookAndFeelData().get(name);
		}
		catch (Exception e)
		{
			System.err.println(I18N.gettext("lookandfeel.lookandfeel_cfg_not_found"));
		}
		if (file == null) return;
		try
		{
			loadLookAndFeel(backend, name, file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println(I18N.gettext("lookandfeel.lookandfeel_could_not_be_loaded"));
		}
	}

	public static void loadLookAndFeel(Backend backend, String name, String file) throws Exception
	{
		URLClassloader classLoader =(URLClassloader)UIManager.get("ClassLoader");
		//later dynamicaly search lenfs 
		URL url = null; //[] = new URL[1];

		//if (Start.programURL != null)
		url = new URL(Start.programURL, "plugins/lf/" + file);
		//else url = new URL(Start.localURL, "plugins/lf/" + file);

		classLoader.addURL(url);
		//UIManager.put("ClassLoader", PluginsInfo.classLoader);
		//System.out.println(url);

		//		try
		//		{
		//
		//			classLoader = new URLClassLoader(new URL[] { new URL(Start.localURL, "plugins/lf/metouia.jar")});
		//		}
		//		catch (Exception e)
		//		{
		//			e.printStackTrace();
		//		}

		//		try
		//		{
		//			Start.classLoader.addURL(new URL(Start.localURL, "plugins/lf/metouia.jar"));
		//		}
		//		catch (MalformedURLException e)
		//		{
		//			e.printStackTrace();
		//		}
		//
		//		UIManager.put("ClassLoader", Start.classLoader);
		//		//		Sets the current look and feel to metouia look & feel:
		//
				
		Class s = classLoader.loadClass(name);
		if (name.equals("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel"))
			initOyoaha(s);
		else if (name.equals("com.l2fprod.gui.plaf.skin.SkinLookAndFeel"))
			initSkinLF(s,classLoader);
		else if (name.equals("org.compiere.plaf.CompiereLookAndFeel"))
			initCompiere(s,classLoader);
		else
			UIManager.setLookAndFeel((LookAndFeel) s.newInstance());

		SwingUtilities.updateComponentTreeUI(backend.getMainWindow());
//		Jeti j = backend.getMain();
//		if(j!=null)j.updateLF();
	}
	
	private static void initCompiere(Class compiere, URLClassloader classLoader) throws Exception
	{
		try {
		if(new File(Start.path + "Compiere.properties").exists())
		{
			Class s2 = classLoader.loadClass("org.compiere.util.Ini");
			Method m = s2.getMethod("loadProperties",new Class[] {String.class});
			m.invoke(null, new Object[] {Start.path + "Compiere.properties"});
			Class s = classLoader.loadClass("org.compiere.plaf.CompiereTheme");
			Method m1 = s.getMethod("load",new Class[] {String.class});
			m1.invoke(null, new Object[] {Start.path + "Compiere.properties"});
			Class s3 = classLoader.loadClass("org.compiere.plaf.CompierePanelUI");
			Method m3 = s3.getMethod("setSetDefault",new Class[] {boolean.class});
			m3.invoke(null, new Object[] {Boolean.TRUE});
			//CompierePLAF.setPLAF(backend.getMainWindow());
			//CompiereTheme.load(Start.path + "Compiere.properties");
			//Ini.loadProperties(Start.path + "Compiere.properties");
			////CompierePanelUI.setSetDefault(true);
		}
		}catch (Exception e) {e.printStackTrace();}
		UIManager.setLookAndFeel((LookAndFeel) compiere.newInstance());
	}

	private static void initOyoaha(Class oyoahaClass) throws Exception
	{
		String mt = Preferences.getString("lf", "metaltheme",null);
		if (mt != null)
		{
			URL metalTheme = new URL(mt);
			Method m = oyoahaClass.getMethod("setCurrentTheme", new Class[] { java.net.URL.class });
			m.invoke(null, new Object[] { metalTheme });
		}
		Object lf = oyoahaClass.newInstance();
		String ot = Preferences.getString("lf", "oyoahatheme",null);
		if (ot != null)
		{
			URL theme = new URL(ot);
			Method m = oyoahaClass.getMethod("setOyoahaTheme", new Class[] { java.net.URL.class });
			m.invoke(lf, new Object[] { theme });
		}

		UIManager.setLookAndFeel((LookAndFeel) lf);
	}

	private static void initSkinLF(Class skinLFClass, URLClassloader classLoader) throws Exception
	{
		Object skin = null;
		String ot = Preferences.getString("lf", "skinlftheme",null);
		if (ot == null)
		{
			URL theme;
			//if (Start.programURL != null)
			theme = new URL(Start.programURL, "plugins/lf/" + "themepack.zip");
			//else theme = new URL(Start.localURL, "plugins/lf/" + "themepack.zip");
			skin = loadSkinLFTheme(theme, skinLFClass);
		}
		else if (ot.endsWith(".zip"))
		{
			skin = loadSkinLFTheme(new URL(ot), skinLFClass);
		}
		else
		{
			URL metalTheme = new URL(ot);
			Method m = skinLFClass.getMethod("loadSkin", new Class[] { java.net.URL.class });
			skin = m.invoke(null, new Object[] { metalTheme });
		}
		
		Class skinClass = classLoader.loadClass("com.l2fprod.gui.plaf.skin.Skin");

		Method m = skinLFClass.getMethod("setSkin", new Class[] { skinClass });
		skin = m.invoke(null, new Object[] { skin });

		Object lf = skinLFClass.newInstance();
		UIManager.setLookAndFeel((LookAndFeel) lf);
	}

	private static Object loadSkinLFTheme(URL theme, Class skinLFClass) throws Exception
	{
		Method m = skinLFClass.getMethod("loadThemePack", new Class[] { java.net.URL.class });
		return m.invoke(null, new Object[] { theme });
	}

	//	private void searchLookAndFeels()
	//	{//dynamic look and feel find ipv with file?
	//		//voeg samen met plugindata?
	//		String urlString = Start.path + "plugins" + File.separator + "lf";
	//		//String urlString = "z:/data/java//plugins/newplugins";
	//		//urlString += dir + "/";
	//		System.out.println(urlString);
	//		File path = new File(urlString);
	//		File file[] = path.listFiles();
	//		if (file == null)
	//		{
	//			System.out.println("look and feels available");
	//			return;
	//		}
	//		for (int tel = 0; tel < file.length; tel++)
	//		{
	//			File currentFile = file[tel];
	//			if (currentFile.toString().endsWith(".jar"))
	//			{
	//				//find jars
	//				System.out.println(currentFile);
	//				//getEmoticonInfo(currentFile);
	//			}
	//		}
	//	}

	public static Map loadLookAndFeelData() throws IOException
	{
		Map lookAndFeels = new HashMap();
		InputStream data = null;
		try
		{
			//if (Start.programURL != null)
			if (new File(Start.path + "plugins" + File.separator + "lf").exists())
			{
				data = new FileInputStream(Start.path + "plugins" + File.separator + "lf" + File.separator + "lookandfeel.cfg");
			}
			else
			{
				//loadremote plugins
				data = (new URL(Start.programURL + "plugins/lf/lookandfeel.cfg")).openStream();
			}
				
			BufferedReader reader = new BufferedReader(new InputStreamReader(data));
	
			StringBuffer buffer = new StringBuffer();
			//for(int in2 =reader.read();in2!=-1;in2= reader.read())
			while(reader.ready())
			{	
				char ca = (char) reader.read();
				if (ca == ' ')
				{
					lookAndFeels.put(buffer.toString(), reader.readLine());
					buffer = new StringBuffer();
				}
				else buffer.append(ca);
			}
		}
		finally 
		{
			if (data!=null)data.close();
		}
		return lookAndFeels;
	}

	
	public void unload(){}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
