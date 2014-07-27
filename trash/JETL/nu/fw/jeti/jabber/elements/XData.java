// Created on 26-jul-2003
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.List;

/**
 * @author E.S. de Boer
 * X-data
 */
public class XData extends Extension implements XExtension
{
	private String instructions; //multi add line end
	private String title;
	private String type;
	private List fields;
	//@todo make item & reported
	
	public XData(){}
	
	public XData(String type){this.type = type;}
	
	public XData(XDataBuilder builder)
	{
		instructions = builder.getInstructions();
		title = builder.title;
		type = builder.type;
		fields = builder.getFields();
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getInstructions()
	{
		return instructions;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public boolean hasFields()
	{
		return fields != null; 
		
	}

	public Iterator getFields()
	{
		return fields.iterator();
	}


	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns='jabber:x:data'");
		appendAttribute(xml,"type",type);
		xml.append(">");
		if(instructions !=null) xml.append("<instructions>"+ instructions + "</instructions>");
		if(title != null)xml.append("<title>" + title + "</title>");
		if(fields !=null)
		{
			for(Iterator i = fields.iterator();i.hasNext();)
			{
				((XDataField)i.next()).appendToXML(xml);
			}
		}
		xml.append("</x>");
	}
}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
