package nu.fw.jeti.jabber.elements;

import nu.fw.jeti.jabber.Backend;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public interface IQExtension
{
	public void execute(InfoQuery iq,Backend backend);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
