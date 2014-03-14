// Created on 27-jul-2003
package nu.fw.jeti.jabber.elements;

import java.util.LinkedList;
import java.util.List;

/**
 * @author E.S. de Boer
 *
 */
public class XDataFieldBuilder
{
	private String desc;//multi add line end
	public String label;
	public String var;
	public String type;
	private List options;
	public boolean required=false;
	private String value; //multi add line end
	
	public void reset()
	{
		desc = null;
		label = null;
		var=null;
		type=null;
		options = null;
		required=false;
		value=null;
	}

	/**
	 * adds a description (multiple calls wil be added with an added end of line)
	 * @param description
	 */
	public void addDescription(String description)
	{
		if(desc == null) desc = description; 
		else
		{
			desc = desc + "\n" + description;
		}
	}

	public String getDescription()
	{
		return desc;
	}
	
	/**
	 * adds a value (multiple calls wil be added with an added end of line)
	 * @param value
	 */
	public void addValue(String value)
	{
		if(this.value == null) this.value = value; 
		else
		{
			this.value = this.value + "\n" + value;
		}
	}

	public String getValue()
	{
		return value;
	}
	
	/**
	 * adds an option
	 * @param value
	 */
	public void addOption(String value)
	{
		addOption(null,value);
	}
	
	/**
	 * adds an option
	 * @param label optional label
	 * @param value
	 */
	public void addOption(String label, String value)
	{
		if(options == null) options = new LinkedList(); 
		options.add(new String[]{label,value});
	}

	/**
	 * returns options as a List containing String[]
	 * with on position 0 the label and on position 1 the value
	 * @return List
	 */
	public List getOptions()
	{
		return options;	
	}
	
	public XDataField build()
	{
		checkType();
		return new XDataField(this);
	}
	
	private void checkType() 
	{
		if(type == null) type = "text-single";
		else if(type.equals("boolean") || type.equals("fixed") || type.equals("hidden") || type.equals("jid-multi")|| type.equals("jid-single")|| type.equals("list-multi")|| type.equals("list-single")|| type.equals("text-multi")|| type.equals("text-private")|| type.equals("text-single"));
		else  type = "text-single";
	}
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
