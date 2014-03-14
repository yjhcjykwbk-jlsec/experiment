package nu.fw.jeti.plugins.xhtml;

import java.awt.Color;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;
import nu.fw.jeti.plugins.Word;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class XHTML extends Extension implements XExtension
{
	private String body;
	private List wordList;
	
	public XHTML(){}

	public XHTML(List wordList)
	{
		this.wordList = wordList;
		//this.body = "<body>" + body + "</body>";
	}
	
	public XHTML(String body,List text)
	{
		this.body = body;
		this.wordList = text;
	}
	
	public List getWordList()
	{
		return wordList;
	}
		
	public String getBody(){return body;}

	public void appendToXML(StringBuffer xml)
	{
		if(body==null) generateBody();
		xml.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		xml.append(body);
		xml.append("</html>");
	}
	
	private void generateBody()
	{
		StringBuffer temp = new StringBuffer();
		temp.append("<body>");
		parseElement(temp,0,wordList.size(),false);
		temp.append("</body>");
		body=temp.toString();
	}
	
	
	
	
	/************************Methods to convert wordlist to xml*************************/
	
	private void parseElement(StringBuffer xhtml,int start, int end,boolean openStyle)
	{//msn transport needs lettertype + color in 1 style to work
		if(start>=end)return;
		AttributeSet set = ((Word)wordList.get(start)).getAttributes();
		int newEnd =0;
		if(set==null);//remove empty set
		else if (set.isDefined(StyleConstants.FontFamily))
		{	
			String insert ="font-family: " + StyleConstants.getFontFamily(set);
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.FontFamily);
//			xhtml.append("<span style=\"");
//		xhtml.append();
//		Object value = set.getAttribute(StyleConstants.FontFamily);
//		newEnd = getStyleEnd(StyleConstants.FontFamily,value,start,end);
//		parseElement(xhtml, start, newEnd,true);
//		xhtml.append("</span>");
			
			
			openStyle=false;
		}
		else if (set.isDefined(StyleConstants.FontSize))
		{	
			String insert =" font-size: " + StyleConstants.getFontSize(set) + "pt";
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.FontSize);
			openStyle=false;
		}
		else if (set.isDefined(StyleConstants.Foreground))
		{
			String hex = colorToHexString((Color)set.getAttribute(StyleConstants.Foreground));
			String insert =" color: #" + hex;
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.Foreground);
			openStyle=false;
			
//			Object value = set.getAttribute(StyleConstants.Foreground);
//			if(!set.isDefined(StyleConstants.Foreground+value.toString()))
//			{	
//				newEnd = getStyleEnd(StyleConstants.Foreground,value,start,end);
//				if(openStyle)
//				{
// 					if(newEnd<end)xhtml.append("\"><span style=\"");
//					else xhtml.append(';');
//				}
//				else if(!openStyle) xhtml.append("<span style=\"");
//				String hex = colorToHexString((Color)value);
//				xhtml.append(" color: #" + hex);
//				if(newEnd != 0)
//				{
//					parseElement(xhtml, start, newEnd,true);
//				}
//				if(!openStyle)xhtml.append("</span>");
//				openStyle=false;
//			}
		}
		else if (set.isDefined(StyleConstants.Background))
		{
			String hex = colorToHexString((Color)set.getAttribute(StyleConstants.Background));
			String insert =" background-color: #" + hex;
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.Background);
			openStyle=false;
						
//			Object value = set.getAttribute(StyleConstants.Background);
//			if(!set.isDefined(StyleConstants.Background+value.toString()))
//			{	
//				xhtml.append("<span style=\"");
//				String hex = colorToHexString((Color)value);
//				xhtml.append("background: #" + hex);
//				newEnd = getStyleEnd(StyleConstants.Background,value,start,end);
//				if(newEnd != 0)
//				{
//					parseElement(xhtml, start, newEnd,true);
//				}
//				xhtml.append("</span>");
//			}
		}
		else if (set.isDefined(StyleConstants.StrikeThrough))
		{	
			String insert =" text-decoration: line-through";
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.StrikeThrough);
			openStyle=false;
			
//			Object value = set.getAttribute(StyleConstants.StrikeThrough);
//			if(!set.isDefined(StyleConstants.StrikeThrough+value.toString()))
//			{	
//				xhtml.append("<span style=\"");
//				xhtml.append("text-decoration: line-through");
//				newEnd = getStyleEnd(StyleConstants.StrikeThrough,value,start,end);
//				if(newEnd != 0)
//				{
//					parseElement(xhtml, start, newEnd,true);
//				}
//				xhtml.append("</span>");
//			}
		}
		else if (set.containsAttribute(StyleConstants.Underline,Boolean.TRUE))
		{
			String insert =" text-decoration: underline";
			newEnd = addStyle(xhtml, start, end, openStyle, set,insert,StyleConstants.Underline);
			openStyle=false;
			
//			Object value = set.getAttribute(StyleConstants.Underline);
//			xhtml.append("<span style=\"");
//			xhtml.append("text-decoration: underline");
//			newEnd = getStyleEnd(StyleConstants.Underline,value,start,end);
//			parseElement(xhtml, start, newEnd,true);
//			xhtml.append("</span>");
			
		}
		else if (StyleConstants.isBold(set))
		{
			if(openStyle)xhtml.append("; margin:0 \">");
			openStyle=false;
			xhtml.append("<strong>");
			Object value = set.getAttribute(StyleConstants.Bold);
			newEnd = getStyleEnd(StyleConstants.Bold,value,start,end);
			parseElement(xhtml, start, newEnd,false);
			xhtml.append("</strong>");
		}
		else if (StyleConstants.isItalic(set))
		{
			if(openStyle)xhtml.append("; margin:0 \">");
			openStyle=false;
			xhtml.append("<em>");
			Object value = set.getAttribute(StyleConstants.Italic);
			newEnd = getStyleEnd(StyleConstants.Italic,value,start,end);
			parseElement(xhtml, start, newEnd,false);
			xhtml.append("</em>");

		}
		if(openStyle)xhtml.append("; margin:0 \">");//margin because msn transport needs ; after all styles
		//no more styles so print this one
		if(newEnd==0)
		{
			StringBuffer b = new StringBuffer();
			escapeString(b, wordList.get(start).toString());
			xhtml.append(b);
			if(start+1<end)parseElement(xhtml, start+1, end,false);
		}//parse words after these words
		else if(newEnd<end)parseElement(xhtml, newEnd, end,false);
			
	}

	private int addStyle(StringBuffer xhtml, int start, int end, boolean openStyle, AttributeSet set,String insert,Object styleConstant)
	{
		int newEnd;
		Object value = set.getAttribute(styleConstant);
		newEnd = getStyleEnd(styleConstant,value,start,end);
		if(openStyle)
		{
			if(newEnd<end)xhtml.append("\"><span style=\"");
			else xhtml.append(';');
		}
		else xhtml.append("<span style=\"");
		xhtml.append(insert);
		parseElement( xhtml, start, newEnd,true);
		if(!openStyle)xhtml.append("</span>");
		return newEnd;
	}

	private int getStyleEnd(Object styleConstant,Object value, int start, int end)
	{
		for(int i=start;i<end;i++)
		{
			MutableAttributeSet set =((Word)wordList.get(i)).getAttributes();
			if(set==null)return i;
			if(set.isDefined(styleConstant))
			{	
				if(value.equals(set.getAttribute(styleConstant)))
				{
					set.removeAttribute(styleConstant);
					//set.addAttribute(styleConstant+value.toString(), value);
				}
			}
			else return i;
		}
		return end;
	}
	
	private static final char[] HEX_DIG = {
						'0', '1', '2', '3', '4', '5', '6', '7',
						'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
						};
	
	public String colorToHexString(Color c) 
	{
		int rgb = c.getRGB();
		char[] cdata = new char[6];
		for (int i = 0; i < 6; i++) {
			cdata[5-i] = HEX_DIG[rgb & 0xF];
			rgb = rgb >> 4;
		}
		return new String(cdata);
	}
	
//	  /**
//   * resolve sets of attributes that are recursively stored in each other
//   *
//   * @param style  the set of attributes containing other sets of attributes
//   */
//  public static AttributeSet resolveAttributes(AttributeSet style) {
//    SimpleAttributeSet set = new SimpleAttributeSet();
//    if(style != null) {
//      Enumeration names = style.getAttributeNames();
//      Object value;
//      Object key;
//      while(names.hasMoreElements()) {
//        key = names.nextElement();
//        //System.out.println("Util resolveAttributes key=" + key);
//        value = style.getAttribute(key);
//        //System.out.println("Util resolveAttributes value=" + value);
//        if( (!key.equals(StyleConstants.NameAttribute)) &&
//            (!key.equals(StyleConstants.ResolveAttribute)) &&
//            (!key.equals(AttributeSet.ResolveAttribute)) &&
//            (!key.equals(AttributeSet.NameAttribute)))
//        {
//          set.addAttribute(key, value);
//        }
//        else {
//          if(key.equals(StyleConstants.ResolveAttribute) ||
//             key.equals(AttributeSet.ResolveAttribute)) {
//            //System.out.println("Util resolveAttributes resolving key=" + key);
//            set.addAttributes(resolveAttributes((AttributeSet) value));
//          }
//        }
//      }
//    }
//    return set;
//  }
	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
