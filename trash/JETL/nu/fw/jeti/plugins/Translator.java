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
 *	Created on 14-nov-2003
 */
 
package nu.fw.jeti.plugins;

import java.util.List;

import javax.swing.text.JTextComponent;

/**
 * @author E.S. de Boer
 *
 */
public interface Translator
{
	public void init(JTextComponent text);
	
	public void translate(List wordList);	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
