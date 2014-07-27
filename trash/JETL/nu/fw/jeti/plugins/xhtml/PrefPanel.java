package nu.fw.jeti.plugins.xhtml;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.plugins.xhtml.fontchooser.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class PrefPanel extends PreferencesPanel
{
	FontPanel fontPanel = new FontPanel();

	public PrefPanel(Backend backend)
	{
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3, 5, 0, 3);
        c.gridwidth = GridBagConstraints.REMAINDER;

		//this.backend = backend;
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
		fontPanel.setAttributes(set);

 		add(new JLabel(I18N.gettext("xhtml.The_font_used_when_sending_messages")), c);
		add(fontPanel, c);

        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);
	}

	public void savePreferences()
	{
		AttributeSet set = fontPanel.getAttributes();
		Color c;
		if (set.isDefined(StyleConstants.Foreground))
		{
			c = (Color) set.getAttribute(StyleConstants.Foreground);
			System.out.println(c);
			Preferences.putInteger("xhtml", "foreground", c.getRGB());
		}
		if (set.isDefined(StyleConstants.Background))
		{
			c = (Color) set.getAttribute(StyleConstants.Background);
			System.out.println(c);
			Preferences.putInteger("xhtml", "background", c.getRGB());
		}
		if (set.isDefined(StyleConstants.FontFamily))
		{
			Preferences.putString("xhtml", "font-family", StyleConstants.getFontFamily(set));
		}
		if (set.isDefined(StyleConstants.FontSize))
		{
			Preferences.putInteger("xhtml", "font-size", StyleConstants.getFontSize(set));
		}
	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
