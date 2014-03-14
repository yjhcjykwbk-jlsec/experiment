// Created on 28-apr-2003
package nu.fw.jeti.plugins.groupchat.elements;

import java.text.MessageFormat;

import nu.fw.jeti.backend.XExecutableExtension;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.Packet;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.groupchat.GroupchatWindow;
import nu.fw.jeti.plugins.groupchat.Plugin;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;


/**
 * @author E.S. de Boer
 *
 */
public class XConference extends Extension implements XExecutableExtension
{
	private JID room;

	public XConference()
	{}

	public XConference(JID room)
	{
		this.room = room;
	}

	public JID getRoom()
	{
		return room;
	}
	
	public void execute(Packet packet,final Backend backend)	
	{
		Popups.OptionChoosed choose = new Popups.OptionChoosed()
		{
			public void optionChoosed(int option)
			{
				if (option == javax.swing.JOptionPane.NO_OPTION)
				{
					JID temp = getRoom();  
					JID jid = new JID(temp.getUser(),temp.getDomain(),backend.getMyJID().getUser());
					backend.send(new Presence(jid,"available"));//,new XMUC()));
                    GroupchatWindow gcw = Plugin.getGroupchat(jid, backend);
					gcw.show();
				} 
			}
		};

		Object[] options = { I18N.gettext("groupchat.Decline") , I18N.gettext("groupchat.Accept") };
		Popups.showOptionDialog(
			MessageFormat.format(I18N.gettext("groupchat.{0}_invites_you_to_{1}"), new Object[]{packet.getFrom().getUser(),getRoom()}),
					I18N.gettext("groupchat.Groupchat_Invitation"),
			javax.swing.JOptionPane.YES_NO_OPTION,
			javax.swing.JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1],
			choose);
	}
	

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns= 'jabber:x:conference'");
			//xml.append(body);
		appendAttribute(xml,"jid",room);
		xml.append("/>");
	}

}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
