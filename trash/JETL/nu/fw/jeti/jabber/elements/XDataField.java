// Created on 28-jul-2003
package nu.fw.jeti.jabber.elements;

import java.util.Iterator;
import java.util.List;

/**
 * @author E.S. de Boer
 *
 */
public class XDataField extends Extension
{

		private String desc;//multi add line end
		private List options; //more 2 each
		private boolean required=false;
		private String value; //multi add line end
		private String label;
		private String var;
		private String type; 
	 
//		 <xs:enumeration value='boolean'/>
//		 <xs:enumeration value='fixed'/>
//		 <xs:enumeration value='hidden'/>
//		 <xs:enumeration value='jid-multi'/>
//		 <xs:enumeration value='jid-single'/>
//		 <xs:enumeration value='list-multi'/>
//		 <xs:enumeration value='list-single'/>
//		 <xs:enumeration value='text-multi'/>
//		 <xs:enumeration value='text-private'/>
//		 <xs:enumeration value='text-single'/>
			
		public XDataField(String var, String value)
		{
			 this.var = var;
			 this.value = value;
		}
				 
		public XDataField(XDataFieldBuilder builder)
		{
			desc = builder.getDescription();
			required =builder.required;
			label = builder.label;
			var = builder.var;
			type = builder.type;
			options = builder.getOptions();
			value = builder.getValue();
		}
		
		public String getDescription()
		{
			return desc;
		}
		
		public boolean hasOptions()
		{
			return options!=null; 
		}
		
		public int getOptionsSize()
		{
			return options.size();  
		}
		
		/**
		 * returns options as array of Object[] the label is the first element
		 * the value the second  
		 * @return Object[]
		 */
		public Object[] getOptions()
		{
			return options.toArray(); 
		}
		
		/**
		 * returns options as Object[] the label is the first element
		 * the value the second  
		 * @return Iterator
		 */
		public Iterator getOptionsIterator()
		{
			return options.iterator(); 
		}
		
		public boolean getRequired()
		{
			return required; 
		}
		
		public String getValue()
		{
			return value;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public String getVar()
		{
			return var;
		}
		
		public String getType()
		{
			return type;
		}

		public void appendToXML(StringBuffer xml)
		{
			xml.append("<field");
			appendAttribute(xml,"label",label);
			appendAttribute(xml,"var",var);
			appendAttribute(xml,"type",type);
			xml.append(">");
			if(desc !=null) xml.append("<desc>" +desc+ "</desc>"); //addmultiline
			if(value !=null) xml.append("<value>"+ value + "</value>");//addmultiline
			if(required == true) xml.append("<required/>");
			if(options !=null)
			{
				for(Iterator i=options.iterator();i.hasNext();)
				{
					xml.append("<option");
					String[] option = (String[])i.next(); 
					if(option[0] != null) appendAttribute(xml,"label",option[0]);
					xml.append(">");
					if(option[1] != null) xml.append("<value>" + option[1] + "</value>");
					xml.append("</option>");
				}
			}
			xml.append("</field>");
		}
	}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
