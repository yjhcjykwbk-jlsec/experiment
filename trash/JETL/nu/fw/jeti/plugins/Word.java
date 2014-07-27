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
 *  or mail me at eric@jeti.tk
 *
 *	Created on 13-feb-2004
 */
 
package nu.fw.jeti.plugins;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * @author E.S. de Boer
 *
 */
public class Word implements Cloneable
{//TODO check attributes return not null ipv fontsize plugin
	public String word;
	private MutableAttributeSet attributes;
	private MutableAttributeSet mas;

	public Word(StringBuffer word)
	{
		this .word = word.toString();
	}
	
	public Word(StringBuffer word,MutableAttributeSet al)
	{
		this .word = word.toString();
		this.attributes = al;
	}
	
	public Word(String word)
	{
		this .word = word;
	}
	
	public Word(String word,MutableAttributeSet al)
	{
		this .word = word;
		this.attributes = al;
	}
	
	public void addAttributes(AttributeSet attr)
	{
		if(mas==null)
		{	
			mas = new SimpleAttributeSet();
			if(attributes!=null)mas.addAttributes(attributes);
		}
		mas.addAttributes(attr);
	}
	
	public MutableAttributeSet getAttributes()
	{
		if(mas!=null) return mas;
		if(attributes== null) attributes = new SimpleAttributeSet();
		return attributes;
	}
	
	public String toString()
	{
		return word;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		Word w = (Word) super.clone();
		if(attributes!=null)w.attributes = new SimpleAttributeSet(attributes);
		if(mas!=null)w.mas = new SimpleAttributeSet(mas);
		return w;
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
