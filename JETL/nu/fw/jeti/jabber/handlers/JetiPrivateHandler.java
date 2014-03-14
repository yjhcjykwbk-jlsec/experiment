package nu.fw.jeti.jabber.handlers;

import java.util.ArrayList;
import java.util.List;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.JetiExtensionBuilder;

import org.xml.sax.Attributes;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */




public class JetiPrivateHandler extends ExtensionHandler
{
	private JetiExtensionBuilder builder;

	private List tempList;

	public JetiPrivateHandler()
	{
		builder=new JetiExtensionBuilder();
	}

	public void startHandling(Attributes attributes)
	{
		builder.reset();
		tempList = new ArrayList(10);
		builder.reset();
		//attributes.
		for (int i = 0; i < attributes.getLength(); i++)
		{
			String name = attributes.getQName(i);
			if(name.equals("xmlVersion")) builder.setXmlVersion(attributes.getValue(i));
			else if(!name.equals("xmlns") && !attributes.getValue(i).equals("")) builder.put(name,attributes.getValue(i));
		}
	}

	public void startElement(String name,Attributes attributes)
	{// in attributes in volgede tags?
		//we have no attributes, so all we do is make sure the buffer is
		//reset for reading in character data.
		if(!name.equals("status"))
		{
			tempList = new ArrayList(10);
			//System.out.println("we hebben een pref start element");
			for (int i = 0; i < attributes.getLength(); i++)
			{
				//String name2 = attributes.getName(i);
				//System.out.println(name2  +" : " +attributes.getValue(i) );
				tempList.add(attributes.getValue(i));
				//if(!name.equals("xmlns")) builder.put(name,attributes.getValue(i));
			}
		}
	}

	public void endElement(String name)
	{// element = tussen tags
		//System.out.println("end element name" + name);
		if(!name.equals("status"))
		{
			//Collections.reverse(tempList);
			builder.putMessages(name,tempList);
		}
		//System.out.println(new String(elementChars));
	}

	public Extension build()
	{
		Extension e = builder.build();
		builder.reset();
		return e;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
