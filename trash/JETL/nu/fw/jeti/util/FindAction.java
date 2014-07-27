/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 27-feb-2004
 */
 
package nu.fw.jeti.util;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 * @author E.S. de Boer
 *
 */
public class FindAction extends AbstractAction
{
	private String searchString = null;
	private int startPoint = 0;
	private int endPoint = 0;
	
	public FindAction(){
		//super("find", EditorUtilities.loadIcon("icons/Find16.gif"));
		super(I18N.gettext("main.popup.Find"));
		putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_F));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, ActionEvent.CTRL_MASK));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e != null) 
		{
			Object o = e.getSource();
			if (o instanceof JTextComponent) 
			{
				JTextComponent text = (JTextComponent) o;
				if(e.getModifiers() == 0 )
				{//find Next
					if (searchString==null) askSearchString(text);
					if (searchString!=null)search(text,text.getCaretPosition());
				}
				else
				{	
					askSearchString(text);
					if (searchString!=null)search(text,0);
				}
			}
	    }
	}
	
	private void askSearchString(JTextComponent text)
	{
		searchString = JOptionPane.showInputDialog(text.getTopLevelAncestor(), I18N.gettext("main.popup.Enter_the_text_to_search_for"),text.getSelectedText());
	}
	

	private void search(JTextComponent text, int startPositionIn)
	{
		String fulltext = text.getText();

//		//	strip out the surplus \r characters
//		StringTokenizer tokenizer = new StringTokenizer(fulltext, "\r");
//		StringBuffer buffer = new StringBuffer();
//		while( tokenizer.hasMoreTokens() ){
//			buffer.append( tokenizer.nextToken() );
//		}

		//get the substring based on the requested start position
		fulltext = ( fulltext ).substring(startPositionIn);

		int searchStringLength = searchString.length();
		// so long as we can find the text in the file, go ahead and highlight it
		// if we can't show the dialog
		if(fulltext.indexOf(searchString) > -1){
			startPoint = (fulltext.indexOf(searchString) ) + startPositionIn;

			endPoint = startPoint + searchStringLength;
			text.requestFocus();
			text.setCaretPosition(startPoint);
		//move caret to endPoint - thus highlighting the selected text
			text.moveCaretPosition(endPoint);
		//make the selection visible
			//text.getCaret().setSelectionVisible(true);
		}
		else if (startPositionIn!=0){
			if(JOptionPane.showConfirmDialog(text.getTopLevelAncestor(),MessageFormat.format(I18N.gettext("main.popup.{0}_not_found_until_the_end_of_this_file,_start_again_from_the_beginning?"),new Object[]{searchString})) == JOptionPane.OK_OPTION)
			{
				search(text, 0);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(text.getTopLevelAncestor(),MessageFormat.format(I18N.gettext("main.popup.{0}_not_found"),new Object[]{searchString}));
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
