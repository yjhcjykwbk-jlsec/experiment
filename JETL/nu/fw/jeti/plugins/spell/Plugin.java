// Created on 16-okt-2003
package nu.fw.jeti.plugins.spell;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.*;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.Spell;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryDichoDisk;
import com.swabunga.spell.engine.Word;



/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins,Spell
{
	public final static String VERSION = "0.2";
	public final static String DESCRIPTION = "Spellcheck";
	public final static String MIN_JETI_VERSION = "0.5.1";
	public final static String NAME = "spell";
	public final static String ABOUT = "by E.S. de boer, uses Jazzy (jazzy.sourceforge.net)";
	private JTextComponentSpellChecker spellChecker;
	private static Map dictionaries = new HashMap(2);
	private static String[] availableLanguages=new String[0];
	private boolean disabled=false;
	//private static String[] availableLanguages;
	//private static SpellDictionary dictionary;
			
	public static void init (Backend backend)
	{
		loadDictionaries();
		String path = Start.path  + "dictionaries" + File.separator;
		for(int i=0;i<availableLanguages.length;i++)
		{
			loadDictionary(path,availableLanguages[i],true);
		}
		
		//if(dictionaries.isEmpty()) throw new InstantiationException(I18N.gettext("spell.Dictionary not found"));
	}
		
	public Plugin()
	{
		if(dictionaries.isEmpty())
		{
			disabled=true;
			return;
		}
		String defaultLanguage =Preferences.getString("spell","defaultLanguage", null);
		SpellDictionary dictionary = (SpellDictionary)dictionaries.get(defaultLanguage);
		if(dictionary==null)
		{
			dictionary = (SpellDictionary)dictionaries.values().iterator().next();
		}
		spellChecker = new JTextComponentSpellChecker(dictionary);
	}
	
	public void addChangeDictoryMenuEntry(JMenu menu)
	{
		if(availableLanguages.length<2)return;
		JMenu subMenu = new JMenu(I18N.gettext("spell.Dictionary"));
		for(int i=0;i<availableLanguages.length;i++)
		{
			String language = availableLanguages[i].
				substring(0,availableLanguages[i].indexOf('.'));
			JMenuItem item = new JMenuItem(language);
			item.setActionCommand(availableLanguages[i]);
			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					disabled=false;
					JMenuItem item = ((JMenuItem)e.getSource());
					String language =item.getText();
					if(!dictionaries.containsKey(language))
					{
						System.out.println(item.getActionCommand());
						String path =Start.path  + "dictionaries" + File.separator;
						loadDictionary(path,item.getActionCommand(),false);
					}
					SpellDictionary dictionary = (SpellDictionary)dictionaries.get(language);
					spellChecker = new JTextComponentSpellChecker(dictionary);
				}
			});
			subMenu.add(item);
		}
		JMenuItem item = new JMenuItem(I18N.gettext("spell.None"));
		item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				disabled=true;
			}
		});
		subMenu.add(item);
		menu.add(subMenu);
	}
	

	public void unload()
	{
		spellChecker = null;	
		dictionaries=null;
		System.out.println("unload spell");
	}
	
	public static String[] getLanguages()
	{
		return availableLanguages;
	}
	
	private static void loadDictionaries()
	{
		BufferedReader data = null;
		try
		{
			//if(Start.programURL != null)
			{
				String path = Start.path  + "dictionaries" + File.separator;
				try
				{
					data =new BufferedReader(new FileReader(path + "list.txt"));
				}
				catch (IOException ex)
				{
					scanDictionaries();
					return;
				}
				LinkedList list = new LinkedList();
				while (true)
				{
					String file = data.readLine();
					if (file ==null) break;//end of stream
					list.add(file);
					//readDictionary(path,file);
				}
				availableLanguages = (String[])list.toArray(availableLanguages);
			}
		}
		catch(IOException e){e.printStackTrace();}
		finally 
		{
			if(data!=null)
			{	
				try
				{
					data.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}
	
	protected static void scanDictionaries()
	{
		String urlString = Start.path  + "dictionaries" + File.separator;
		File path = new File(urlString);
		File file[] = path.listFiles();
		if(file == null)
		{
			System.err.println(I18N.gettext("main.error.statusicons_dir_not_found"));
			return;
		}
		List list = new LinkedList();
		try
	    {
	        BufferedWriter writer = new BufferedWriter(new FileWriter(urlString + "list.txt"));
			for (int tel=0;tel<file.length;tel++)
			{
				File currentFile = file[tel];
				String fileName = currentFile.getName();
			  	if(fileName.endsWith(".dico"))
				{
			  		String file2 = fileName.substring(0,fileName.length()-5);
			  		file2 += ".phon";
			  		if(new File(urlString + file2).exists())
			  		{
			  			writer.write(fileName + " " + file2 + "\r\n");
			  			list.add(fileName + " " + file2);
			  		}
			  		else
			  		{
			  			writer.write(fileName + "\r\n");
			  			list.add(fileName);
			  		}
				}
			}
			writer.close();
			availableLanguages = (String[])list.toArray(availableLanguages);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
	    }
	}
	
	protected static void unloadDictionary(String language)
	{
		dictionaries.remove(language);
	}
	
	
	protected static void loadDictionary(String files)
	{
		loadDictionary(Start.path  + "dictionaries" + File.separator,files,true);
	}
	
	private static void loadDictionary(String path, String files,boolean check)
	{
		try
		{
			File dict =null;
			File phon =null;
			String dictionary=null;
			int space = files.indexOf(' ');
			if(space==-1)
			{
				dictionary = files;
			}
			else
			{
				dictionary = files.substring(0,space);
				phon = new File(path + files.substring(space+1,files.length()));
			}
			String language = dictionary.substring(0,dictionary.length()-5);
			if(Preferences.getBoolean("spell", language, false) || !check)
			{
				System.out.println(language);
				dict = new File(path + dictionary);
				dictionaries.put(language, new SpellDictionaryDichoDisk(dict,phon));
			}
			
		}
		catch (IOException e)
		{
			Popups.errorPopup(e.getLocalizedMessage(), I18N.gettext("spell.Dictionary not found"));
		}
	}
	
	public boolean rightClick(final JTextComponent text,MouseEvent e)
	{
		int offset = text.viewToModel(e.getPoint());
		Highlighter.Highlight[] highlights = text.getHighlighter().getHighlights();
		for (int i = 0; i < highlights.length; i++)
		{
			final Highlighter.Highlight h = highlights[i];
			if (offset >= h.getStartOffset() &&  offset <= h.getEndOffset())
			{
				try
				{	
					text.getHighlighter().removeHighlight(h);
					String badWord = text.getText(h.getStartOffset(),h.getEndOffset()-h.getStartOffset());	
					correct(text,badWord,h.getStartOffset()).show(e.getComponent(),e.getX(),e.getY());
				}catch (BadLocationException e2)
				{
					e2.printStackTrace();
				}

				return true;
			}
		}
		return false;
	}
	
	private JPopupMenu correct(final JTextComponent text,final String badWord,final int StartOfWord)
	{
			List suggestion = spellChecker.getSuggestions(badWord);
			JPopupMenu popupMenu = new JPopupMenu();
			for(Iterator j = suggestion.iterator();j.hasNext();)
			{
				final String word = ((Word)j.next()).toString();
				JMenuItem menuItem = new JMenuItem(word);
				menuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Document doc = text.getDocument();
						try
						{
							doc.remove(StartOfWord, badWord.length());
							text.getDocument().insertString(StartOfWord, word, null);
						}
						catch (BadLocationException e2)
						{
							e2.printStackTrace();
						}
					}
				});
				popupMenu.add(menuItem);
			}
			JMenuItem menuItem = new JMenuItem(I18N.gettext("spell.Ignore"),'I'); // TODO: mnemonic here
			popupMenu.add(menuItem);
			return popupMenu;
			//spellChecker.correct(text.getText(h.getStartOffset(),h.getEndOffset()-h.getStartOffset()),h.getStartOffset(),text);
		
	}

	public void keyReleased(KeyEvent e, JTextComponent text)
	{
		if(disabled)return;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if (e.isControlDown() || e.isShiftDown())
			{
				try
				{
					int pos = text.getCaretPosition();
					int wordStart = Utilities.getWordStart(text, pos);
					int wordEnd = Utilities.getWordEnd(text, pos);
					String word = text.getDocument().getText(wordStart,
							wordEnd - wordStart);
					word = word.trim();
					if (word.length() == 0)
					{// if space try word before space
						if (pos == 0) return;
						pos--;
						wordStart = Utilities.getWordStart(text, pos);
						wordEnd = Utilities.getWordEnd(text, pos);
						word = text.getDocument().getText(wordStart,
								wordEnd - wordStart);
					}
					if (word.trim().length() == 0) return;
					Rectangle rect = text.modelToView(wordEnd);
					correct(text, word, wordStart).show(text, rect.x, rect.y);
				} catch (BadLocationException e2)
				{
					e2.printStackTrace();
				}
			} else
			{
				spellChecker.spellCheck(text);
				{// remove fast type highlights and remove when correcting
					int offset = text.getCaretPosition();
					Highlighter.Highlight[] highlights = text.getHighlighter()
							.getHighlights();
					for (int i = 0; i < highlights.length; i++)
					{
						Highlighter.Highlight h = highlights[i];
						if (offset >= h.getStartOffset()
								&& offset <= h.getEndOffset())
						{
							text.getHighlighter().removeHighlight(h);
						}
					}
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
