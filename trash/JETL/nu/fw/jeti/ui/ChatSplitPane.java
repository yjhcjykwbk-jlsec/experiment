/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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
 *
 *	Created on 28-apr-2003
 */

package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;

import nu.fw.jeti.events.ChatEndedListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.plugins.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;


/**
 * class defining basic feature of a chat window
 * used by groupchatwindow and chatwindow
 * @author E.S. de Boer
 *
 */

public class ChatSplitPane extends JSplitPane 
{
	private JID from;
	private String me;
	private Backend backend;
	private JScrollPane scrlInvoer = new JScrollPane();
	private JTextPane txtInvoer = new JTextPane();
	private JScrollPane scrlUitvoer = new JScrollPane();
	private JTextPane txtUitvoer = new ToolTipTextpane();
	private String fromName;
	private String thread;
	private boolean enterSends = false;
	private boolean showTimestamp = false;
	private Emoticons emoticons;
	private JPanel pnlBottom = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel pnlContol = new JPanel();
	private JPopupMenu popupMenu = new JPopupMenu();
	private String composingID;
	private boolean typing = false;
	private FormattedMessage xhtml;
	private JFrame parentFrame;
	private boolean groupChat;
	private Spell spell;
	private OpenPGP openPGP;
	private Translator translator;
	private Translator links;
	private Notifiers titleTimer;
	private Notifiers titleFlash;
	private SimpleAttributeSet colorAttributeSet = new SimpleAttributeSet();
	private boolean toFrontOnNewMessage=false;
	private static DateFormat dateFormat = DateFormat.getTimeInstance();
	DateFormat shortDate= DateFormat.getDateInstance(DateFormat.SHORT);
	DateFormat shortTime= DateFormat.getTimeInstance(DateFormat.SHORT);
	private boolean scrolls=true;
	private Date date = new Date();
    //private boolean sendEnabled=true;

	class ToolTipTextpane extends JTextPane
	{//Tooltip on textpane, show time of message
		public String getToolTipText(MouseEvent e)
		{
			int location = txtUitvoer.viewToModel(e.getPoint());
			StyledDocument doc = (StyledDocument) txtUitvoer.getDocument();
			AttributeSet set = doc.getCharacterElement(location).getAttributes();
			return (String) set.getAttribute("time");	
		}
	}

	public ChatSplitPane(Backend backend, JID to,String toName,String me,String thread,boolean groupChat,JFrame parentFrame,JMenu menu)
	{//remove parent frame?
		//online = false;
		this.groupChat = groupChat; 
		this.parentFrame = parentFrame; 
		from = to;
        fromName = toName; 
		this.backend = backend;
		this.me = me;
		this.thread = thread;
		String type = "unknown";
		if(groupChat) type = "groupchat";
		init(type,menu);
	}
	
	public ChatSplitPane(Backend backend, JID to,String toName,String me,String thread,boolean groupChat,JFrame parentFrame,String type,JMenu menu)
	{
		this.groupChat = groupChat; 
		this.parentFrame = parentFrame; 
		from = to;
        fromName = toName; 
		this.backend = backend;
		this.me = me;
		this.thread = thread;
		//type = "images";
		init(type,menu);
		
	}
	
	public void init(String type,JMenu menu)
	{
		JCheckBoxMenuItem chkItem = new JCheckBoxMenuItem(
				I18N.gettext("main.chat.To_front_on_new_message"));
		chkItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				toFrontOnNewMessage= ((JCheckBoxMenuItem)e.getSource()).isSelected();
			}
		});
		menu.add(chkItem);
		if (PluginsInfo.isPluginLoaded("emoticons"))
		{
			initEmoticons(type,menu);
		}
		
		if (!groupChat && PluginsInfo.isPluginLoaded("openpgp"))
		{
			openPGP =  (OpenPGP)PluginsInfo.getPluginInstance("openpgp");
			if(openPGP.canEncrypt(from))
			{
				Document doc = txtUitvoer.getDocument();
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setForeground(sas, Color.blue);
				try
				{
					doc.insertString(doc.getLength(),"Encrypted Converstation\n", sas);
				} catch (BadLocationException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		if (PluginsInfo.isPluginLoaded("spell"))
		{
			spell = (Spell) PluginsInfo.newPluginInstance("spell");
			spell.addChangeDictoryMenuEntry(menu);
		}
		if (PluginsInfo.isPluginLoaded("links"))
		{
			links = (Translator) PluginsInfo.newPluginInstance("links");
			links.init(txtUitvoer);
		}
		if (PluginsInfo.isPluginLoaded("titlescroller"))
		{
			titleTimer = (Notifiers) PluginsInfo.newPluginInstance("titlescroller");
			titleTimer.init(parentFrame, fromName);
		}
		if (PluginsInfo.isPluginLoaded("titleflash"))
		{
			titleFlash = (Notifiers) PluginsInfo.newPluginInstance("titleflash");
			titleFlash.init(parentFrame, fromName);
		}
		if(PluginsInfo.isPluginLoaded("windowsutils"))
		{
			final NativeUtils util =(NativeUtils)PluginsInfo.newPluginInstance("windowsutils");
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
						I18N.gettext("windowsutils.Set_Always_On_Top"));
			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(((JCheckBoxMenuItem)e.getSource()).isSelected())
					{	
						util.windowAlwaysOnTop(ChatSplitPane.this.getTopLevelAncestor(), true);
					}
					else
					{	
						util.windowAlwaysOnTop(ChatSplitPane.this.getTopLevelAncestor(), false);
					}
				}
			});
			menu.add(item);
		}
	
		translator = PluginsInfo.getTranslator();
		if(emoticons!=null || spell != null)
		{	
			txtInvoer.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					if (SwingUtilities.isRightMouseButton(e))
					{
						if(spell != null)
						{
							if(!spell.rightClick(txtInvoer,e) && emoticons != null) popupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
						else popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
		
	
//		InputMap inputMap = txtUitvoer.getInputMap();
//
//		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
//		inputMap.put(key,new FindAction(txtUitvoer));
		
		
		ToolTipManager.sharedInstance().registerComponent(txtUitvoer);
			
		
		enterSends = Preferences.getBoolean("jeti","enterSends",true);
		showTimestamp = Preferences.getBoolean("jeti","showTimestamp",true);
		parentFrame.setTitle(fromName);
		
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (PluginsInfo.isPluginLoaded("xhtml"))
		{
			xhtml = (FormattedMessage) PluginsInfo.newPluginInstance("xhtml");
			xhtml.initXHTML(txtUitvoer, txtInvoer, pnlContol);
		}
		
		String t = "";
		//if(!groupChat)	
		//notify();
		if (thread == null)t = "no id - ";
		Document doc = txtUitvoer.getDocument();
		try
		{
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, Color.gray);
			doc.insertString(doc.getLength(), t + I18N.gettext("main.chat.Chat_started_on:") + " " + DateFormat.getDateTimeInstance().format(new Date()), sas);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		//pack();//solves bug iconofied frame then no system message and wrong layout
		setBorder(null);
		setSize(300, 350);
	}
	
	public JTextPane getTextInput()
	{
		return txtInvoer;
	}
	
	private void initEmoticons(String type,JMenu menu)
	{
		//showEmoticons = true;
		emoticons = (Emoticons) PluginsInfo.newPluginInstance("emoticons");
		try
		{
			emoticons.init(txtUitvoer, pnlContol, txtInvoer, popupMenu,type,menu);
		}
		catch (IllegalStateException ex)
		{
			ex.printStackTrace(); 
			//showEmoticons = false;
			PluginsInfo.unloadPlugin("emoticons");
			emoticons = null;
			return;
		}
	}
	
	public void close()
	{
		for(Iterator j = backend.getListeners(ChatEndedListener.class);j.hasNext();)
		{//exit
			((ChatEndedListener)j.next()).chatEnded(from);
		}
	}
		
		
	

	
//	public boolean compareJID(JID jid)
//	{
//		return from.equals(jid);
//	}
//	
//	public String getThread()
//	{
//		return thread;
//	}



	public void appendMessage(Message message,String name)
	{
		if (name == null) {
		 	appendSystemMessage(message.getBody());
		} else if(name.equals(me)) {
            showMessage(message,new Color(156, 23, 23),me);
		} else {
			if(titleTimer!=null ) {
                titleTimer.start(
                    MessageFormat.format(
                        I18N.gettext("main.chat.{0}_says:_{1}"),
                        new Object[] { new String(name),
                                       new String(message.getBody()) }));
            }
			if(titleFlash!=null) {
                titleFlash.start("");
            }
			showMessage(message, new Color(17, 102, 6), name);
		}
	}
	
	public synchronized void appendSystemMessage(String message)
	{
		Document doc = txtUitvoer.getDocument();
		try
		{
			final Point  viewPos = scrlUitvoer.getViewport().getViewPosition();
			boolean scroll = scrolls;//cache scroll value because insertstring will update it
			SimpleAttributeSet sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, Color.gray);
            String timeStamp;
            if (showTimestamp) {
                timeStamp = formatTime(new Date()) + " ";
            } else {
                timeStamp = "";
            }
			doc.insertString(doc.getLength(), "\n"+ timeStamp + message, sas);
			if(scroll)txtUitvoer.setCaretPosition(doc.getLength());
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						scrlUitvoer.getViewport().setViewPosition(viewPos);
					}
				});
			}
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	private synchronized void showMessage(Message message, Color color, String name)
	{//two threads can write to display (typing & receiving)
		String text = message.getBody();
        Date delay =null;
		List wordList=null;
		date.setTime(System.currentTimeMillis());
		
        // Loop through extensions
		if (message.hasExtensions())
		{
			for (Iterator i = message.getExtensions(); i.hasNext();)
			{
				Extension extension = (Extension) i.next();
				if (extension instanceof XDelay) {
					delay = ((XDelay) extension).getDate();
				} else if(xhtml!=null) {
					if(wordList==null) wordList = xhtml.getWordList(extension);
				} else if(openPGP!=null) {
					if(wordList==null) {
						String temp =  openPGP.decrypt(extension);
						if(temp!=null) text = temp;
					}
				}
			}
		}
		if (xhtml!=null && !name.equals(me)) {
            xhtml.useXHTML(wordList!=null, name);
        }

		final Point  viewPos = scrlUitvoer.getViewport().getViewPosition();
		boolean scroll = scrolls;//cache scroll value because insertstring will update it
		Document doc = txtUitvoer.getDocument();
		try
		{
			doc.insertString(doc.getLength(),"\n",null);
            if (delay != null) 
            {
            	SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setForeground(sas, Color.darkGray);
                doc.insertString(doc.getLength(), formatTime(delay) + " ", sas);
            }
			else if (showTimestamp) 
			{// Eventual timestamp
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setForeground(sas, Color.gray);
                doc.insertString(doc.getLength(), formatTime(date) + " ", sas);
            }
			
            StyleConstants.setForeground(colorAttributeSet, color);

            // Print originator if not error
            if (!message.getType().equals("error")) {
				colorAttributeSet.addAttribute("time",dateFormat.format(date));
				doc.insertString(doc.getLength(),name+ ": ",colorAttributeSet);
			}
			
			if (wordList==null) {
                wordList = createWordList(text);
            }

            // Mark eventual weblinks (if links extension is active)
			if(links!=null) {
                links.translate(wordList);
            }
			
			if(groupChat) {//me replace with nick
				for(Iterator i = wordList.iterator();i.hasNext();) {
					Word w = (Word) i.next();
					if(w.word.equals("/me")) {
                        w.word = name;
                    }
				}
			}

			// Handle error messages
			if (message.getType().equals("error")) {
                insertError(wordList, message);
			}

			// Insert eventual emoticons
			if (emoticons != null) {
                emoticons.insertEmoticons(wordList);
            }

			//TODO make a new translator thing for here and below
			if (translator != null) translator.translate(wordList);

            // Insert words from wordlist
			for(Iterator i = wordList.iterator();i.hasNext();) {
				Word w = (Word) i.next();
				doc.insertString(doc.getLength(),w.toString(),
                                 w.getAttributes());
			}

			if (scroll)	{
                txtUitvoer.setCaretPosition(doc.getLength());
            } else {
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						scrlUitvoer.getViewport().setViewPosition(viewPos);
					}
				});
			}
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		if(toFrontOnNewMessage)
		{
			if(!getTopLevelAncestor().isFocusOwner())
			{
				if(getTopLevelAncestor() instanceof JFrame)
				{
					JFrame frame = (JFrame)getTopLevelAncestor();
					if(frame.getExtendedState()==JFrame.ICONIFIED)
					{
						frame.setState(JFrame.NORMAL);
					}
				}
				txtInvoer.requestFocus();
			}
		}
	}

    /**
     * Format a timestamp for display.
     */
    private String formatTime(Date date) {
		String result = "";
		
		if((System.currentTimeMillis() -date.getTime())> 600000)
		{//message is older then 10 min
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.AM_PM, Calendar.AM);
		    if(date.before(cal.getTime()))
		    {//print date if it is the previous day
		    	result += shortDate.format(date)+ " ";
		    }
		}
		result+="["+shortTime.format(date)+"]";
		return result;
    }

    // Insert error message into wordList
    private void insertError(List wordList, Message message) {
        String error;
        boolean replace = false;

        switch (message.getErrorCode()) {
        case 404:
            error = MessageFormat.format(
                I18N.gettext("main.error.User_{0}_could_not_be_found"),
                new Object[] {message.getFrom()});
            replace = true;
            break;
        default:
            error = I18N.gettext("main.error.Error_in_chat:");
            break;
        }

        // Create list of error words and make them red
        List errorWords = createWordList(error);
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setForeground(sas, Color.red);
        for(Iterator i = errorWords.iterator();i.hasNext();) {
            ((Word)i.next()).addAttributes(sas);
        }
        
        if (!replace) {
            errorWords.addAll(wordList);
        }
        wordList.clear();
        wordList.addAll(errorWords);
	}

	private List createWordList(String text)
	{
		List wordList = new ArrayList();
		StringBuffer temp = new StringBuffer();
		for(int i = 0;i<text.length();i++)
		{//split text up in words
			char c = text.charAt(i);
			//System.out.println("'" + c + "'");
			switch (c)
			{
				case ' ': addWordFromTemp(temp, wordList);wordList.add(new Word(" ")); temp = new StringBuffer(); break;
				case '\n': addWordFromTemp(temp, wordList);wordList.add(new Word("\n")); temp = new StringBuffer(); break;
				case '\t': addWordFromTemp(temp, wordList); wordList.add(new Word("\t"));temp = new StringBuffer();break;
				default: temp.append(c);
			}
		}
		addWordFromTemp(temp, wordList);
		return wordList;
	}
	
	private void addWordFromTemp(StringBuffer temp, List wordList)
	{
		if(temp.length()>0)wordList.add(new Word(temp));
	}

	private void jbInit() throws Exception
	{
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setBottomComponent(pnlBottom);
		//scrlInvoer.setAutoscrolls(true);
		scrlInvoer.setPreferredSize(new Dimension(100, 40));
		//scrlUitvoer.setAutoscrolls(false);
		txtInvoer.addKeyListener(new java.awt.event.KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				txtInvoer_keyPressed(e);
			}
			
			public void keyReleased(KeyEvent e)
			{
				if(spell!=null)spell.keyReleased(e,txtInvoer);
			}
		});
		txtUitvoer.setEditable(false);
		txtUitvoer.getDocument().putProperty( DefaultEditorKit.EndOfLineStringProperty, "\n" );
		txtInvoer.getDocument().putProperty( DefaultEditorKit.EndOfLineStringProperty, "\n" );
		parentFrame.addWindowFocusListener(new java.awt.event.WindowAdapter()
		{
			public void windowGainedFocus(WindowEvent e)
			{
				txtInvoer.requestFocusInWindow();
			}
		});
		pnlBottom.setLayout(borderLayout1);
		pnlBottom.add(scrlInvoer, BorderLayout.CENTER);
		pnlBottom.add(pnlContol, BorderLayout.NORTH);
		add(pnlBottom, JSplitPane.BOTTOM);
		add(scrlUitvoer, JSplitPane.TOP);
		scrlUitvoer.getViewport().add(txtUitvoer, null);
		scrlInvoer.getViewport().add(txtInvoer, null);
		scrlUitvoer.getVerticalScrollBar().getModel().addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				BoundedRangeModel model =(BoundedRangeModel)e.getSource();
				if(!model.getValueIsAdjusting())
				{
					//System.out.println(model);
					//System.out.println(((double)model.getValue())/model.getMaximum());
					//System.out.println(model.getMaximum()-model.getExtent()-model.getValue());
					if(model.getMaximum()-model.getExtent()-model.getValue()>10)
					{
						scrolls=false;
					}
					else scrolls=true;
				}
				
				
			}
		});
		setDividerLocation(190);
	}

	public void send()
	{
		boolean sendXHTML = false;
		XExtension html = null;
		XExtension ownHtml = null;
		List wordList=null;
		if(xhtml != null)wordList = xhtml.makeWordListFromDocument();
		if(wordList==null)wordList=createWordList(txtInvoer.getText());
		StringBuffer temp = new StringBuffer();
		//TODO Translator
		//if (translator != null) translator.translate(wordList);
		
		for(Iterator i = wordList.iterator();i.hasNext();)
		{
			temp.append(i.next());
		}
		String text = temp.toString();
		if(xhtml != null)
		{	
			html = xhtml.getXHTMLExtension(wordList);
			//ownwordlist will be edited so make deep copy
			ArrayList tempList = new ArrayList();
			try 
			{
				for(Iterator i = wordList.iterator();i.hasNext();)
				{
					tempList.add(((Word)i.next()).clone());
				}
			}
			catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			ownHtml = xhtml.getXHTMLExtension(tempList);
			sendXHTML = xhtml.sendXML();
		}
		Message message = null;
		if(groupChat)
		{	
			if(html!=null) message = new Message(from,text ,html);
			else message = new Message(from,text);
		}
		else
		{
			MessageBuilder b = new MessageBuilder();
			b.type = "chat";
			b.setTo(from);
			b.setId(backend.getIdentifier());
			b.thread = thread;
			b.addXExtension(new XMessageEvent("composing", null));
		
			if(openPGP!=null && openPGP.canEncrypt(from))
			{
				b.body = "This message is encrypted with openPGP";
				b.addXExtension(openPGP.encrypt(text));
			}
			else
			{
				b.body = text;
				if(sendXHTML)b.addXExtension(html);
			}
			message =((Message) b.build());
			showMessage(new Message(text, from, null,thread,ownHtml),new Color(156, 23, 23),me);
		}
		backend.sendMessage(message);
		
		txtInvoer.getHighlighter().removeAllHighlights();
		txtInvoer.setText("");
		txtInvoer.requestFocus();
		typing = false;
	}
	
	public void composingID(String id)
	{
		composingID = id;
	}

	void txtInvoer_keyPressed(KeyEvent e)
	{
		
		if(!groupChat)
		{
			if (txtInvoer.getText().length() < 2 && typing)
			{
				typing = false;
				if (composingID != null)
				{
					//send not composing
					backend.send(new Message(null, from, null, new XMessageEvent(null, composingID)));
				}
			}
			else if (txtInvoer.getText().length() > 0 && !typing)
			{
				typing = true;
				if (composingID != null)
				{
					//send composing
					 backend.send(new Message(null, from, null, new XMessageEvent("composing", composingID)));
				}
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (enterSends)
			{
				if ((e.getModifiers() == KeyEvent.SHIFT_MASK) || (e.getModifiers() == KeyEvent.CTRL_MASK))
				{
					//txtInvoer.setText(txtInvoer.getText() +"\n");
					Document doc = txtInvoer.getDocument();
					try
					{
						doc.insertString(txtInvoer.getCaretPosition(), "\n", null);
					}
					catch (BadLocationException e3)
					{e3.printStackTrace();}
				}
				else
				{
					send();
					//jButton1.doClick();
					e.consume();
				}
			}
			else
			{
				if ((e.getModifiers() == KeyEvent.SHIFT_MASK) || (e.getModifiers() == KeyEvent.CTRL_MASK))
				{
					send();
					//jButton1.doClick();
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
