// Created on 28-jul-2003
package nu.fw.jeti.jabber;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.elements.XDataBuilder;
import nu.fw.jeti.jabber.elements.XDataField;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class XDataPanel extends JPanel
{
	private boolean editable;
	private List fields = new LinkedList(); 
	
	public XDataPanel(XData xdata,final XDataCallback callback)
	{
		setLayout(new BorderLayout());
		if(xdata.getType().equals("form")) editable =true;
		if(xdata.getInstructions() !=null)
		{
			JTextArea area = new JTextArea(xdata.getInstructions()); 
			area.setEditable(false);
			area.setBackground(SystemColor.control); 
			add(area,BorderLayout.NORTH);
		}
		if(xdata.hasFields())
		{
			JPanel pnlFields = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(3, 5, 0, 3);
			for(Iterator i = xdata.getFields();i.hasNext();)
			{
				XDataField data = (XDataField)i.next();
				parseField(data, pnlFields, c);
			}
            c.gridwidth = 1;
            c.weighty = 1.0;
            c.weightx = 1.0;
            pnlFields.add(Box.createVerticalGlue(), c);
			add(new JScrollPane(pnlFields),BorderLayout.CENTER);
		}
		JPanel panel = new JPanel();
		JButton btnOk  = new JButton();
		I18N.setTextAndMnemonic("Send",btnOk);
		btnOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				sendForm(callback); 
			}
		});
		panel.add(btnOk);
		JButton btnCancel  = new JButton(I18N.gettext("Cancel"));
		btnCancel.setMnemonic(KeyEvent.VK_ESCAPE);
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				callback.cancelForm();  
			}
		});
		panel.add(btnCancel);
		add(panel,BorderLayout.SOUTH);
	}
	
	private void parseField(XDataField field,JPanel pnlFields, GridBagConstraints c)
	{//change to implemnt check for required fields?
		String type = field.getType();
		String value = field.getValue();
		String label = field.getLabel();
		String description = field.getDescription();
		boolean required = field.getRequired();
		final String var = field.getVar(); 
		if(type.equals("boolean"))
		{
			String label2;
			if(label != null) label2 =field.getLabel();
			else label2 = var; 
			if(required) label2 = "*" + label2; 
			JCheckBox chkBox = new JCheckBox(label2);
			if("0".equals(value))chkBox.setSelected(false);
			if("1".equals(value))chkBox.setSelected(true); 
			chkBox.setEnabled(editable);
			if(description !=null)chkBox.setToolTipText(description);
			pnlFields.add(chkBox, c);
			chkBox.setName(var);
			fields.add(chkBox);
		}
		else if (type.equals("fixed"))
		{
			JLabel lbl = new JLabel(value);
			lbl.setAlignmentX(0.0f);
			if(description !=null) lbl.setToolTipText(description);
			lbl.setAlignmentX(0.0f);
			pnlFields.add(lbl, c);
		}
		else if (type.equals("list-multi"))
		{//assmodel
			addLabel(label,var,description, required, pnlFields, c);
			JList list = new JList();
			list.setEnabled(editable);
			pnlFields.add(list, c);
			list.setAlignmentX(0.0f);
			list.setName(var);
			fields.add(list);
		}
		else if (type.equals("list-single")) 
		{//addcombo
			addLabel(label,var,description, required,pnlFields, c);
			if(field.hasOptions())
			{ 
				Object[] values = new Object[field.getOptionsSize()];
				int tel=0;
				int index = 0;
				for (Iterator i = field.getOptionsIterator(); i.hasNext();tel++)
				{
					Object[] obj = (Object[]) i.next();
					values[tel] = new LabelValue(obj);
					if(((String)obj[1]).equals(value)) index = tel;
										
				}
				JComboBox cmbBox = new JComboBox(values);
				cmbBox.setSelectedIndex(index);
				cmbBox.setEditable(editable);
				cmbBox.setAlignmentX(0.0f);
				pnlFields.add(cmbBox, c);
				cmbBox.setName(var); 	
				fields.add(cmbBox);
			}
		}
		else if (type.equals("text-multi"))
		{
			addLabel(label,var,description, required,pnlFields, c);
			final JTextArea area = new JTextArea(value, 2, 20);
            JScrollPane scrollPane = new JScrollPane(area);
			area.setEditable(editable);
			area.setAlignmentX(0.0f);
			pnlFields.add(scrollPane, c);
			area.setName(var);
			fields.add(area);
		}
		else if (type.equals("text-private"))
		{
			addLabel(label,var,description, required,pnlFields, c);
			final JPasswordField pass = new JPasswordField(value);
			pass.setEditable(editable);
			pass.setAlignmentX(0.0f);
			pnlFields.add(pass, c);
			pass.setName(var);
			fields.add(pass);
		}
		else if (type.equals("text-single"))
		{
			addLabel(label,var,description, required, pnlFields, c);
			final JTextField text = new JTextField(value);
			text.setEditable(editable);
			text.setAlignmentX(0.0f);
			pnlFields.add(text, c);
			text.setName(var); 
			fields.add(text);
		}
		else if (type.equals("jid-multi"))
		{
			addLabel(label,var,description, required, pnlFields, c);
		}
		else if (type.equals("jid-single"))
		{
			addLabel(label,var,description, required,pnlFields, c);
		}
		else if (type.equals("hidden"))
		{
			fields.add(new Object[]{var,value}); 
		}
	}
	
	private void addLabel(String label,String var,String description,
                          boolean required,JPanel panel, GridBagConstraints c)
	{
		String label2;
		if(label != null) label2 =label;
		else label2 = var;
		if(required) label2 = "*" + label2;
		JLabel lbl = new JLabel(label2);
		if(description !=null) lbl.setToolTipText(description);
		lbl.setAlignmentX(0.0f);
		panel.add(lbl, c); 
	}
	
	private void sendForm(XDataCallback callback)
	{
		XDataBuilder builder = new XDataBuilder(); 
		builder.type ="submit"; 
		for (Iterator iter = fields.iterator(); iter.hasNext();)
		{
			Object element = iter.next();
			String var = null;
			String value = null;
			if(element instanceof Object[])
			{
				var =(String)((Object[])element)[0];
				value =(String)((Object[])element)[1];
				if(value != null)
				{
					builder.addField(new XDataField(var,value));
				}
			}
			else
			{
				JComponent component = (JComponent)element;
				var = component.getName();
				if(component instanceof JCheckBox)
				{
					  value = String.valueOf(((JCheckBox)component).isSelected()?1:0);  
				}
				else if(component instanceof JTextComponent)
				{
					value = ((JTextComponent)component).getText(); 
				}
				else if(component instanceof JComboBox)
				{
					value =((LabelValue)((JComboBox)component).getSelectedItem()).value; 
				}
				if(value != null)
				{
					builder.addField(new XDataField(var,value));
				}
				if(component instanceof JList)
				{
					JList list = ((JList)component);
					if (!list.isSelectionEmpty())
					{
						Object[] values = list.getSelectedValues();
						for (int i = 0; i < values.length; i++)
						{
							builder.addField(new XDataField(var,((LabelValue)values[i]).value));
						}
					} 
		 		}
			}
		}
		try
		{
			callback.sendForm(builder.build());
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
	}
}

 class LabelValue
{
	String label;
	String value;
	
	LabelValue(Object[] obj)
	{
		value = (String)obj[1];
		if(obj[0]==null)label= value;
		else label =(String) obj[0];
	}
	
	public String toString()
	{
		return label;
	}
}




/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
