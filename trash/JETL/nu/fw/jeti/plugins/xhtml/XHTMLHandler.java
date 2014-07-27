package nu.fw.jeti.plugins.xhtml;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.Word;
import nu.fw.jeti.util.Log;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class XHTMLHandler extends ExtensionHandler
{
	private List wordList = new ArrayList();
	private SimpleAttributeSet currentAttributes;
	
	private StringBuffer body;

	public void startHandling(Attributes attr)
	{
		reset();
		body = new StringBuffer(); //attr.getValue("from");
	}

	private void reset()
	{
		body = null;
		wordList.clear();
		currentAttributes = new SimpleAttributeSet();
	}

	public void startElement(String name, Attributes attr)
	{
		addToWordList(getUntrimmedText());
		
		//create xhtml string to print in log window
		body.append(getText() + "<" + name + " ");
		for (int i = 0; i < attr.getLength(); i++)
		{
			body.append(attr.getQName(i) + "=\"" + attr.getValue(i) + "\"");
		}
		body.append(">");
		
		SimpleAttributeSet sas = new SimpleAttributeSet();
		sas.setResolveParent(currentAttributes);
		if(name.equals("body"))
		{
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("blockquote"))
		{//TODO indent does not work
			StyleConstants.setLeftIndent(sas, 10f);
			StyleConstants.setRightIndent(sas, 10f);
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
			wordList.add(new Word("\n",sas));
		}
		else if(name.equals("q"))
		{//quote cite=url attribute not implemented???
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
			wordList.add(new Word("'",sas));
		}
		else if(name.equals("pre"))
		{
			StyleConstants.setFontFamily(sas,"courier");
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("li") || name.equals("ol") || name.equals("ul"))
		{//TODO implement lists
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("h1"))
		{//todo h1 =?
			StyleConstants.setFontSize(sas,32);
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("h2"))
		{
			StyleConstants.setFontSize(sas,24);
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("h3"))
		{
			StyleConstants.setFontSize(sas,18);
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		
		
		else if(name.equals("a"))
		{//TODO link
			StyleConstants.setFontFamily(sas,"courier");
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		
		
		else if(name.equals("img"))
		{//TODO img
			StyleConstants.setFontFamily(sas,"courier");
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
	
		else if(name.equals("span") || name.equals("div") || name.equals("p")  )
		{
			parseStyle(attr.getValue("style"),sas);
			currentAttributes = sas;
		}
		else if(name.equals("em")|| name.equals("cite") )
		{
			StyleConstants.setItalic(sas,true);
			currentAttributes = sas;
		}
		else if(name.equals("strong"))
		{
			StyleConstants.setBold(sas,true);
			currentAttributes = sas;
		}
		else if(name.equals("code"))
		{//render in monospace font (courier)
			StyleConstants.setFontFamily(sas,"courier");
			currentAttributes = sas;
		}
		else
		{
			StringBuffer temp = new StringBuffer();
			temp.append("<" + name + " ");
			for (int i = 0; i < attr.getLength(); i++)
			{
				temp.append(attr.getQName(i) + "=\"" + attr.getValue(i) + "\"");
			}
			temp.append(">");
			Log.notParsedXML(temp.toString());
		}
		
		clearCurrentChars();
	}
	
	private void parseStyle(String styleAttributes,SimpleAttributeSet sas)
	{
		if(styleAttributes==null)return;
		String[] styles = styleAttributes.split(";");
		for(int i=0;i<styles.length;i++)
		{
			String[] temp = styles[i].split(":");
			if(temp.length!=2)continue;
			String style = temp[0].trim();
			String value = temp[1].trim();
			if(style.equals("color"))
			{
				if(value.length()!=7)
				{
					value+="000000";
					value =value.substring(0, 7);
				}
				Color color;
				try {
					color = Color.decode(value);
				}catch (NumberFormatException e)
				{
					color = Color.BLACK;
				}
				StyleConstants.setForeground(sas,color);
			}
			else if(style.equals("background-color"))
			{//not oficialy supported
				Color color;
				try {
					color = Color.decode(value);
				}catch (NumberFormatException e)
				{
					color = Color.WHITE;
				}
				StyleConstants.setBackground(sas,color);
			}
			else if(style.equals("font-family"))
			{
				StyleConstants.setFontFamily(sas,value);
			}
			else if(style.equals("font-size"))
			{
				int size;
				try 
				{
					size = 	Integer.parseInt(value.substring(0,value.length()-2));
				}
				catch (NumberFormatException e)
				{
					size = 12;
				}
				StyleConstants.setFontSize(sas,size);
			}
			else if(style.equals("text-decoration"))
			{//TODO overline || blink 
				if(value.equals("underline"))StyleConstants.setUnderline(sas, true);
				else if(value.equals("line-through"))StyleConstants.setStrikeThrough(sas, true);
				
			}
			else if(style.equals("text-align"))
			{
				if(value.equals("left"))StyleConstants.setAlignment(sas,StyleConstants.ALIGN_LEFT);
				else if(value.equals("rigth"))StyleConstants.setAlignment(sas,StyleConstants.ALIGN_RIGHT);
				else if(value.equals("center"))StyleConstants.setAlignment(sas,StyleConstants.ALIGN_CENTER);
				else if(value.equals("justify"))StyleConstants.setAlignment(sas,StyleConstants.ALIGN_JUSTIFIED);
			}
		}
	}
	

	public void endElement(String name)
	{
		addToWordList(getUntrimmedText());
		body.append(getText() + "</" + name + ">");
		
		if(name.equals("a") || name.equals("body") || name.equals("cite") 
				|| name.equals("code") || name.equals("div") || name.equals("em") || name.equals("h1") 
				|| name.equals("h2")|| name.equals("h3") || name.equals("img") || name.equals("li") 
				|| name.equals("ol")|| name.equals("p")|| name.equals("pre")|| name.equals("ul")
				||  name.equals("strong") || name.equals("span"))
		{
			currentAttributes =(SimpleAttributeSet)currentAttributes.getResolveParent();
		}
		else if(name.equals("br"))
		{
			wordList.add(new Word("\n"));
		}
		else if(name.equals("q"))
		{//quote cite=url attribute not implemented???
			wordList.add(new Word("'",currentAttributes));
			currentAttributes =(SimpleAttributeSet)currentAttributes.getResolveParent();
		}
		else if(name.equals("blockquote"))
		{
			wordList.add(new Word("\n",null));
			currentAttributes =(SimpleAttributeSet)currentAttributes.getResolveParent();
		}
		clearCurrentChars();
	}
	
	private void addToWordList(StringBuffer text)
	{
		if(text.length()<1)return;
		StringBuffer temp = new StringBuffer();
		for(int i = 0;i<text.length();i++)
		{//split text up in words
			char c = text.charAt(i);
			switch (c)
			{
				case ' ':	addWordFromTemp(temp); wordList.add(new Word(" "));  temp = new StringBuffer(); break;
				case '\n':	addWordFromTemp(temp); wordList.add(new Word("\n")); temp = new StringBuffer(); break;
				case '\t':	addWordFromTemp(temp); wordList.add(new Word("\t")); temp = new StringBuffer();break;
				default: temp.append(c);
			}
		}
		addWordFromTemp(temp);
	}
	
	private void addWordFromTemp(StringBuffer temp)
	{
		if(temp.length()>0)wordList.add(new Word(temp,currentAttributes));
	}

	public Extension build()
	{
		Extension e = new XHTML(body.toString(),new ArrayList(wordList));
		reset();
		return e;
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
