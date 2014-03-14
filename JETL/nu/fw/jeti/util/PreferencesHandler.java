package nu.fw.jeti.util;

import org.xml.sax.helpers.*;
import org.xml.sax.*;
import java.util.*;

/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class PreferencesHandler extends DefaultHandler
{
	
	private Map preferences;
	private Map messages;
	private StringBuffer text= new StringBuffer();
    private String show = "available";

	public PreferencesHandler(Map preferences, Map messages)
	{
		this.preferences = preferences; 
        this.messages = messages;
	}

	public void startDocument()
	{
	}

	public void endDocument()
	{
	}

	public void startElement(String namespaceURI,
							String sName, // simple name
							String qName, // qualified name
							Attributes attrs)							
	{
		if(qName.equals("plugin"))
		{
			String type = attrs.getValue("type");
			String name = attrs.getValue("name");
			Object[] temp = new Object[6];
			temp[0] = name;
			temp[1] = Boolean.valueOf(attrs.getValue("enabled"));
			temp[3] = attrs.getValue("transport");

            List l = Preferences.getPlugable(type);
            for (int i=0; i<l.size(); i++) {
                Object[] t = (Object[])l.get(i);
                if (name.equals(t[0])) {
                    l.remove(i--);
                }
            }
			l.add(temp);
		}
		else if(qName.equals("preference"))
		{
			String key = attrs.getValue("key");
			String value = attrs.getValue("value");
			preferences.put(key,value);
		}
		else if(qName.equals("messages"))
		{
			show = attrs.getValue("show");
            if (show == null) {
                show = "available";
            }
		}
		else if(qName.equals("message"))
		{
			String text = attrs.getValue("text");
            List list = (List)messages.get(show);
            if (list == null) {
                list = new ArrayList(10);
            }
            list.add(text);
            messages.put(show, list);
		}
	}


	public void endElement(String namespaceURI,
						   String sName, // simple name
						   String qName  // qualified name
							)
	{
        text = new StringBuffer();
	}

	public void characters(char buf[], int offset, int Len)
	{
		   text.append(buf, offset, Len);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
