// Created on 16-okt-2003
package nu.fw.jeti.plugins.spell;

import java.awt.*;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.*;

import com.swabunga.spell.engine.Configuration;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.event.*;
import com.swabunga.spell.swing.JSpellDialog;



/** This class spellchecks a JTextComponent throwing up a Dialog everytime
 *  it encounters a misspelled word.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 * @author E.S. de Boer
 */

public class JTextComponentSpellChecker implements SpellCheckListener {

//    private static final String COMPLETED="COMPLETED";
  private String dialogTitle = null;

  private SpellChecker spellCheck = null;
  private JSpellDialog dlg = null;
  transient private JTextComponent textComponent = null;
 // private ResourceBundle messages;

  // Constructor
  public JTextComponentSpellChecker(SpellDictionary dict) {
    this(dict, null);
  }
//
//  // Convinient Constructors, for those lazy guys.
//  public JTextComponentSpellChecker(String dictFile) throws IOException {
//    this(dictFile, null);
//  }
//
//  public JTextComponentSpellChecker(String dictFile, String title) throws IOException {
//    this(new SpellDictionaryHashMap(new File(dictFile)), title);
//  }
//
//  public JTextComponentSpellChecker(String dictFile, String phoneticFile, String title) throws IOException {
//    this(new SpellDictionaryHashMap(new File(dictFile), new File(phoneticFile)), title);
//  }

  public JTextComponentSpellChecker(SpellDictionary dict, String title) {
    spellCheck = new SpellChecker(dict);
    spellCheck.addSpellCheckListener(this);
    dialogTitle = title;
    //messages = ResourceBundle.getBundle("com.swabunga.spell.swing.messages", Locale.getDefault());
  }

  // MEMBER METHODS
  
  /**
   * Set user dictionary (used when a word is added)
   */
//  public void setUserDictionary(SpellDictionary dictionary) {
//    if (spellCheck != null)
//      spellCheck.setUserDictionary(dictionary);
//  }

  private void setupDialog() {

    Component comp = SwingUtilities.getRoot(textComponent);

    // Probably the most common situation efter the first time.
    if (dlg != null && dlg.getOwner() == comp)
      return;

    if (comp != null && comp instanceof Window) {
      if (comp instanceof Frame)
        dlg = new JSpellDialog((Frame) comp, dialogTitle, true);
      if (comp instanceof Dialog)
        dlg = new JSpellDialog((Dialog) comp, dialogTitle, true);
      // Put the dialog in the middle of it's parent.
      if (dlg != null) {
        Window win = (Window) comp;
        int x = (int) (win.getLocation().getX() + win.getWidth() / 2 - dlg.getWidth() / 2);
        int y = (int) (win.getLocation().getY() + win.getHeight() / 2 - dlg.getHeight() / 2);
        dlg.setLocation(x, y);
      }
    } else {
      dlg = new JSpellDialog((Frame) null, dialogTitle, true);
    }
  }

  public synchronized int spellCheck(JTextComponent textComp) {
    
    this.textComponent = textComp;

    DocumentWordTokenizer tokenizer = new DocumentWordTokenizer(textComp.getDocument());
    int exitStatus = spellCheck.checkSpelling(tokenizer);

    //textComp.requestFocus();
    //textComp.setCaretPosition(0);
    this.textComponent = null;
    return exitStatus;
  }

  public void spellingError(SpellCheckEvent event) {

//        java.util.List suggestions = event.getSuggestions();
   // event.getSuggestions();
    int start = event.getWordContextPosition();
    int end = start + event.getInvalidWord().length();

//    // Mark the invalid word in TextComponent
//    textComp.requestFocus();
//    textComp.setCaretPosition(0);
//    textComp.setCaretPosition(start);
//    textComp.moveCaretPosition(end);
    
	Highlighter.HighlightPainter painter = new Painter();
	try
	{
		textComponent.getHighlighter().addHighlight(start,end, painter );
	}
	catch(BadLocationException ble) {ble.printStackTrace(); }
  }
  
  public List getSuggestions(String badWord)
  {
  	System.out.println(badWord);
  		return spellCheck.getSuggestions(badWord, Configuration.getConfiguration().getInteger(Configuration.SPELL_THRESHOLD));
  }
  

  public void correct(String word,int location,JTextComponent textComponent)
  {
  	setupDialog();
  	SpellCheckEvent event = new BasicSpellCheckEvent(word,spellCheck.getSuggestions(word, Configuration.getConfiguration().getInteger(Configuration.SPELL_THRESHOLD)),0);
  	dlg.show(event);
  	String replaceWord =event.getReplaceWord(); 
  	if(replaceWord != null)
  	{	
  		Document doc = textComponent.getDocument();
  		try
		{
  			
			doc.remove(location, word.length());
			
			textComponent.getDocument().insertString(location, replaceWord, null);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
  	}
  	
  }
  
  class Painter extends DefaultHighlighter.DefaultHighlightPainter
  {
  		public Painter()
  		{
  			super(Color.RED);
  		}
  		
  		/**
  		 * This method is overridden to get desired behaviour.
  		 *
  		 * Paints a portion of a highlight.
  		 *
  		 * @param g the graphics context
  		 * @param offs0 the starting model offset >= 0
  		 * @param offs1 the ending model offset >= offs1
  		 * @param bounds the bounding box of the view, which is not
  		 *		necessarily the region to paint.
  		 * @param c the editor
  		 * @param view View painting for
  		 * @return region drawing occured in
  		 */
  		public Shape paintLayer(Graphics g, int offs0, int offs1,
								Shape bounds, JTextComponent c, View view)
  		{
  			Rectangle b = bounds.getBounds();
  			try {
  				g.setColor(super.getColor());
  				Rectangle r1 = c.modelToView(offs0);
  				Rectangle r2 = c.modelToView(offs1);
  				
  				int x = r1.x;
  				int y = r1.y + r1.height;
  				boolean up = false;
  				int a = 0;
  				while (x < r2.x) {
  					if (up) a++; else a--;
  					if (a!=0) up=!up;
  					y+=a;
  					g.drawLine(x, y, x, y);
  					x++;
  				}
  				
  			}
  			catch(BadLocationException ex) {
  				ex.printStackTrace();
  			}
  			return b;
  		}
  }
  
  
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
