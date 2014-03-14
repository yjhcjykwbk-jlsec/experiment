package nu.fw.jeti.events;

import java.util.Date;

import javax.swing.text.Document;

import nu.fw.jeti.jabber.JID;

/**
 * Listener interface signalling end of a chat session, used to save chat
 * 27-4-2004
 * @author E.S. de Boer
 * @version 1.0
 */


public interface ChatEndedListener extends JETIListener
{
	/**
	 * Called when a chat session has ended
	 * @param jid The JID of the chat session (room jid or person)
	 */
	void chatEnded(JID jid);
}




/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
