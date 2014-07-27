package nu.fw.jeti.plugins;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;

/**
 * @author E.S. de Boer
 *
 * 
 */
public interface FormattedMessage
{
	public void initXHTML(JTextPane txtUitvoer, JTextPane txtInvoer, JPanel pnlControl);

	List getWordList(Extension extension);
	
	void useXHTML(boolean useXHTMl,String name);
	
	List makeWordListFromDocument();
	
	XExtension getXHTMLExtension(List wordList);
	
	boolean sendXML();

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
