// Created on 29-jul-2003
package nu.fw.jeti.jabber;

import nu.fw.jeti.jabber.elements.XData;

/**
 * @author E.S. de Boer
 *
 */
public interface XDataCallback
{
	void sendForm(XData  xdata);
	
	void cancelForm();
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
