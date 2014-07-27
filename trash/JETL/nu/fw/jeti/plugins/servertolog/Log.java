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
 *	Created on 31-mei-2004
 */
 
package nu.fw.jeti.plugins.servertolog;


import nu.fw.jeti.jabber.JID;

/**
 * @author Eric
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Log
{
	private String startTime;
	private JID from;
	private StringBuffer text = new StringBuffer();
	
	public Log(JID from, String startTime)
	{
		this.startTime = startTime;
		this.from = from;
	}
	
	public void write(String text)
	{
		this.text.append(text);
	}
	
	public void newLine()
	{
		this.text.append('\n');
	}
	
	public JID getFrom()
	{
		return from;
	}
	
	public String getStartTime()
	{
		return startTime;
	}
	
	public String getText()
	{
		return text.toString();
	}
	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
