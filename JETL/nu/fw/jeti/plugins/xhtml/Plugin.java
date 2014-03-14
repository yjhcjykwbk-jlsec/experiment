/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2002 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 */


package nu.fw.jeti.plugins.xhtml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.text.*;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XExtension;
import nu.fw.jeti.plugins.FormattedMessage;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.Word;
import nu.fw.jeti.plugins.xhtml.fontchooser.FontDialog;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


/**
 * @author E.S. de Boer
 *
 * 
 */
public class Plugin implements Plugins, FormattedMessage
{
	private JButton btnFont;
	private JButton btnColor;
	private JButton btnBold;
	private JButton btnItalic;
	private JButton btnUnderline;
	private JTextPane txtInvoer;
	private JTextPane txtUitvoer;
	private boolean showXHTML = true;
	public final static String VERSION = "0.3";
	public final static String DESCRIPTION = "xhtml.Formats_messages";
	public final static String MIN_JETI_VERSION = "0.5";
	public final static String NAME = "xhtml";
	public final static String ABOUT = "by E.S. de boer, uses Fontchooser from SimplyHTML by Ulrich Hilger";
	

	public static void init(Backend backend)
	{
		backend.addExtensionHandler("http://www.w3.org/1999/xhtml",new XHTMLHandler());
	}

	public void initXHTML(JTextPane txtUitvoer, final JTextPane txtInvoer, JPanel pnlControl)
	{
		this.txtInvoer = txtInvoer;
		this.txtUitvoer = txtUitvoer;
		MutableAttributeSet set = new SimpleAttributeSet();
		int i = Preferences.getInteger("xhtml", "foreground", 0);
		if (i != 0)
		{
			StyleConstants.setForeground(set, new Color(i));
		}
		i = Preferences.getInteger("xhtml", "background", 0);
		if (i != 0)
		{
			StyleConstants.setBackground(set, new Color(i));
		}
		i = Preferences.getInteger("xhtml", "font-size", 0);
		if (i != 0)
		{
			StyleConstants.setFontSize(set, i);
		}
		StyleConstants.setFontFamily(set, Preferences.getString("xhtml", "font-family", "Arial"));
		txtInvoer.setParagraphAttributes(set, false);

		//font
		//btnFont = new JButton(StatusIcons.getImage("plugins/xhtml.jar!/nu/fw/jeti/plugins/xhtml/font.gif"));
		btnFont = new JButton(new ImageIcon(getClass().getResource("font.gif")));
		btnFont.setToolTipText(I18N.gettext("xhtml.Font"));
		btnFont.setMargin(new Insets(0, 0, 0, 0));
		btnFont.setPreferredSize(new Dimension(23, 23));
		btnFont.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				AttributeSet attr = txtInvoer.getCharacterAttributes(); //new SimpleAttributeSet();
				FontDialog d = new FontDialog((Frame)txtInvoer.getTopLevelAncestor(), I18N.gettext("xhtml.Choose_Font"), attr);
				d.setModal(true);
				d.show();

				if (d.getResult() == FontDialog.RESULT_OK)
				{
					attr = d.getAttributes();
					int start = txtInvoer.getSelectionStart();
					int end = txtInvoer.getSelectionEnd();
					if (end != start)
					{
						StyledDocument doc = (StyledDocument) txtInvoer.getDocument();
						doc.setCharacterAttributes(start, end - start, attr, false);
					}
					else txtInvoer.setParagraphAttributes(attr, false);
					txtInvoer.requestFocus();
				}
			}
		});
		pnlControl.add(btnFont);

		//color
		//btnColor = new JButton(StatusIcons.getImage("plugins/xhtml.jar!/nu/fw/jeti/plugins/xhtml/color.gif"));
		btnColor = new JButton(new ImageIcon(getClass().getResource("color.gif")));
		btnColor.setToolTipText(I18N.gettext("xhtml.Color"));
		btnColor.setMargin(new Insets(0, 0, 0, 0));
		btnColor.setPreferredSize(new Dimension(23, 23));
		btnColor.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Color color = JColorChooser.showDialog(txtInvoer.getTopLevelAncestor(), I18N.gettext("xhtml.Color"), null);
				if (color != null)
				{
					int start = txtInvoer.getSelectionStart();
					int end = txtInvoer.getSelectionEnd();
					MutableAttributeSet set = new SimpleAttributeSet();
					set.addAttribute(StyleConstants.Foreground, color);
					if (end != start)
					{
						StyledDocument doc = (StyledDocument) txtInvoer.getDocument();
						//System.out.println(set);
						doc.setCharacterAttributes(start, end - start, set, false);
					}
					else txtInvoer.setParagraphAttributes(set, false);
					txtInvoer.requestFocus();
				}
			}
		});
		pnlControl.add(btnColor);

		//URL urlJar = Start.path + "plugins" + File.separator + "xhtml.jar!/";

		ActionListener returnFocus = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                txtInvoer.requestFocus();
            }
        };

		//bold
		Action boldAction = new StyledEditorKit.BoldAction();
		boldAction.putValue(Action.NAME, null);
		boldAction.putValue(Action.SHORT_DESCRIPTION, I18N.gettext("xhtml.Bold"));
		//boldAction.putValue(Action.SMALL_ICON, StatusIcons.getImage("plugins/xhtml.jar!/nu/fw/jeti/plugins/xhtml/Bold16.gif"));
		boldAction.putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("Bold16.gif")));
		btnBold = new JButton(boldAction);
		btnBold.setMargin(new Insets(0, 0, 0, 0));
		btnBold.setPreferredSize(new Dimension(23, 23));
        btnBold.addActionListener(returnFocus);
		pnlControl.add(btnBold);

		//		italic
		Action italicAction = new StyledEditorKit.ItalicAction();
		italicAction.putValue(Action.NAME, null);
		italicAction.putValue(Action.SHORT_DESCRIPTION, I18N.gettext("xhtml.Italic"));
		//italicAction.putValue(Action.SMALL_ICON,StatusIcons.getImage("plugins/xhtml.jar!/nu/fw/jeti/plugins/xhtml/Italic16.gif"));
		italicAction.putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("Italic16.gif")));
		btnItalic = new JButton(italicAction);
		btnItalic.setMargin(new Insets(0, 0, 0, 0));
		btnItalic.setPreferredSize(new Dimension(23, 23));
        btnItalic.addActionListener(returnFocus);
		pnlControl.add(btnItalic);
		//			  
		//		underline
		Action underlineAction = new StyledEditorKit.UnderlineAction();
		underlineAction.putValue(Action.NAME, null);
		underlineAction.putValue(Action.SHORT_DESCRIPTION, I18N.gettext("xhtml.Underline"));
		//underlineAction.putValue(Action.SMALL_ICON,StatusIcons.getImage("plugins/xhtml.jar!/nu/fw/jeti/plugins/xhtml/Underline16.gif"));
		underlineAction.putValue(Action.SMALL_ICON,new ImageIcon(getClass().getResource("Underline16.gif")));
		btnUnderline = new JButton(underlineAction);
		btnUnderline.setMargin(new Insets(0, 0, 0, 0));
		btnUnderline.setPreferredSize(new Dimension(23, 23));
        btnUnderline.addActionListener(returnFocus);
		pnlControl.add(btnUnderline);

	}

	//	/**
	//	 * Method changeXHTML change buttons to enabled/disabled.
	//	 */
	//	private changeXHTML()
	//	{
	//		btnBold.setEnabled(showXHTML);
	//		btnColor.setEnabled(showXHTML);
	//		btnFont.setEnabled(showXHTML);
	//		btnItalic.setEnabled(showXHTML);
	//		btnUnderline.setEnabled(showXHTML);
	//
	//	}

	public List getWordList(Extension extension)
	{
		if(extension instanceof XHTML)
		{	
			return ((XHTML) extension).getWordList();
		}
		else return null;
	}
		
	public void useXHTML(boolean useXHTMl,String name)
	{//look if other one sends xhtml
		if (useXHTMl != showXHTML && !useXHTMl)
		{
			Document doc = txtUitvoer.getDocument();
			try
			{
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setForeground(sas, Color.gray);
				doc.insertString(doc.getLength(),MessageFormat.format(I18N.gettext("xhtml.{0}_does_not_support_formatted_messages"),new Object[]{name}) + " \n", sas);
				txtUitvoer.setCaretPosition(doc.getLength());
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		showXHTML=useXHTMl;
	}
	
	public XExtension getXHTMLExtension(List wordList)
	{
		return new XHTML(wordList);
	}
	
	public boolean sendXML()
	{
		return showXHTML;
	}
	
	public List makeWordListFromDocument()
	{//TODO parse wordlist instead of txtuitvoer
		Element[] element = txtInvoer.getDocument().getRootElements();
		List wordList = new ArrayList();
		//boolean span = parseSpan(xhtml,txtInvoer.getParagraphAttributes());
		//AttributeSet set = txtInvoer.getParagraphAttributes().copyAttributes();
		parseElement(element[0], wordList, new SimpleAttributeSet());
		return wordList;
		
//		if (span)
//		{
//			xhtml.append("</span>");
//			//show.append("</span>");
//		}
//
//		//System.out.println(xhtml);
//		//System.out.println(show);
//
//		
//		if(!groupChat)
//		{
//			chat.appendMessage(new Message(null, null, null, null, new XHTML(xhtml.toString(),wordList)),me);
//		}
//		if (showXHTML)
//		{
//			XHTML html = new XHTML(xhtml.toString());
//			MessageBuilder b = new MessageBuilder();
//			b.type = "chat";
//			b.setTo(from);
//			b.setId(backend.getIdentifier());
//			b.body = txtInvoer.getText();
//			b.thread = thread;
//			if(!groupChat) b.addExtension(new XMessageEvent("composing", null));
//			b.addExtension(html);
//			backend.sendMessage((Message) b.build());
//		}
//		else
//		{
//			if(!groupChat) backend.sendMessage(new Message(txtInvoer.getText(), from, backend.getIdentifier(), thread, new XMessageEvent("composing", null)));
//			else backend.sendMessage(new Message(from,txtInvoer.getText()));
//			//showMessage(message, "#009400", me);
//		}
	}
	
	private void addToWordList(String text,SimpleAttributeSet currentAttributes, List wordList)
	{
		if(text.equals(""))return;
		StringBuffer temp = new StringBuffer();
		for(int i = 0;i<text.length();i++)
		{//split text up in words
			char c = text.charAt(i);
			switch (c)
			{
				case ' ':	addWordFromTemp(temp, wordList, currentAttributes); wordList.add(new Word(" ", (SimpleAttributeSet)currentAttributes.clone())); temp = new StringBuffer(); break;
				case '\n':	addWordFromTemp(temp, wordList, currentAttributes); temp = new StringBuffer(); break;
				case '\t':	addWordFromTemp(temp, wordList, currentAttributes); wordList.add(new Word("\t", (SimpleAttributeSet)currentAttributes.clone()));temp = new StringBuffer();break;
				default:	temp.append(c);
			}
		}
		addWordFromTemp(temp, wordList, currentAttributes);
	}
	
	private void addWordFromTemp(StringBuffer temp, List wordList, SimpleAttributeSet currentAttributes)
	{
		if(temp.length()>0)wordList.add(new Word(temp,(SimpleAttributeSet)currentAttributes.clone()));
	}
	
	
	/**
	 * Method parseElement.
	 * parses a styled document to a wordList 
	 * @param elem element to parse
	 * 
	 */
	private void parseElement(Element elem, List wordList,SimpleAttributeSet attr)
	{
		AttributeSet set = elem.getAttributes();
		attr.addAttributes(set);
		if (elem.getName().equals("paragraph"))
		{
			if (elem.getStartOffset() != 0)
			{//only <br/>?
				wordList.add(new Word("\n",(SimpleAttributeSet)attr.clone()));
			}
		}
		if (elem.getName().equals("content"))
		{
			if (elem.getElementCount() > 0)
			{
				for (int i = 0; i < elem.getElementCount(); i++)
				{
					parseElement(elem.getElement(i),wordList, new SimpleAttributeSet(attr));
				}
			}
			else
			{
				try
				{
					int offset = elem.getEndOffset() - elem.getStartOffset();
					String text = elem.getDocument().getText(elem.getStartOffset(), offset);
					//remove linebreaks
					addToWordList(text, attr, wordList);
				}
				catch (BadLocationException ble)
				{
					ble.printStackTrace();
				}
			}
		}
		else if (elem.getElementCount() > 0)
		{
			for (int i = 0; i < elem.getElementCount(); i++)
			{
				parseElement(elem.getElement(i), wordList,new SimpleAttributeSet(attr));
			}
		}
	}
	
		
//	
//	public void sendMessage()
//	{//TODO parse wordlist instead of txtuitvoer
//		Element[] element = txtInvoer.getDocument().getRootElements();
//		StringBuffer xhtml = new StringBuffer();
//		List wordList = new ArrayList();
//		//String hex = SHTMLDocument.colorToHexString(txtInvoer.getForeground());
//		//xhtml.append("<span style=\"color:#" + hex + "\">");
//		boolean span = parseSpan(xhtml,txtInvoer.getParagraphAttributes());
//		//AttributeSet set = txtInvoer.getParagraphAttributes().copyAttributes();
//		parseElement(element[0], xhtml, wordList, new SimpleAttributeSet());
//		if (span)
//		{
//			xhtml.append("</span>");
//			//show.append("</span>");
//		}
//
//		//System.out.println(xhtml);
//		//System.out.println(show);
//
//		
//		if(!groupChat)
//		{
//			chat.appendMessage(new Message(null, null, null, null, new XHTML(xhtml.toString(),wordList)),me);
//		}
//		if (showXHTML)
//		{
//			XHTML html = new XHTML(xhtml.toString());
//			MessageBuilder b = new MessageBuilder();
//			b.type = "chat";
//			b.setTo(from);
//			b.setId(backend.getIdentifier());
//			b.body = txtInvoer.getText();
//			b.thread = thread;
//			if(!groupChat) b.addExtension(new XMessageEvent("composing", null));
//			b.addExtension(html);
//			backend.sendMessage((Message) b.build());
//		}
//		else
//		{
//			if(!groupChat) backend.sendMessage(new Message(txtInvoer.getText(), from, backend.getIdentifier(), thread, new XMessageEvent("composing", null)));
//			else backend.sendMessage(new Message(from,txtInvoer.getText()));
//			//showMessage(message, "#009400", me);
//		}
//	}
	
	
	
	
	
	
	
	
		
//	/**
//	 * Method parseElement.
//	 * parses a styled document to xhtml 
//	 * @param elem element to parse
//	 * @param show list of words
//	 * @param xhtml xhtml output
//	 */
//	private void parseElement(Element elem, StringBuffer xhtml, List wordList,SimpleAttributeSet attr)
//	{
//		AttributeSet set = elem.getAttributes();
//		attr.addAttributes(set);
//		if (elem.getName().equals("paragraph"))
//		{
//			//System.out.println("par");
//			if (elem.getStartOffset() != 0)
//			{//only <br/>?
//				xhtml.append("<br></br>");
//				//show.append("<br></br>");
//				wordList.add(new Word("\n"));
//			}
//		}
//		//System.out.println(elem);
//		if (elem.getName().equals("content"))
//		{
//			boolean span = parseSpan(xhtml, set);
//			if (StyleConstants.isBold(set))
//			{
//				xhtml.append("<strong>");
//				//show.append("<strong>");
//			}
//			if (StyleConstants.isItalic(set))
//			{
//				xhtml.append("<em>");
//				//show.append("<em>");
//
//			}
//			if (StyleConstants.isUnderline(set))
//			{//remove <u> and replace with textdecoration
//				xhtml.append("<u>");
//				//show.append("<u>");
//			}
//			if (elem.getElementCount() > 0)
//			{
//				for (int i = 0; i < elem.getElementCount(); i++)
//				{
//					parseElement(elem.getElement(i), xhtml, wordList, new SimpleAttributeSet(attr));
//				}
//			}
//			else
//			{
//				//System.out.println("no childern");
//				try
//				{
//					int offset = elem.getEndOffset() - elem.getStartOffset();
//					String text = elem.getDocument().getText(elem.getStartOffset(), offset);
//					StringBuffer escaped = new StringBuffer();
//					XMLData.escapeString(escaped, text);
//					//remove linebreaks
//					if (escaped.charAt(escaped.length() - 1) == '\n')
//					{
//						//System.out.println("enter");
//						xhtml.append(escaped.deleteCharAt((escaped.length() - 1)));
//					}
//					else
//						xhtml.append(escaped);
//					addToWordList(text, attr, wordList);
//					//append unescaped
////					if (text.charAt(text.length() - 1) == '\n')
////					{
////						//System.out.println("enter");
////						show.append(text.substring(0, text.length() - 1));
////					}
////					else
////						show.append(text);
//					//System.out.println(elem.getDocument().getText(elem.getStartOffset(), offset));
//				}
//				catch (BadLocationException ble)
//				{
//					ble.printStackTrace();
//				}
//			}
//			if (StyleConstants.isUnderline(set))
//			{
//				xhtml.append("</u>");
//				//show.append("</u>");
//			}
//			if (StyleConstants.isItalic(set))
//			{
//				xhtml.append("</em>");
//				//show.append("</em>");
//			}
//			if (StyleConstants.isBold(set))
//			{
//				xhtml.append("</strong>");
//				//show.append("</strong>");
//			}
//
//			if (span)
//			{
//				xhtml.append("</span>");
//				//show.append("</span>");
//			}
//		}
//		else if (elem.getElementCount() > 0)
//		{
//			for (int i = 0; i < elem.getElementCount(); i++)
//			{
//				parseElement(elem.getElement(i), xhtml, wordList,new SimpleAttributeSet(attr));
//			}
//		}
//	}
	
	

//	private boolean parseSpan(StringBuffer xhtml, AttributeSet set)
//	{
//		boolean span = false;
//		if (set.isDefined(StyleConstants.Foreground))
//		{
//			String hex = colorToHexString((Color) set.getAttribute(StyleConstants.Foreground));
//			span = checkSpan(xhtml, span);
//			xhtml.append("color: #" + hex + ";");
//		}
//		if (set.isDefined(StyleConstants.Background))
//		{//background not in xhtmk jep?
//			String hex = colorToHexString((Color) set.getAttribute(StyleConstants.Background));
//			span = checkSpan(xhtml, span);
//			xhtml.append(" background: #" + hex + ";");
//		}
//		if (set.isDefined(StyleConstants.FontFamily))
//		{
//			span = checkSpan(xhtml, span);
//			xhtml.append(" font-family: " + StyleConstants.getFontFamily(set) + ";");
//		}
//		if (set.isDefined(StyleConstants.FontSize))
//		{
//			span = checkSpan(xhtml, span);
//			xhtml.append(" font-size: " + StyleConstants.getFontSize(set) + "pt" + ";");
//		}
//		if (set.isDefined(StyleConstants.StrikeThrough))
//		{
//			span = checkSpan(xhtml, span);
//			xhtml.append(" text-decoration: line-through;");
//		}
//		if (span)
//		{
//			//remove last ;
//			xhtml.deleteCharAt(xhtml.length() - 1);
//			xhtml.append("\">");
//		}
//		return span;
//	}
//
//	//check if there is a span tag otherwise add it
//	private boolean checkSpan(StringBuffer xhtml,boolean span)
//	{
//		if (!span)
//		{
//			xhtml.append("<span style=\"");
//			span = true;
//		}
//		return span;
//	}
//	
//	private static final char[] HEX_DIG = {
//						'0', '1', '2', '3', '4', '5', '6', '7',
//						'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
//						};
//	
//	public String colorToHexString(Color c) 
//	{
//		int rgb = c.getRGB();
//		char[] cdata = new char[6];
//		for (int i = 0; i < 6; i++) {
//			cdata[5-i] = HEX_DIG[rgb & 0xF];
//			rgb = rgb >> 4;
//		}
//		return new String(cdata);
//	}
//	
////	  /**
////   * resolve sets of attributes that are recursively stored in each other
////   *
////   * @param style  the set of attributes containing other sets of attributes
////   */
////  public static AttributeSet resolveAttributes(AttributeSet style) {
////    SimpleAttributeSet set = new SimpleAttributeSet();
////    if(style != null) {
////      Enumeration names = style.getAttributeNames();
////      Object value;
////      Object key;
////      while(names.hasMoreElements()) {
////        key = names.nextElement();
////        //System.out.println("Util resolveAttributes key=" + key);
////        value = style.getAttribute(key);
////        //System.out.println("Util resolveAttributes value=" + value);
////        if( (!key.equals(StyleConstants.NameAttribute)) &&
////            (!key.equals(StyleConstants.ResolveAttribute)) &&
////            (!key.equals(AttributeSet.ResolveAttribute)) &&
////            (!key.equals(AttributeSet.NameAttribute)))
////        {
////          set.addAttribute(key, value);
////        }
////        else {
////          if(key.equals(StyleConstants.ResolveAttribute) ||
////             key.equals(AttributeSet.ResolveAttribute)) {
////            //System.out.println("Util resolveAttributes resolving key=" + key);
////            set.addAttributes(resolveAttributes((AttributeSet) value));
////          }
////        }
////      }
////    }
////    return set;
////  }


	public void unload(){}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
