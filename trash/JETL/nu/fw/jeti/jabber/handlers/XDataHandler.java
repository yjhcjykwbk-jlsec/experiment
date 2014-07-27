// Created on 27-jul-2003
package nu.fw.jeti.jabber.handlers;

import org.xml.sax.Attributes;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XDataBuilder;
import nu.fw.jeti.jabber.elements.XDataFieldBuilder;

/**
 * @author E.S. de Boer
 *
 */
public class XDataHandler extends ExtensionHandler
{//add item and reported
	private XDataBuilder builder;
	private XDataFieldBuilder fieldBuilder;
	private String label;
	private String value;
	boolean inOption;

	public XDataHandler()
	{
		builder = new XDataBuilder();
		fieldBuilder = new XDataFieldBuilder();
	}

	public void startHandling(Attributes attr)
	{
		inOption=false;
		label=null;
		value=null;
		builder.reset();
		builder.type = attr.getValue("type");
	}

	public void startElement(String name,Attributes attr)
	{
		if(name.equals("field"))
		{
			fieldBuilder.reset();
			fieldBuilder.label =attr.getValue("label");
			fieldBuilder.var = attr.getValue("var");
			fieldBuilder.type = attr.getValue("type");
		}
		else if(name.equals("option"))
		{
			inOption = true;
			label = attr.getValue("label");
		}
		//else if(!name.equals("title") || !name.equals("instructions")  ) nu.fw.jeti.util.Log.notParsedXML("roster " + name);
	}

	public void endElement(String name)
	{
		if(name.equals("field"))
		{
			builder.addField(fieldBuilder.build());
		}
		else if(name.equals("title"))
		{
			builder.title = getText();  
		}
		else if(name.equals("instructions"))
		{
			builder.addInstructions(getText());
		}
		else if(name.equals("desc"))
		{
			fieldBuilder.addDescription(getText()); 
		}
		else if(name.equals("required"))
		{
			fieldBuilder.required = true;
		}
		else if(name.equals("option"))
		{
			fieldBuilder.addOption(label,value);
			inOption=false;
			label=null;
			value=null;
		}
		else if(name.equals("value"))
		{
			if(inOption == true) value = getText();
			else fieldBuilder.addValue(getText()); 
		}
		else nu.fw.jeti.util.Log.notParsedXML("XData " + name + getText());
		clearCurrentChars();
	}

	public Extension build() throws InstantiationException 
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
