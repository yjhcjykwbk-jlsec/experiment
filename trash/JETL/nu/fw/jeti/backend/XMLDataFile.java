package nu.fw.jeti.backend;

import java.util.Map;

/**
 * <p>Title: J²M</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * Saves data to a XML file, it adds tabs to the xml using the open and close tag methods
 * @author E.S. de Boer
 * @version 1.0
 */

public abstract class XMLDataFile
{
	int tabDepth=0;

    public XMLDataFile()
    {
    }

	private void addTabs(StringBuffer xml)
	{
		xml.append("\r\n");
		for(int i =0;i<tabDepth;i++)
		{
			xml.append("    ");
		}
	}

	/**
	 * <code>toString</code> is a serializer for the data contained in the
	 * object to an equivalent snippet of XML stream.
	 *
	 * @return a <code>String</code> value which contains the XML
	 * representation of this object
	 */
	public String toString()
	{
		StringBuffer retval=new StringBuffer();
		appendToXML(retval);
		return new String(retval);
	}

	/**
	* <code>appendToXML</code> appends the XML representation of the
	* current packet data to the specified <code>StringBuffer</code>.
	*
	* @param xml The <code>StringBuffer</code> to append to
	*/
    public abstract void appendToXML(StringBuffer xml);

	protected final void appendHeader(StringBuffer xml)
	{
	   xml.append("<?xml version=\"1.0\"?>");
    }

	protected final void appendOpenTag(StringBuffer xml,String name)
	{
		//xml.append("\r\n");
		addTabs(xml);
		tabDepth++;
		xml.append(name);
	}

	protected final void appendCloseTag(StringBuffer xml,String name)
	{
		//xml.append("\r\n");
		tabDepth--;
		addTabs(xml);
		xml.append(name);

	}

	protected final void appendCloseSymbol(StringBuffer xml)
	{
		tabDepth--;
		xml.append("/>");
	}

	protected final boolean appendElement(StringBuffer xml,String name,String value)
	{
	   //xml.append("\r\n");
	   addTabs(xml);
	   boolean result = HelpXMLData.appendElement(xml,name,value);
	   //xml.append('\n');
	   return result;
	}

   protected final boolean appendElement(StringBuffer xml,String name,boolean value)
   {
	   //xml.append("\r\n");
	   addTabs(xml);
	   return HelpXMLData.appendElement(xml,name,value);
   }

   protected final boolean appendElement(StringBuffer xml,Map map)
   {
	   //xml.append("\r\n");
	   addTabs(xml);
	   return HelpXMLData.appendElement(xml,map);
   }

   protected final boolean appendAttribute(StringBuffer xml,String name,String value)
   {
	  return HelpXMLData.appendAttribute(xml,name,value);
   }

   protected final boolean appendAttribute(StringBuffer xml,String name,Object value)
   {
	   return HelpXMLData.appendAttribute(xml,name,value);
   }

   abstract static class HelpXMLData extends XMLData
   {

   }

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
