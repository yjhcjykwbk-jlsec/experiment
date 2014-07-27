/*
 *   License
 *
 * The contents of this file are subject to the Jabber Open Source License
 * Version 1.0 (the "License").  You may not copy or use this file, in either
 * source code or executable form, except in compliance with the License.  You
 * may obtain a copy of the License at http://www.jabber.com/license/ or at
 * http://www.opensource.org/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 *   Copyrights
 *
 * Portions created by or assigned to Jabber.com, Inc. are
 * Copyright (c) 2000 Jabber.com, Inc.  All Rights Reserved.  Contact
 * information for Jabber.com, Inc. is available at http://www.jabber.com/.
 *
 * Portions Copyright (c) 1999-2000 David Waite
 *
 *   Acknowledgements
 *
 * Special thanks to the Jabber Open Source Contributors for their
 * suggestions and support of Jabber.
 *
 *   Changes
 *   renamed/added methods
 */
package nu.fw.jeti.backend;

import java.util.Map;
import java.util.Iterator;

/**
 * an XMLData object is the root class of a heirarchy of objects, all which
 * serialize themselves to XML data.
 *
 * @author  David Waite <a href="mailto:dwaite@jabber.com">
 *                      <i>&lt;dwaite@jabber.com&gt;</i></a>
 * @author  Author: mass
 * @author  Author: E.S. de Boer
 * @version Revision: 2
 */
public abstract class XMLData
{
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

    /**
     * <code>appendElement</code> adds the XML for a child 'element' to a
     * StringBuffer, as a helper to appendItem.
     *
     * @param xml a <code>StringBuffer</code> value
     * @param name a <code>String</code> value
     * @param value a <code>String</code> value
     *
     * @return a <code>bool</code> which is true if the element was generated.
     *         This is used for any shortcuts in the outputted XML
     */
    protected static final boolean appendElement(StringBuffer xml,String name,String value)
    {
        if (value==null) return false;
        xml.append( '<' );
        xml.append(name);
        if(value.equals(""))
		{//shortcut
			xml.append("/>" );
			return true;
		}
		xml.append( '>' );
        escapeString(xml, value);
        xml.append( "</" );
        xml.append(name);
        xml.append( '>' );
	    return true;
    }
    
    
	/**
		 * <code>appendElement</code> adds the XML for a child 'element' to a
		 * StringBuffer, as a helper to appendItem.
		 *
		 * @param xml a <code>StringBuffer</code> value
		 * @param name a <code>String</code> value
		 * @param value a <code>String</code> value
		 *
		 * @return a <code>bool</code> which is true if the element was generated.
		 *         This is used for any shortcuts in the outputted XML
		 */
		protected static final boolean appendElement(StringBuffer xml,String name,Object value)
		{
			if (value==null) return false;
			return appendElement(xml,name,value.toString()); 
		}

    /**
     * <code>appendElement</code> outputs an element if needed. If value=false
     * it does not output the tag.
     *
     * @param xml a <code>StringBuffer</code> value
     * @param name a <code>String</code> value
     * @param value a <code>boolean</code> value
     *
     * @return a <code>bool</code> which is true if the element was generated.
     *         This is used for any shortcuts in the outputted XML
     */
    protected static final boolean appendElement(StringBuffer xml,String name,boolean value)
    {
        if (value==false)return false;
        xml.append( '<' );
        xml.append(name);
        xml.append( "/>" );
	    return true;
    }

	/**
	 * <code>appendElement</code> adds a the entry's of a map to the stingbuffer
	 *
	 * @param xml a <code>StringBuffer</code> value
	 * @param map a <code>Map</code> value
	 *
	 * @return a <code>bool</code> which is true if the element was generated.
	 *         This is used for any shortcuts in the outputted XML
	 */
	protected static final boolean appendElement(StringBuffer xml,Map map)
	{
		if (map == null) return false;
		for (Iterator i=map.entrySet().iterator(); i.hasNext(); )
		{
			Map.Entry e = (Map.Entry) i.next();
			String name = (String)e.getKey();
			String value =(String)e.getValue();

			if (value==null) continue;
			xml.append( '<' );
			xml.append(name);
			if(value.equals(""))
			{//shortcut
				xml.append("/>" );
				continue;
			}
			xml.append( '>' );
			escapeString(xml, value);
			xml.append( "</" );
			xml.append(name);
			xml.append( '>' );
		}
		return false;
    }

    /**
     * <code>appendAttribute</code> outputs an attribute if needed. If value=null
     * it will not output the attribute. Note that it outputs in the format:<p>
     * &qout;&nbsp;<i>attrib</i>=<i>value</i>&qout;, or it puts a proceeding
     * space in front of the attribute, and no trailing space. This is for
     * optimizing the XML generation.
     *
     * @param xml a <code>StringBuffer</code> value
     * @param name a <code>String</code> value
     * @param value a <code>boolean</code> value
     *
     * @return a <code>bool</code> which is true if the attribute was
     * generated. This is used for any shortcuts in the outputted XML
     */
    protected static final boolean appendAttribute(StringBuffer xml,String name,String value)
    {
		if (value==null) return false;
		xml.append( ' ' );
		xml.append(name );
		xml.append("=\"");
		escapeString(xml,value);
		xml.append("\"");
		return true;
    }

    /**
     * <code>appendAttribute</code> outputs an attribute if needed. If value=null
     * it will not output the attribute. Note that it outputs in the format:<p>
     * &qout;&nbsp;<i>attrib</i>=<i>value</i>&qout;, or it puts a proceeding
     * space in front of the attribute, and no trailing space. This is for
     * optimizing the XML generation.
     *
     * @param xml a <code>StringBuffer</code> value
     * @param name a <code>String</code> value
     * @param value a <code>Object</code> value, which supports toString()
     *
     * @return a <code>bool</code> which is true if the attribute was
     * generated. This is used for any shortcuts in the outputted XML
     */
    protected static final boolean appendAttribute(StringBuffer xml,String name,Object value)
    {
		if (value==null) return false;
	    return appendAttribute(xml,name,value.toString());
    }

    public static final void escapeString(StringBuffer xml, String data)
    {
    	char c;
		for (int i=0;i<data.length();i++)
		{
			int d = data.charAt(i);
			if(d>=32 || d==9 ||d==10 || d==13)
			{
				switch(c=data.charAt(i))
				{
					case '&':
					xml.append("&amp;");
					break;
					case '<':
					xml.append("&lt;");
					break;
					case '>':
					xml.append("&gt;");
					break;
					case '\'':
					xml.append("&apos;");
					break;
					case '\"':
					xml.append("&quot;");
					break;
					default:
					xml.append(c);
		        }
			}
	    }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
