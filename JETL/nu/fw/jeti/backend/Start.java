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

package nu.fw.jeti.backend;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.JFrame;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.fw.jeti.images.Icons;
import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.ui.Jeti;
import nu.fw.jeti.ui.LoginStatusWindow;
import nu.fw.jeti.ui.LoginWindow;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Log;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

import org.xml.sax.SAXException;
/**
 * @author E.S. de Boer
 */

public class Start 
{
	public static String VERSION = "0.6.1.1"; //not final because it gets hardcoded in .class
	//private static Class[] loadedPlugins2 = new Class[Plugins.TOTAL_PLUGINS];
	public static URL programURL; //url where the plugins are change to plugins url can use always also if local
	public static String path; //local path
	//public static URL localURL;
	public static boolean applet=false;
	//public static boolean applet=true;
	private SAXParser parser;
	private static Splash splash;
	private PluginsInfo pluginsInfo;
	private Backend backend;
	
	//private Popups popups;
	
		
	public Start(String urlString,Container container)
	{
		try
		{
			parser = SAXParserFactory.newInstance().newSAXParser();
		}
		catch (FactoryConfigurationError ex)
		{
			ex.printStackTrace();
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
		}
		if (urlString == null)
			urlString = getPath();
		if(programURL==null)
		{
			try
			{
				programURL = new URL(urlString);
			}
			catch (MalformedURLException ex)
			{
				ex.printStackTrace();
			}
		}

		if (System.getProperty("file.separator").equals("/")) //unix? if mac then bug?
		{
			path = urlString.substring(5);
		}
		else
			path = urlString.substring(6);
		path = path.replace('/', File.separatorChar);
		System.out.println(path + "oi");

		//parentClassLoader = getClass().getClassLoader();
		
		backend = new Backend(this);
		new Preferences(backend, parser);
		I18N.init();
		//Preferences.initMessages();
		JFrame.setDefaultLookAndFeelDecorated(Preferences.getBoolean("jeti","javadecorations",false));
		//parse plugins? make better or make comments
		new StatusIcons(parser);
		Jeti jeti = new Jeti(backend,container);
		new Log(backend);
		new Popups(backend.getMainWindow());
		pluginsInfo = new PluginsInfo(backend,parser,this);

		jeti.init();
		if(container!=null) container.add(jeti);
		else backend.getMainWindow().show();
		
		//jeti.show();
		if(!applet)
		{
			//jeti.initPopups();
			LoginWindow.createLoginWindow(backend);
		}
		else 
		{
			LoginInfo loginInfo = nu.fw.jeti.applet.Jeti.loginInfo;
			if(loginInfo == null) new nu.fw.jeti.applet.LoginWindow(backend).show();
			else if(loginInfo.getUsername()==null) return;
			else if(loginInfo.getPassword()==null)
			{
				new nu.fw.jeti.applet.LoginWindow(backend).show();
			}
			else new LoginStatusWindow(loginInfo,backend,1);
		}
		//System.out.println("Start end");
	}
	
	public void setSplashText(String text) 
	{
		if(applet)nu.fw.jeti.applet.Jeti.from.setText(I18N.gettext("Loading") + " " + text + "...");
		else splash.from.setText(I18N.gettext("Loading") + " " + text + "...");
	}


	private String getPath()
	{
		// Now, search for and get the URL for this class.
		URL url = this.getClass().getResource("Start.class");
		//System.out.println(url);
		String urlString = null;

		try
		{ //remove %20 from program files etc
			urlString = URLDecoder.decode(url.toString(), "UTF8"); //encode if to url? probaly not
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} //1.2 error

		//System.out.println(url.getPath()  ); //werkt niet in 1.2
		//System.out.println(url);
		if (url.getProtocol().equals("jar"))
		{
			// Strip off JarURL-specific syntax.
			urlString = urlString.substring(4);
			urlString = urlString.substring(0, urlString.lastIndexOf("!"));
			urlString = urlString.substring(0, urlString.lastIndexOf("/") + 1);
		}
		else urlString = urlString.substring(0, urlString.lastIndexOf("/jeti") + 1);
		return urlString;
	}

	public static void remoteLoad(URL url, String path)
	{
		Start.path = path;
		Start.programURL = url;
		start(path,null);
	}
	
//	public static void appletLoad(URL url,LoginInfo info, Container container)
//	{
//		Start.remoteURL = url;
//		applet = true;
//		start(url.toString(),info,container);
//	}

	public static void main(String[] args)
	{
		start(null,null);
	}
	
	public static void start(String path,Container container)
	{
		Frame frame = new Frame();
		splash = new Splash(frame);
		new Start(path,container);
		splash.dispose();
		frame.dispose();
		splash=null;
	}
	
//	public void initPopups()
//	{
//		if(backend!=null) backend.getMain().initPopups();
//	}
	
	public Backend getBackend()
	{
		return backend;
	}
	
	public void close()
	{
		if(backend!=null) backend.getMain().close();
	}
	
	public void exit()
	{
		pluginsInfo.exit();
		if(applet)nu.fw.jeti.applet.Jeti.exit();
		backend=null;
	}

    static class Splash extends Window
    {
        Label from;
        public Splash(Frame frame)
        { 
            super(frame);
            Label label = new Label("Jeti is Loading"+"...");
            label.setFont(new java.awt.Font("Serif", 1, 40)); 
            label.setBackground(new Color(150,150,255));
            add(label,BorderLayout.CENTER);
            Panel panel = new Panel();
            panel.setBackground(new Color(255,255,150));
            from = new Label("(c) 2001-2004 E.S. de Boer");
            from.setBackground(new Color(255,255,150));
            panel.add(from);
            Label version = new Label("Version: " + Start.VERSION);
            version.setBackground(new Color(255,255,150));
            panel.add(version);
            add(panel,BorderLayout.SOUTH); 
            pack();
            setLocationRelativeTo(null);
            show();
        }
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
