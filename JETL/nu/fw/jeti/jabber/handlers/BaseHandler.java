package nu.fw.jeti.jabber.handlers;
import org.xml.sax.Attributes;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

abstract public class BaseHandler
{
	private StringBuffer currentChars = new StringBuffer();

    public void characters(String text)
	{// append text to buffer because parser likes to cut text
		currentChars.append(text);
	}

	public String getText()
	{//return text between elements
	    return currentChars.toString().trim();
	}
	
	public StringBuffer getUntrimmedText()
	{//return text between elements
	    return currentChars;
	}

	public void clearCurrentChars()
	{
	    currentChars = new StringBuffer();
	}

	abstract public void startHandling(Attributes attr);

	//abstract public void build();

	public void endElement(String name){clearCurrentChars();}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
