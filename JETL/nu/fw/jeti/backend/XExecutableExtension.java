// Created on 14-okt-2003
package nu.fw.jeti.backend;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Packet;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 * Exectutable Xextension
 * this extension can be executed to allow Xextension in plugins
 */
public interface XExecutableExtension extends XExtension
{
	public void execute(Packet packet,final Backend backend);
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
