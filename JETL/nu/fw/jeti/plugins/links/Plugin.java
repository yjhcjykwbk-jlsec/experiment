/* 
 *	Created on 3-march-2004
 */
 
package nu.fw.jeti.plugins.links;



import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.*;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.Translator;
import nu.fw.jeti.plugins.Word;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, Translator
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "links.Make_hyperlinks_clickable";
	public final static String MIN_JETI_VERSION = "0.5.3";
	public final static String NAME = "links";
	public final static String ABOUT = "by E.S. de Boer";
	private SimpleAttributeSet linkAttributeSet = new SimpleAttributeSet();
	
	public Plugin()
	{
		StyleConstants.setForeground(linkAttributeSet, Color.BLUE);
		StyleConstants.setUnderline(linkAttributeSet, true);
	}
        
    public static void init(Backend backend)
    {
           Browser.init();
    }
           
    
    public void unload(){}
    
    public void init(final JTextComponent text)
    {
    	text.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					int location = text.viewToModel(e.getPoint());
					//System.out.println(location);
					StyledDocument doc = (StyledDocument) text.getDocument();
					AttributeSet set = doc.getCharacterElement(location).getAttributes();
					String url = (String)set.getAttribute("link");
					if(url !=null)
					{
						Browser.init();
						try
						{
							Browser.displayURL(url);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					}
					
				}
			}
		});
    }
    
    
    public void translate(List wordList)
	{
		for(int i=0;i<wordList.size();i++) 
	    {
			Word word = (Word)wordList.get(i);
	         String token = word.word;
	         if(token.startsWith("http"))
	         {
	         	checkIfURL(word,"");
	         }
	         else if(token.startsWith("www") && token.indexOf(".") > 0   && !token.endsWith("."))
	         {
	         	checkIfURL(word,"http://");
	         }
	    }
	}

	private void checkIfURL(Word word,String prefix)
	{
		String token = word.word;
		try
		{//check if really url
			String url =prefix + token;
			new URL(url);
			linkAttributeSet.addAttribute("link", url);
			word.addAttributes(linkAttributeSet);
		}
		catch (MalformedURLException e)
		{//no url
			
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
