/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2001 E.S. de Boer  
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

package nu.fw.jeti.plugins.emoticons;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.PopupMenu;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.XMLDataFile;
import nu.fw.jeti.images.Icons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Emoticons;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.plugins.Word;
import nu.fw.jeti.plugins.xhtml.fontchooser.FontDialog;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

import org.xml.sax.SAXException;



/**
 * @author E.S. de Boer
 */
public class Plugin implements Plugins,Emoticons
{//TODO change to list and check if a emoticon thing is in the string (:; etc
	private List emoticons;
	private SimpleAttributeSet sas = new SimpleAttributeSet();
	private static Map smilies;//contains a map with the starting char of a smilie (contains icon for every text)
	private static Map smilieList;//contains lists containing all icons with text (only 1 icon multiple texts are removed)
	private List currentIconSet;
	private Emoticon[] displayableIconList;
	public final static String VERSION = "1.8";
	public final static String DESCRIPTION = "emoticons.Shows_graphical_emoticons";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "emoticons";
	public final static String ABOUT = "by E.S. de Boer";
	
	private SAXParser parser;

	public Plugin()
	{emoticons = Preferences.getPlugable("emoticons");}

	private void loadParser()
	{
		try{parser = SAXParserFactory.newInstance().newSAXParser();}
		catch (FactoryConfigurationError ex){ex.printStackTrace();}
		catch (SAXException ex){ex.printStackTrace();}
		catch (ParserConfigurationException ex){ex.printStackTrace();}
	}

	public static void init(Backend backend) throws IOException
    {
		new Plugin(backend);
    }	
		
	public Plugin(Backend backend) throws IOException
	{
		loadParser();
		new Icons(parser,"emoticons");
		
		/*
		//if(Start.programURL == null && !new File(Start.path + "plugins" + File.separator + "emoticons").exists()) throw new IOException("no emoticons");
		emoticons =Preferences.getPlugable("emoticons");
		loadParser();
		InputStream  data = null;
		data = getClass().getResourceAsStream("/emoticons.xml");
		if(data==null)
		{
			try
	        {
				data = (new URL(Start.programURL + "plugins/emoticons/emoticons.xml")).openStream();
			}
			//else data = new FileInputStream(Start.path + "plugins" + File.separator + "emoticons" + File.separator + "emoticons.xml");
			catch (IOException ex)
			{
				if (new File(Start.path + "plugins" + File.separator + "emoticons").exists()) scanEmoticons();
				else throw new IOException("no emoticons");
			}
		}
		if(data != null)
		{
			try
			{
				parser.parse(new InputSource(new InputStreamReader(data)),new PluginsHandler(emoticons));
			}
			catch (SAXException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		for(Iterator i = emoticons.iterator();i.hasNext();)
		{//remove emoticons whitout description /deleted plugins still in preferences
			Object[] object = (Object[])i.next();
			if(object[3] == null)
			{
				System.out.println(object[0] + "not found or no description");
				i.remove();
			}
		}
		*/
		
		emoticons =Preferences.getPlugable("emoticons");
		smilies = new HashMap();
		smilieList = new HashMap();
		//backend.Start.loadPics("emoticons",this);
		for (Iterator i = emoticons.iterator();i.hasNext();)
		{
			Object[] temp = (Object[])i.next();
			if(((Boolean)temp[1]).booleanValue())
			{
				loadEmoticon((String)temp[4],(String)temp[3]);
			}
		}
		parser = null;
			//System.out.println(smilies);
			//makeSmilieList();
	}

	public void unload(){}
	
	/*
	public static PreferencesPanel getPreferencesPanel()
	{
		return null;
	}
	
	

	public void scanEmoticons()
	{
		List oldEmoticons = new ArrayList(emoticons);
		emoticons.clear();
		searchEmoticons();
		//System.out.println(this.toString());
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(Start.path + "plugins" + File.separator + "emoticons" + File.separator + "emoticons.xml"));
			writer.write(this.toString());
			writer.close();
			//new PrintWriter(new BufferedOutputStream(new FileOutputStream(Start.path +"plugins/plugins.xml"))).write(this.toString());
		}
		catch (IOException ex2)
		{
			ex2.printStackTrace();
		}
		for(Iterator i = oldEmoticons.iterator();i.hasNext();)
		{
			Object[] oldPlugin =(Object[])i.next();
			for(Iterator j = emoticons.iterator();j.hasNext();)
			{	
				Object[] newPlugin =(Object[])j.next();
				if(oldPlugin[0].equals(newPlugin[0]))
				{
					newPlugin[1] = oldPlugin[1];
					break;
				}
			}
		}
	}

	private void searchEmoticons()
	{//voeg samen met plugindata?
		String urlString = Start.path + "plugins" + File.separator + "emoticons";
		
		//urlString += dir + "/";
		//System.out.println(urlString);
		File path = new File(urlString);
		File file[] = path.listFiles();
		if(file == null)
		{
			//System.out.println("no emoticons");
			return;
		}
		for (int tel=0;tel<file.length;tel++)
		{
			File currentFile = file[tel];
			//if(currentFile.toString().endsWith(".jisp") || currentFile.toString().endsWith(".jar") ||currentFile.toString().endsWith(".zip"))
			if(currentFile.isFile())
			{
				//System.out.println(currentFile);
				getEmoticonInfo(currentFile);
			}
		}
	}

	private void getEmoticonInfo(File file)
	{
		//System.out.println(file);
		try
		{
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			URL urlJar = new URL("jar:"+file.toURL() +"!/" + name + "/");
			//System.out.println(urlJar);
			Object[] data = new Object[6];
			if(parser ==null) loadParser();
			try{
				parser.parse(new URL(urlJar,"icondef.xml").openStream(),new EmoticonsHandler(urlJar,data));
				//parser.parse(stream,new EmoticonsHandler(urlJar,data));
			} catch(ZipException e)
			{//no zip file so skip
				return;
			}
			boolean found = false;
			for(Iterator i = emoticons.iterator();i.hasNext();)
			{
				Object[] temp = (Object[])i.next();
				if(data[0].equals(temp[0]))
				{
					temp[2] = data[2];
					temp[3] = data[3];
					temp[4] = file.getName();
					found = true;
					break;
				}
			}
			if(!found)
			{//new plugin
				//System.out.println("data 0" + data[0]);
				data[1] = Boolean.TRUE;
				data[4] = file.getName();
				emoticons.add(data);
			}
		}
		catch (IOException ex)
		{
			System.err.println(ex.getMessage());
		}catch (SAXException ex)
		{
			ex.printStackTrace();
		}
	}


	public void appendToXML(StringBuffer xml)
	{
		appendHeader(xml);
		appendOpenTag(xml,"<plugins>");
		for(Iterator i = emoticons.iterator();i.hasNext();)
		{
			appendOpenTag(xml,"<plugin>");
			Object[] temp = (Object[]) i.next();
			appendElement(xml,"name",(String)temp[0]);
			appendElement(xml,"description",(String)temp[2]);
			appendElement(xml,"version",(String)temp[3]);
			appendElement(xml,"min_jeti_version",(String)temp[4]);//hackje
			appendCloseTag(xml,"</plugin>");
		}
		appendCloseTag(xml,"</plugins>");
	}
	*/

	public void init(JTextPane txtOutput,JPanel controls,final JTextPane txtInput,final JPopupMenu popupMenu,String type,JMenu menu) throws IllegalStateException
	{
		if(smilieList.isEmpty()) return;
		txtOutput.setEditorKit(new EmoticonsEditorKit());
			
		final JButton btnEmoticons = new JButton(new ImageIcon(getClass().getResource("emoticon.gif")));
		if (PluginsInfo.isPluginLoaded("xhtml"))
		{
			btnEmoticons.setToolTipText(I18N.gettext("emoticons.Emoticons"));
			btnEmoticons.setMargin(new Insets(0, 0, 0, 0));
			btnEmoticons.setPreferredSize(new Dimension(23, 23));
			btnEmoticons.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					popupMenu.show(btnEmoticons,10,btnEmoticons.getY());
				}
			});
			controls.add(btnEmoticons);
		}
		
		//final JMenu selectIconMenu = new JMenu(I18N.gettext("emoticons.Select_Icon"));
		
		JMenu mnuEmoticons = new JMenu(I18N.gettext("emoticons.Emoticons"));
		//JMenu setMenu = new JMenu(I18N.gettext("emoticons.Change_Set"));
		//a group of radio button menu items
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rbMenuItem= null;
		for(Iterator i =smilies.keySet().iterator();i.hasNext();)
		{
			final String iconSet = (String)i.next();
			rbMenuItem = new JRadioButtonMenuItem(iconSet);
			rbMenuItem.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					//popupMenu.remove()
				    currentIconSet =(List)smilies.get(iconSet);
					displayableIconList =(Emoticon[])smilieList.get(iconSet);
					popupMenu.setEnabled(true);
					buildSelectIcons(popupMenu,txtInput);
					btnEmoticons.setVisible(true);
				}
			});
			group.add(rbMenuItem);
			mnuEmoticons.add(rbMenuItem);
		}
		rbMenuItem = new JRadioButtonMenuItem(I18N.gettext("emoticons.No_Emoticons"));
		rbMenuItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentIconSet =null;
				popupMenu.setEnabled(false);
				btnEmoticons.setVisible(false);
			}
		});
		group.add(rbMenuItem);
		mnuEmoticons.add(rbMenuItem);
		mnuEmoticons.add(mnuEmoticons);
		if(menu!=null)menu.add(mnuEmoticons);



		//set first emoticonset
		
		//rbMenuItem = (JRadioButtonMenuItem)setMenu.getItem(0);
		currentIconSet = (List)smilies.get(type);
		displayableIconList = (Emoticon[])smilieList.get(type);
		
		if(currentIconSet==null)
		{
			type="default";
			currentIconSet = (List)smilies.get(type);
			displayableIconList = (Emoticon[])smilieList.get(type);
		}
		if(currentIconSet==null)
		{
			rbMenuItem = (JRadioButtonMenuItem)mnuEmoticons.getItem(0);
			currentIconSet = (List)smilies.get(rbMenuItem.getText());
			displayableIconList = (Emoticon[])smilieList.get(rbMenuItem.getText());
		}
		
		//rbMenuItem.setSelected(true);

		//popupMenu.add(selectIconMenu);
		buildSelectIcons(popupMenu,txtInput);
				

	    /*
		JComboBox cmbSets = new JComboBox();
		for(Iterator i =smilies.keySet().iterator();i.hasNext();)
		{
		    cmbSets.addItem((String)i.next());
		}
		controls.add(cmbSets);
		cmbSets.addItemListener(new ItemListener()
		{
		    public void itemStateChanged(ItemEvent e)
			{
				currentIconSet =(Map)smilies.get((String)e.getItem());
		    }
		});
		*/
		//currentIconSet = (Map)smilies.get("MSN New Version");


	}

	private void buildSelectIcons(JPopupMenu  subMenu,final JTextPane txtInvoer)
	{
		subMenu.removeAll();
		//JMenuItem
		JButton menuItem =null;
		
		for(int i=0;i<displayableIconList.length;i++)
		{
			Emoticon emoticon = displayableIconList[i];
			Icon icon = emoticon.getIcon();
			final String smilie = emoticon.toString();
			if(smilie.length() > 5)
			{//max text length = 5 
				menuItem = new JButton("...",icon);
				menuItem.setToolTipText(smilie);
			}
			else menuItem = new JButton(smilie,icon);
			
			menuItem.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent arg0)
				{
					Document doc = txtInvoer.getDocument();
					try
					{
				        doc.insertString(txtInvoer.getCaretPosition(),smilie,null);
					}
					catch(BadLocationException e2){e2.printStackTrace();}
				}
			});
			menuItem.setMargin(new java.awt.Insets(0,0,0,0));
			menuItem.setHorizontalAlignment(SwingConstants.LEADING );
			menuItem.setBackground(java.awt.Color.white);

			//menuItem.setPreferredSize(new java.awt.Dimension(icon.getIconWidth() +50 ,icon.getIconHeight()));
		//	menuItem.setLayout(new java.awt.FlowLayout());
			subMenu.add(menuItem);
		}
		int size = displayableIconList.length;
		//int x = size/2;
		
		//System.out.println(size + " : " + x);

		//JPopupMenu pm = subMenu.getPopupMenu();
        //pm.setLayout(new java.awt.FlowLayout(SwingConstants.CENTER,0,0));
		//pm.setLayout(new java.awt.GridLayout((int)Math.ceil(size/5.0),5));
		subMenu.setLayout(new java.awt.GridLayout((int)Math.sqrt(size),(int)Math.sqrt(size)));
		subMenu.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				txtInvoer.requestFocusInWindow();
			}
		});
		
		//pm.setPopupSize(500,500);
		//pm.setPreferredSize(new java.awt.Dimension(300,300));
		//pm.setPreferredSize(new java.awt.Dimension(300,50));
//		pm.setBackground(java.awt.Color.white);
        //  setMinimumSize(getPreferredSize());
		//subMenu.setLayout(new BoxLayout(subMenu,BoxLayout.X_AXIS  ) );
		//return subMenu;
	}
	
//	class menuButton extends JButton implements MenuElement
//	{
//		 public void menuSelectionChanged(boolean isIncluded) {
//		 	ButtonModel model = getModel();
//	        if(model.isArmed() != isIncluded) {
//	            model.setArmed(isIncluded);
//	        }
//	    }
//		 
//		 public Component getComponent() {
//	        return this;
//	    }
//	}
	
	public void insertEmoticons(List wordList)
	{
		if(currentIconSet != null)
		{//no icons selected
			for(int i=0;i<wordList.size();i++) 
			{
				Word word =(Word) wordList.get(i);
				String token = word.word;
				if(token.length()>1)
				{	
					//System.out.println(token);
					//System.out.println(i);
					for(Iterator j=currentIconSet.iterator();j.hasNext();)
					{	
						//Map.Entry entry =(Map.Entry) j.next();
						Emoticon emoticon = (Emoticon)j.next();
						String toCheck = emoticon.toString();
						if (token.equals(toCheck))
						{
							setIcon(sas,emoticon);
							word.addAttributes(sas);
							break;
						}
						else if (token.startsWith(toCheck))
						{
							word.word = "o";
							setIcon(sas,emoticon);
							word.addAttributes(sas);
							//split smiley and word
							wordList.add(i+1,new Word(" "));
							wordList.add(i+2,new Word(token.substring(toCheck.length())));
							i++;//don't check inserted space
							break;
						}
						else if (token.endsWith(toCheck))
						{
							word.word = token.substring(0,token.length()-toCheck.length());						
							//split smiley and word
							wordList.add(i+1,new Word(" "));
							setIcon(sas,emoticon);
							wordList.add(i+2,new Word("o",(SimpleAttributeSet)sas.clone()));
							i--;//check if there was another smiley in front
							break;
						}
					}
				}
			}
		}
	}
	
	private void setIcon(SimpleAttributeSet sas, Emoticon emoticon)
	{
		StyleConstants.setIcon(sas,new ImageIcon(emoticon.getIcon().getImage()));
	}

//	public void reloadEmoticon(String name,String type)
//	{
//		if(!smilies.containsKey(type))loadEmoticon(name, type);
//	}
	
	public void loadEmoticon(String name,String type)
	{
		String name2 = name.substring(0, name.lastIndexOf("."));
		InputStream stream = null;
		URL url = null;
		try
		{
			//if(Start.programURL != null)
			url =  new URL ("jar:" + Start.programURL + "plugins/emoticons/" + name +"!/" + name2 + "/");
			//else url =  new URL ("jar:" + Start.localURL + "plugins/emoticons/" + name +"!/" + name2 + "/");
			stream =  new URL(url,"icondef.xml").openStream();
		}
		catch (IOException ex)
		{
			//webstart
			url=null;
			System.out.println("webstart loading");
			stream =  getClass().getClassLoader().getResourceAsStream("msn_messenger-6.0/icondef.xml");
			//ex.printStackTrace();
		}
		try
		{
			if(parser == null) loadParser();
			List icons =(List) smilies.get(type);
			if(icons==null)
			{
				icons = new LinkedList();
				smilies.put(type,icons);
			}
			parser.parse(stream ,new EmoticonsHandler(url,icons));
			Collections.sort(icons);
			HashSet set = new HashSet(icons);
			Emoticon[] iconList = (Emoticon[])smilieList.get(type);
			if(iconList!=null)
			{
				for(int i=0;i<iconList.length;i++)
				{
					set.add(iconList[i]);
				}
			}
			else iconList = new Emoticon[set.size()];
			iconList = (Emoticon[])set.toArray(iconList);
			Arrays.sort(iconList);
			smilieList.put(type,iconList);
			
		}
		catch (SAXException ex)
		{
			ex.getException().printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public void unloadEmoticon(String name)
	{
		smilieList.remove(name);
		smilies.remove(name);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
