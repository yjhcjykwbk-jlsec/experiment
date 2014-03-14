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
 *	Created on 3-sep-2004
 */
 
package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;

/**
 * @author E.S. de Boer
 *
 */
public class Conference extends Extension
{
	private String name;
	private JID jid;
	private String password;
	private String nick;
	private boolean autoJoin;
	
	public Conference(String name, JID jid,boolean autoJoin, String nick, String password)
	{
		this.name = name;
		this.jid = jid;
		this.autoJoin = autoJoin;
		this.nick = nick;
		this.password = password;
	}
	
	public JID getJid()
	{
		return jid;
	}
	public String getName()
	{
		return name;
	}
    public void setName(String name)
    {
        this.name = name;
    }
	public String getNick()
	{
		return nick;
	}
	public String getPassword()
	{
		return password;
	}
	
	public boolean autoJoins()
	{
		return autoJoin;
	}
    public void setAutoJoins(boolean autoJoin)
    {
        this.autoJoin = autoJoin;
    }
	
	public String toString()
	{
		return name;
	}
	
	public void appendToXML(StringBuffer retval)
	{
		retval.append("<conference");
		appendAttribute(retval,"jid",jid);
		appendAttribute(retval,"name",name);
		if(autoJoin) appendAttribute(retval,"autojoin","1");
		retval.append(">");
		appendElement(retval,"nick",nick);
		appendElement(retval,"password",password);
		retval.append("</conference>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
