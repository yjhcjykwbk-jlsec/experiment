package nu.fw.jeti.events;

import nu.fw.jeti.jabber.elements.XMessageEvent;

/**
 * The listener interface for receiving message events.
 * @author E.S. de Boer
 * @version 1.0
 */
//verbeter met ids etc
public interface MessageEventListener extends JETIListener
{
	void onComposing(nu.fw.jeti.jabber.JID jid,String thread,XMessageEvent messageEvent);

	void requestComposing(nu.fw.jeti.jabber.JID jid, String id,String thread);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
