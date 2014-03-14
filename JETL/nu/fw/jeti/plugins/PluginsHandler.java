package nu.fw.jeti.plugins;

import org.xml.sax.helpers.*;
import java.util.*;

/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class PluginsHandler extends DefaultHandler
{
	private List plugins;
	private StringBuffer text= new StringBuffer();
	private Object[] current;
	private boolean found = false;

    public PluginsHandler(List plugins)
    {
		this.plugins = plugins;
    }

	//public void startDocument()
	

	//public void endDocument()
	
//	public void startElement(String namespaceURI,
//							String sName, // simple name
//							String qName, // qualified name
//							Attributes attrs)
//							throws SAXException
//	{
//	  // if(qName.equals("icon")) texts = new LinkedList();
//		/*
//		if(qName.equals("plugin"))
//		{
//			current = new Object[6];
//			current[1] = new Boolean(false);
//		}
//		*/
//	}


	public void endElement(String namespaceURI,String sName,String qName)
	{

		if(qName.equals("name"))
		{//search name
			String name = text.toString().trim();
			for(Iterator i = plugins.iterator();i.hasNext();)
			{
				Object[] temp = (Object[])i.next();
				if(name.equals(temp[0]))
				{
					current = temp;
					found = true;
					break;
				}
			}
			if(!found)
			{
				current = new Object[6];
				current[0] = name;
				current[1] = Boolean.FALSE;
				current[3] = "";
			}
		}
		else if(qName.equals("description")) current[2] = text.toString().trim();
		else if(qName.equals("version"))
		{
			String t = text.toString().trim();
			if(t.equals("")) current[3] = text;
//			else if (current[3]==null) current[3]
		}
		else if(qName.equals("min_jeti_version")) current[4] = text.toString().trim();
		else if(qName.equals("parent")) current[5] = text.toString().trim();
		else if(qName.equals("plugin"))
		{
			if(!found)	plugins.add(current);
			else found = false;
		}
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
