/*
 * Created on 28-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nu.fw.jeti.plugins.vcard;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class VCardDisplay extends JFrame
{
	private Map personal;
	private Map business;
	private List homeTels;
	private List workTels;
	private String name;
		
	public VCardDisplay(Map personal, Map business, List homeTels, List workTels,String name)
	{
		this.personal = personal;
		this.business = business;
		this.homeTels = homeTels;
		this.workTels = workTels;
		this.name = name;
		init();
	}
	
	
	public void init()
	{
		String text = I18N.gettext("vcard.Details_for_{0}");
		setTitle(MessageFormat.format(text, new String[]{name}));
		setIconImage(StatusIcons.getImageIcon("jeti").getImage());
	
		if(business.size()>0)
		{
			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab(I18N.gettext("vcard.Personal"),createPersonalPanel());
			tabs.addTab(I18N.gettext("vcard.Business"),createBusinessPanel());
			getContentPane().setLayout(new BorderLayout());
		    getContentPane().add(tabs);
		}
	  	else
	  	{
		    getContentPane().setLayout(new BorderLayout());
		    getContentPane().add(createPersonalPanel());
	  	}
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    pack();
	    setLocationRelativeTo(null);
	    setVisible(true);
	}
	
	private JPanel createPersonalPanel()
	{
		JPanel pnlPersonal = new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 5, 0, 3);
		// photo
		String[] data = (String[])personal.get("PHOTO");
		if (data!=null){
			try{
				byte[] bytes = Base64.decode(data[1]);
				c.gridwidth = GridBagConstraints.REMAINDER;
				pnlPersonal.add(new JLabel(new ImageIcon(bytes)),c);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		addLine("FN", personal, pnlPersonal, c);
		addLine("GIVEN", personal, pnlPersonal, c);
		addLine("MIDDLE", personal, pnlPersonal, c);
		addLine("FAMILY", personal, pnlPersonal, c);
		addLine("NICKNAME", personal, pnlPersonal, c);
		// bday
		// TODO format, be aware of errors, add age
		addLine("BDAY", personal, pnlPersonal, c);
		addAdress(personal,pnlPersonal, c);

		// tel
		addLine("JABBERID", personal, pnlPersonal, c);
		addLine("EMAIL", personal, pnlPersonal, c);
		addLine("URL", personal, pnlPersonal, c);
		//TODO add linebreaks if too long
		addLine("DESC", personal, pnlPersonal, c);

		c.gridwidth = 2;
	    c.weighty = 1.0;
	    c.weightx = 1.0;
	    pnlPersonal.add(Box.createVerticalGlue(), c);
	    return pnlPersonal;
	}
	
	private JPanel createBusinessPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 5, 0, 3);
		// logo
		String data[] = (String[])business.get("LOGO");
		if (data!=null)	{
			try{
				byte[] bytes = Base64.decode(data[1]);
				c.gridwidth = GridBagConstraints.REMAINDER;
				panel.add(new JLabel(new ImageIcon(bytes)),c);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		addLine("TITLE", business, panel, c);
		addLine("ROLE", business, panel, c);
		addLine("ORGNAME", business, panel, c);
		addLine("ORGUNIT", business, panel, c);
		addLine("EMAIL", business, panel, c);
		addAdress(business,panel, c);
		
		
		c.gridwidth = 2;
	    c.weighty = 1.0;
	    c.weightx = 1.0;
	    panel.add(Box.createVerticalGlue(), c);
	    return panel;
	}
	
	private void addAdress(Map values,JPanel panel, GridBagConstraints c)
	{
		// adress
		addLine("POBOX", values, panel, c);
		addLine("STREET", values, panel, c);
		addLine("EXTADR", values, panel, c);
		addLine("LOCALITY", values, panel, c);
		addLine("REGION", values, panel, c);
		addLine("PCODE", values, panel, c);
		addLine("CTRY", values, panel, c);
	}
	
	    
	private void addLine(String name,Map values,JPanel pnl,GridBagConstraints c)
	{
		String value = (String)values.get(name);
		if(value!=null)
		{
		    c.gridwidth = 1;
		    name = I18N.gettext("vcard."+name);
			pnl.add(new JLabel(name),c);
			c.gridwidth = GridBagConstraints.REMAINDER;
			JTextField t = new JTextField(value);
			t.setEditable(false);
			pnl.add(t,c);
		}
	}
	
	
}
