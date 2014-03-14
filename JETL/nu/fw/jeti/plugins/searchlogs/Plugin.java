package nu.fw.jeti.plugins.searchlogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class Plugin implements Plugins
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "Provides search for the log files";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "searchlogs";
	public final static String ABOUT = "by E.S. de Boer, Uses the Lucene search engine";
	
        
    public static void init(final Backend backend)
    {
    	final JMenuItem item = new JMenuItem(I18N.gettext("Search Log files"));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new SearchWindow().show();
			}
		});
		backend.getMain().addToMenu(item);
		
    }

	public void unload() {}
	        
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
