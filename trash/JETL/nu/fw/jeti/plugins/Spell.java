// Created on 16-okt-2003
package nu.fw.jeti.plugins;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.text.JTextComponent;

/**
 * @author E.S. de Boer
 *
 */
public interface Spell
{
	public boolean rightClick(JTextComponent text,MouseEvent e);
	
	public void keyReleased(KeyEvent e,JTextComponent text);
	
	public void addChangeDictoryMenuEntry(JMenu menu);

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
