package nu.fw.jeti.plugins.windowsutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JMenuItem;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.NativeUtils;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class Plugin implements Plugins, NativeUtils
{
	public final static String VERSION = "0.2";
	public final static String DESCRIPTION = "windowsutils.Provides_always_on_top,_transparency_and_flashing_windows";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "windowsutils";
	public final static String ABOUT = "by E.S. de Boer";
	private static WindowUtil util;
        
    public static void init(final Backend backend)
    {
    	//does this work??
    	System.setProperty("sun.java2d.noddraw",String.valueOf(Preferences.getBoolean("windowsutils","ddraw",true)));
		//-Dsun.java2d.noddraw=true
    	
		util = new WindowUtil();
		final JMenuItem item = new JMenuItem(I18N.gettext("windowsutils.Set_Always_On_Top"));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(item.getText().equals(I18N.gettext("windowsutils.Set_Always_On_Top")))
				{	
					item.setText(I18N.gettext("windowsutils.Not_Always_On_Top"));
					util.windowAlwaysOnTop(backend.getMainWindow(), true);
				}
				else
				{	
					item.setText(I18N.gettext("windowsutils.Set_Always_On_Top"));
					util.windowAlwaysOnTop(backend.getMainWindow(), false);
				}
			}
		});
		backend.getMain().addToMenu(item);
		if(is2000orGreater())
		{	
			final JMenuItem item2 = new JMenuItem(I18N.gettext("windowsutils.Make_transparent"));
			item2.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					if(item2.getText().equals(I18N.gettext("windowsutils.Make_transparent")))
					{	
						item2.setText(I18N.gettext("windowsutils.Make_opaque"));
						util.setWindowAlpha(backend.getMainWindow(),Preferences.getInteger("windowsutils","alpha", 70));
					}
					else
					{	
						item2.setText(I18N.gettext("windowsutils.Make_transparent"));
						util.setWindowAlpha(backend.getMainWindow(), 100);
					}
				}
			});
			backend.getMain().addToMenu(item2);
		}
    }
    
    private static boolean is2000orGreater()
    {
    	String os = System.getProperty("os.name");
    	return (os.equals("Windows 2000") || os.equals("Windows 2000") || os.equals("Windows XP") || os.equals("Windows 2003"));
    }
    
    public boolean supportsAlpha()
    {
    	return is2000orGreater();
    }
  
	public void flash(Component c, boolean flash)
	{
		util.flash(c,flash);
	}
	
	public void windowAlwaysOnTop(Component c, boolean flag)
	{
		util.windowAlwaysOnTop(c,flag);
	}
	
	public void setWindowAlpha(Component c, int alpha)
	{
		util.setWindowAlpha(c,alpha);	
	}

	public void unload() {}
	        
	public static void unload(Backend backend)
	{
		util = null;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
