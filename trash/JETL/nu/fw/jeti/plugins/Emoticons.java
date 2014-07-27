package nu.fw.jeti.plugins;
import java.util.List;

import javax.swing.text.*;
import javax.swing.*;

/**
  * <p>Copyright: Copyright (c) 2001</p>
  * @author E.S. de Boer
  */

public interface Emoticons
{
	public void insertEmoticons(List wordList);
		
	public void init(JTextPane txtOutput,JPanel controls,JTextPane txtInput,JPopupMenu popup,String type,JMenu menu) throws IllegalStateException;
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
