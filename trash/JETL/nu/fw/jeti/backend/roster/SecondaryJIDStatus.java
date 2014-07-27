package nu.fw.jeti.backend.roster;

import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.*;
/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public interface SecondaryJIDStatus extends JIDStatus
{
	public void updatePresence(Presence presence);
	
	//public JIDStatus normalJIDStatus();

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
