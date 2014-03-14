// Created on 26-jul-2003
package nu.fw.jeti.jabber.elements;

import java.util.LinkedList;
import java.util.List;

import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class XDataBuilder
{
	private String instructions; //multi add line end
	public String title;
	public String type;
	private List fields;

	public XDataBuilder()
	{
		reset();
	}
	
	public void reset()
	{
		instructions = null;
		title = null;
		type = null;
		fields = null;
	}
	
	/**
	 * adds instructions (multiple calls wil be added with an added end of line)
	 * @param instructions
	 */
	public void addInstructions(String instructions)
	{
		if(this.instructions == null) this.instructions = instructions; 
		else
		{
			this.instructions = this.instructions + "\n" + instructions;
		}
	}

	public String getInstructions()
	{
		return instructions;
	}
	
	public void addField(XDataField field)
	{
		if(fields== null) fields = new LinkedList();
		fields.add(field);  
	}

	public List getFields()
	{
		return fields;
	}
	
	public XData build() throws InstantiationException
	{
		if (type==null) throw new InstantiationException(I18N.gettext("main.error.No_type"));
		else if(type.equals("cancel") || type.equals("form") || type.equals("result") || type.equals("submit"));
		else throw new InstantiationException("Wrong type"); 
		return new XData(this);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
