package nu.fw.jeti.plugins.groupchat.elements;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;

/**
 * @author Martin Forssen
 */
public interface IQMUCInterface {
    public void execute(InfoQuery iq, Backend backend, IQMUC muc);

    public void timeout();
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
