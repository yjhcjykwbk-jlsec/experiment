/*
 * SimplyHTML, a word processor based on Java, HTML and CSS
 * Copyright (C) 2002 Ulrich Hilger
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package nu.fw.jeti.plugins.xhtml.fontchooser;

import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.StyleSheet;

import nu.fw.jeti.util.I18N;

/**
 * A panel for showing and manipulating font information.
 * <p>
 * <code>FontPanel</code> shows and manipulates CSS attributes.
 *  To set it to HTML attributes, methods setAttributes and getAttributes have to be overridden.
 * </p>
 * 
 * @author Ulrich Hilger
 * @author Light Development
 * @author <a href="http://www.lightdev.com">http://www.lightdev.com </a>
 * @author <a href="mailto:info@lightdev.com">info@lightdev.com </a>
 * @author published under the terms and conditions of the GNU General Public License,
 *  for details see file gpl.txt in the distribution package of this software
 * @version stage 9, release 4, January 12, 2003
 */

public class FontPanel extends JPanel implements TitledPickList.TitledPickListListener, ColorPanel.ColorPanelListener
{

	/** a text field to show a sample of the selected font attributes */
	JTextField sample = new JTextField();

	/** table for automatic font component value read/write */
	private Vector fontComponents = new Vector(0);

	public FontPanel()
	{
		setLayout(new BorderLayout(5, 5));

		/** create a label for previewing font selections */
		sample.setText("");
		sample.setEditable(false);
		sample.setPreferredSize(new Dimension(200, 50));
		sample.setHorizontalAlignment(SwingConstants.CENTER);
		sample.setText(I18N.gettext("xhtml.The_quick_brown_fox_jumped_over_the_lazy_dogs"));
		JPanel previewPanel = new JPanel(new BorderLayout());
		previewPanel.add(sample, BorderLayout.CENTER);
		previewPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), I18N.gettext("xhtml.Preview")));

		/**
		 * create a pick list for family filled with available font family names
		 */
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		FamilyPickList family = new FamilyPickList(ge.getAvailableFontFamilyNames(), I18N.gettext("xhtml.Font_Family"));
		family.addTitledPickListListener(this);
		fontComponents.add(family);

		/** create a pick list for font size */
		String[] fontSizes = new String[] { "8", "10", "12", "14", "18", "24"};
		SizePickList size = new SizePickList(fontSizes, (I18N.gettext("xhtml.Size")));
		size.addTitledPickListListener(this);
		fontComponents.add(size);

		/** wrap together family and size */
		JPanel familySizePanel = new JPanel(new BorderLayout(5, 5));
		familySizePanel.add(family, BorderLayout.CENTER);
		familySizePanel.add(size, BorderLayout.EAST);

		/** create a panel to put font parts family, size and stlye in */
		JPanel fontPartsPanel = new JPanel(new BorderLayout(5, 5));
		fontPartsPanel.add(familySizePanel, BorderLayout.CENTER);
		String[] fontStyles = new String[] { I18N.gettext("xhtml.Plain"), I18N.gettext("xhtml.Bold"), I18N.gettext("xhtml.Italic"),
				I18N.gettext("xhtml.BoldItalic")};
		StylePickList style = new StylePickList(fontStyles, I18N.gettext("xhtml.Style"));
		style.addTitledPickListListener(this);
		fontPartsPanel.add(style, BorderLayout.EAST);
		fontComponents.add(style);

		/** create a panel for underline / line through */
		EffectPanel linePanel = new EffectPanel();
		fontComponents.add(linePanel);

		/** create a panel for color choices */
		JPanel colorPanel = new JPanel(new GridLayout(2, 1, 3, 3));
		colorPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), I18N.gettext("xhtml.Color")));
		ColorPanel fCol = new ColorPanel(I18N.gettext("xhtml.foreground"), Color.black);
		fCol.addColorPanelListener(this);
		fontComponents.add(fCol);
		ColorPanel bCol = new ColorPanel(I18N.gettext("xhtml.background"), Color.white);
		bCol.addColorPanelListener(this);
		fontComponents.add(bCol);
		colorPanel.add(fCol);
		colorPanel.add(bCol);

		sample.setForeground(Color.black);
		sample.setBackground(Color.white);

		/** create a panel to combine line and color choices */
		JPanel eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(linePanel, BorderLayout.NORTH);
		eastPanel.add(colorPanel, BorderLayout.SOUTH);

		/** add all font controls to our font panel */
		add(fontPartsPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		add(previewPanel, BorderLayout.SOUTH);
		add(new JPanel(), BorderLayout.NORTH);
		add(new JPanel(), BorderLayout.WEST);
	}

	/**
	 * construct a FontPanel and display a set of attributes
	 * 
	 * @param frame the main frame having the ResourceBundle
	 * @param a the set of attributes to display
	 */
	public FontPanel(AttributeSet a)
	{
		this();

		/** set the new FontPanel to display our set of attributes */
		setAttributes(a);
	}

	/**
	 * handle ColorChangeEvents from one of our color panels
	 * 
	 * @param e the ColorPanelEvent to handle
	 */
	public void colorChanged(ColorPanel.ColorPanelEvent e)
	{
		ColorPanel source = (ColorPanel) e.getSource();
		if (source.getAttributeKey() == CSS.Attribute.COLOR)
		{
			sample.setForeground(source.getColor());
		} else if (source.getAttributeKey() == CSS.Attribute.BACKGROUND_COLOR)
		{
			sample.setBackground(source.getColor());
		}
	}

	/**
	 * set all components of this FontPanel to reflect a set of attributes.
	 * 
	 * @param a the set of attributes to show
	 */
	public void setAttributes(AttributeSet a)
	{
		Enumeration components = fontComponents.elements();
		while (components.hasMoreElements())
		{
			((AttributeComponent) components.nextElement()).setValue(a);
		}
	}

	/**
	 * get the set of attributes resulting from the settings on this FontPanel.
	 * 
	 * @return the set of attributes set in this FontPanel
	 */
	public AttributeSet getAttributes()
	{
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		Enumeration components = fontComponents.elements();
		while (components.hasMoreElements())
		{
			attributes.addAttributes(((AttributeComponent) components.nextElement()).getValue());
		}
		return attributes;
	}

	public AttributeSet getAttributes(boolean includeUnchanged)
	{
		if (includeUnchanged)
		{
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			Enumeration components = fontComponents.elements();
			while (components.hasMoreElements())
			{
				attributes.addAttributes(((AttributeComponent) components.nextElement()).getValue(includeUnchanged));
			}
			return attributes;
		} else
		{
			return getAttributes();
		}
	}

	public void reset()
	{
		Object c;
		for (int i = 0; i < fontComponents.size(); i++)
		{
			c = fontComponents.get(i);
			if (c instanceof FamilyPickList)
			{
				((FamilyPickList) c).reset();
			} else if (c instanceof SizePickList)
			{
				((SizePickList) c).reset();
			} else if (c instanceof StylePickList)
			{
				((StylePickList) c).reset();
			}
		}
	}

	/**
	 * if another value was picked from a list, update the sample
	 */
	public void valueChanged(TitledPickList.TitledPickListEvent e)
	{
		Object source = e.getSource();
		Font saveFont = sample.getFont();
		if (source instanceof FamilyPickList)
		{
			sample.setFont(new Font(((FamilyPickList) source).getFamily(), saveFont.getStyle(), saveFont.getSize()));
		} else if (source instanceof SizePickList)
		{
			sample.setFont(new Font(saveFont.getFamily(), saveFont.getStyle(), Integer.parseInt((String) ((SizePickList) source).getSelection())));
			/* adjustFontSize(Integer.parseInt((String) ((SizePickList) source).getSelection())))); */
		} else if (source instanceof StylePickList)
		{
			sample.setFont(new Font(saveFont.getFamily(), ((StylePickList) source).getFontStyle(), saveFont.getSize()));
		}
	}

	/**
	 * extend <code>TitledPickList</code> with a way to set values special to font family values
	 */
	class FamilyPickList extends TitledPickList implements AttributeComponent
	{

		private int setValCount = 0;
		private Object originalValue;

		/**
		 * constructor
		 * 
		 * @param options the options to be selectable in this list
		 * @param titleText the title for the pick list
		 */
		FamilyPickList(String[] options, String titleText)
		{
			super(options, titleText);
		}

		/**
		 * set the value of this <code>TitledPickList</code>
		 * 
		 * @param a the set of attributes possibly having a font family attribute this pick list could display
		 * @return true, if the set of attributes had a font family attribute, false if not
		 */
		public boolean setValue(AttributeSet a)
		{
			boolean success = false;
			ignoreTextChanges = true;
			Object newSelection;
			//      if(a.isDefined(CSS.Attribute.FONT_FAMILY)) {
			//        newSelection = a.getAttribute(CSS.Attribute.FONT_FAMILY);
			// 	setSelection(a.getAttribute(CSS.Attribute.FONT_FAMILY));
			//	success = true;
			//      }
			//a.isDefined(StyleConstants.FontFamily)
			String family = StyleConstants.getFontFamily(a);
			if (family != null)
			{

				newSelection = family;
				setSelection(family);
				success = true;
			} else
			{
				newSelection = "SansSerif";
				setSelection(newSelection);
			}
			ignoreTextChanges = false;
			if (++setValCount < 2)
			{
				originalValue = newSelection;
			}
			return success;
		}

		public AttributeSet getValue()
		{
			SimpleAttributeSet set = new SimpleAttributeSet();
			Object value = getSelection();
			//System.out.println("FamilyPicker getValue originalValue=" + originalValue);
			//System.out.println("FamilyPicker getValue value=" + value);
			if (((originalValue == null) && (value != null))
					|| ((originalValue != null) && (value != null) && (!originalValue.toString().equalsIgnoreCase(value.toString()))))
			{
				//Util.styleSheet().addCSSAttribute(set, CSS.Attribute.FONT_FAMILY, value.toString());
				StyleConstants.setFontFamily(set, value.toString());
			}
			return set;
		}

		public AttributeSet getValue(boolean includeUnchanged)
		{
			if (includeUnchanged)
			{
				SimpleAttributeSet set = new SimpleAttributeSet();
				Object value = getSelection();
				//System.out.println("FamilyPicker getValue originalValue=" + originalValue);
				//System.out.println("FamilyPicker getValue value=" + value);
				//Util.styleSheet().addCSSAttribute(set, CSS.Attribute.FONT_FAMILY, value.toString());
				StyleConstants.setFontFamily(set, value.toString());
				return set;
			} else
			{
				return getValue();
			}
		}

		public String getFamily()
		{
			return (String) getSelection();
		}

		public void reset()
		{
			setValCount = 0;
			originalValue = null;
		}
	}

	/**
	 * extend <code>TitledPickList</code> with a way to set values special to font size values
	 */
	class SizePickList extends TitledPickList implements AttributeComponent
	{

		// private Object key;
		private int setValCount = 0;
		private String originalValue;

		/**
		 * constructor
		 * 
		 * @param options the options to be selectable in this list
		 * @param titleText the title for the pick list
		 */
		SizePickList(String[] options, String titleText)
		{
			super(options, titleText);
			//this.key = key;
		}

		/**
		 * set the value of this <code>TitledPickList</code>
		 * 
		 * @param a the set of attributes possibly having a font size attribute this pick list could display
		 * @return true, if the set of attributes had a font size attribute, false if not
		 */
		public boolean setValue(AttributeSet a)
		{
			ignoreTextChanges = true;
			boolean success = false;
			//Object attr = a.getAttribute(key);
			String newSelection;
			if (a != null)
			{
				//LengthValue lv = new LengthValue(a.getAttribute(key));
				//int val = new Float(lv.getAttrValue(attr.toString(), LengthValue.pt)).intValue();
				int val = StyleConstants.getFontSize(a); //(int) Util.getAttrValue(a.getAttribute(key));
				if (val > 0)
				{
					success = true;
					newSelection = new Integer(val).toString();
					setSelection(newSelection);
				} else
				{
					newSelection = "12";
					setSelection(newSelection);
				}
			} else
			{
				newSelection = "12";
				setSelection(newSelection);
			}
			ignoreTextChanges = false;
			if (++setValCount < 2)
			{
				originalValue = newSelection;
			}
			return success;
		}

		public AttributeSet getValue()
		{
			SimpleAttributeSet set = new SimpleAttributeSet();
			String value = (String) getSelection();
			if (((originalValue == null) && (value != null)) || ((originalValue != null) && (!originalValue.equalsIgnoreCase(value))))
			{
				//       Util.styleSheet().addCSSAttribute(set, CSS.Attribute.FONT_SIZE,
				//  set.addAttribute("size", (String) getSelection() /*+ "pt"*/);
				StyleConstants.setFontSize(set, Integer.parseInt((String) getSelection()));
			}
			return set;
		}

		public AttributeSet getValue(boolean includeUnchanged)
		{
			if (includeUnchanged)
			{
				SimpleAttributeSet set = new SimpleAttributeSet();

				//Util.styleSheet().addCSSAttribute(set, CSS.Attribute.FONT_SIZE,
				//(String) getSelection() /*+ "pt"*/);
				StyleConstants.setFontSize(set, Integer.parseInt((String) getSelection()));
				return set;
			} else
			{
				return getValue();
			}
		}

		public void reset()
		{
			setValCount = 0;
			originalValue = null;
		}
	}

	private static StyleSheet styleSheet = new StyleSheet();
	/**
	 * extend <code>TitledPickList</code> with a way to set values special to font style values
	 */
	class StylePickList extends TitledPickList implements AttributeComponent
	{

		private int setValCount = 0;

		/**
		 * constructor
		 * 
		 * @param options the options to be selectable in this list
		 * @param titleText the title for the pick list
		 */
		StylePickList(String[] options, String titleText)
		{
			super(options, titleText);
		}

		/**
		 * set the value of this <code>TitledPickList</code>
		 * 
		 * @param a the set of attributes possibly having a font style attribute this pick list could display
		 * @return true, if the set of attributes had a font style attribute, false if not
		 */
		public boolean setValue(AttributeSet a)
		{
			ignoreTextChanges = true;
			boolean success = false;
			int styleNo = 0;
			String value;
			if (a.isDefined(CSS.Attribute.FONT_WEIGHT))
			{
				value = a.getAttribute(CSS.Attribute.FONT_WEIGHT).toString();
				if (value.equalsIgnoreCase(StyleConstants.Bold.toString()))
				{
					styleNo++;
				}
			}
			if (a.isDefined(CSS.Attribute.FONT_STYLE))
			{
				value = a.getAttribute(CSS.Attribute.FONT_STYLE).toString();
				if (value.equalsIgnoreCase(StyleConstants.Italic.toString()))
				{
					styleNo += 2;
				}
			}
			setSelection(styleNo);
			if (++setValCount < 2)
			{

			}
			ignoreTextChanges = false;
			return success;
		}

		public AttributeSet getValue()
		{
			String value = "Util.CSS_ATTRIBUTE_NORMAL";
			SimpleAttributeSet set = new SimpleAttributeSet();
			int styleNo = getIndex();
			switch (styleNo)
			{
				case 0:
					styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, value);
					break;
				case 1:
					styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_WEIGHT, StyleConstants.Bold.toString());
					break;
				case 2:
					styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, StyleConstants.Italic.toString());
					break;
				case 3:
					styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_WEIGHT, StyleConstants.Bold.toString());
					styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, StyleConstants.Italic.toString());
					break;
			}
			return set;
		}

		public AttributeSet getValue(boolean includeUnchanged)
		{
			if (includeUnchanged)
			{
				String value = "CSS_ATTRIBUTE_NORMAL";
				SimpleAttributeSet set = new SimpleAttributeSet();
				int styleNo = getIndex();
				switch (styleNo)
				{
					case 0:
						styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, value);
						break;
					case 1:
						styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_WEIGHT, StyleConstants.Bold.toString());
						break;
					case 2:
						styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, StyleConstants.Italic.toString());
						break;
					case 3:
						styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_WEIGHT, StyleConstants.Bold.toString());
						styleSheet.addCSSAttribute(set, CSS.Attribute.FONT_STYLE, StyleConstants.Italic.toString());
						break;
				}
				return set;
			} else
			{
				return getValue();
			}
		}

		public int getFontStyle()
		{
			int fontStyle = 0;
			switch (getIndex())
			{
				case 0:
					fontStyle = Font.PLAIN;
					break;
				case 1:
					fontStyle = Font.BOLD;
					break;
				case 2:
					fontStyle = Font.ITALIC;
					break;
				case 3:
					fontStyle = Font.BOLD | Font.ITALIC;
					break;
			}
			return fontStyle;
		}

		public void reset()
		{
			setValCount = 0;

		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
