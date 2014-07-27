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
 *	Created on 10-aug-2004
 */
 
package nu.fw.jeti.plugins.fontsize;



import java.util.Iterator;
import java.util.List;

import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.plugins.Translator;
import nu.fw.jeti.plugins.Word;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, Translator
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "fontsize.change_the_fontsize_of_the_chatwindows";
	public final static String MIN_JETI_VERSION = "0.5.4";
	public final static String NAME = "fontsize";
	public final static String ABOUT = "by E.S. de Boer";
		
	public Plugin() {}
        
    public static void init(Backend backend)
    {
           PluginsInfo.setTranslator(new Plugin());
    }
           
    
    public void unload(){}
    
    public static void unload(Backend backend)
    {
    	PluginsInfo.setTranslator(null);
    }
    
    public void init(JTextComponent text) {}
	
	public void translate(List wordList)
	{
			for(Iterator i = wordList.iterator();i.hasNext();)	
			{
				Word word = (Word)i.next();
				StyleConstants.setFontSize(word.getAttributes(),Preferences.getInteger("fontsize", "font-size", 14));
			}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
