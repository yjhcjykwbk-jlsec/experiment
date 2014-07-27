/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 23-feb-2004
 */
 
package nu.fw.jeti.plugins.wordfilter;

import java.util.Iterator;
import java.util.List;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.Word;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "Replaces bad words with ***";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "wordfilter";
	public final static String ABOUT = "by E.S. de Boer";

	public static void init(Backend backend)
	{//load wordlist
		
	}
	
	public void unload(){}

	public void translate(List wordList)
	{
		for (Iterator iter = wordList.iterator(); iter.hasNext();)
		{
			Word word =(Word) iter.next();
		
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
