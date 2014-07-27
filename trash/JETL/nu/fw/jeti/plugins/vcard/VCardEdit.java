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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.filetransfer.Plugin;
import nu.fw.jeti.plugins.filetransfer.ibb.IBBExtension;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author E.S. de Boer
 *
 */
public class VCardEdit extends JFrame
{
	private Map personal;
	private Map business;
	private LinkedList personalFields;
	private LinkedList businessFields;
	private List homeTels;
	private List workTels;
	private Backend backend;
	private static int HOME=1;
	private static int WORK=2;
	private File photo=null;
	private File logo=null;
		
	public VCardEdit(Map personal, Map business, List homeTels, List workTels
					,Backend backend) 
	{
		this.personal = personal;
		this.business = business;
		this.homeTels = homeTels;
		this.workTels = workTels;
		this.backend = backend;
		businessFields = new LinkedList();
		personalFields = new LinkedList();
		init();
	}
	
	private void init()
	{
		setTitle(I18N.gettext("vcard.Edit_Details"));
		setIconImage(StatusIcons.getImageIcon("jeti").getImage());

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab(I18N.gettext("vcard.Personal"),createPersonalPanel());
		tabs.addTab(I18N.gettext("vcard.Business"),createBusinessPanel());
		getContentPane().setLayout(new BorderLayout());
	    getContentPane().add(tabs,BorderLayout.CENTER);
	    JPanel panel = new JPanel();
	    JButton button = new JButton();
	    I18N.setTextAndMnemonic("vcard.Submit",button);
	    button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				submitVCard();
			}
		});
	    panel.add(button);
	    button = new JButton();
	    Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		};
		button.setAction(cancelAction);
		panel.add(button);
		getContentPane().add(panel,BorderLayout.SOUTH);
		
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
		final JLabel lblPhoto =new JLabel();
		if (data!=null)
		{
			try{
				byte[] bytes = Base64.decode(data[1]);
				lblPhoto.setIcon(new ImageIcon(bytes));
			}catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		pnlPersonal.add(lblPhoto,c);
		JButton button = new JButton();
		I18N.setTextAndMnemonic("vcard.change_photo",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				photo = getImage();
				if(photo!=null){
					try{
						lblPhoto.setIcon(new ImageIcon(photo.toURL()));
					} catch (MalformedURLException e){
						e.printStackTrace();
					}
					pack();
		        }
			}
		});
		c.gridwidth = GridBagConstraints.REMAINDER;
		pnlPersonal.add(button,c);
		addLine("FN", HOME, pnlPersonal, c);
		addLine("GIVEN", HOME, pnlPersonal, c);
		addLine("MIDDLE", HOME, pnlPersonal, c);
		addLine("FAMILY", HOME, pnlPersonal, c);
		addLine("NICKNAME", HOME, pnlPersonal, c);
		// bday
		// TODO format, be aware of errors, add age
		addLine("BDAY", HOME, pnlPersonal, c);
		addAdress(HOME,pnlPersonal, c);

		// tel
		addLine("JABBERID", HOME, pnlPersonal, c);
		addLine("EMAIL", HOME, pnlPersonal, c);
		addLine("URL", HOME, pnlPersonal, c);
		//TODO add linebreaks if too long
		addLine("DESC", HOME, pnlPersonal, c);

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
		String[] data = (String[])business.get("LOGO");
		final JLabel lblPhoto =new JLabel();
		if (data!=null)
		{
			try{
				byte[] bytes = Base64.decode(data[1]);
				lblPhoto.setIcon(new ImageIcon(bytes));
			}catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(lblPhoto,c);
		JButton button = new JButton();
		I18N.setTextAndMnemonic("vcard.change_photo",button);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				logo = getImage();
				if(logo!=null){
					try{
						lblPhoto.setIcon(new ImageIcon(logo.toURL()));
					} catch (MalformedURLException e){
						e.printStackTrace();
					}
					pack();
		        }
			}
		});
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(button,c);
		
		addLine("TITLE", WORK, panel, c);
		addLine("ROLE", WORK, panel, c);
		addLine("ORGNAME", WORK, panel, c);
		addLine("ORGUNIT", WORK, panel, c);
		addLine("EMAIL", WORK, panel, c);
		addAdress(WORK,panel, c);
		
		c.gridwidth = 2;
	    c.weighty = 1.0;
	    c.weightx = 1.0;
	    panel.add(Box.createVerticalGlue(), c);
	    return panel;
	}
	
	private void addAdress(int type,JPanel panel, GridBagConstraints c)
	{
		if(type==WORK) addLine("POBOX", type, panel, c);
		addLine("STREET", type, panel, c);
		addLine("EXTADR", type, panel, c);
		addLine("LOCALITY", type, panel, c);
		addLine("REGION", type, panel, c);
		addLine("PCODE", type, panel, c);
		addLine("CTRY", type, panel, c);
	}
	
	    
	private void addLine(String name,int type,JPanel pnl,GridBagConstraints c)
	{
		Map values;
		List fields;
		if(type==HOME)
		{
			values = personal;
			fields = personalFields;
		}
		else
		{
			values=business;
			fields = businessFields;
		}
		String value = (String)values.get(name);
		c.gridwidth = 1;
		JTextField text = new JTextField(value);
		fields.add(new Object[]{name,text});
	    name = I18N.gettext("vcard."+name);
		pnl.add(new JLabel(name),c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		pnl.add(text,c);
	}
	
	
	private File getImage()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter()
		{
			public boolean accept(File file)
			{
				if(file.isDirectory())return true;
				String filename = file.getName().toLowerCase();
				if(filename.endsWith("gif") 
						|| filename.endsWith("png")
						|| filename.endsWith("jpg")
						|| filename.endsWith("jpeg")){
					return true;
				}
				return false;
			}
	
			public String getDescription()
			{
				return "Images, *.jpg,*.png,*.gif";
			}
		});
		int s = fileChooser.showOpenDialog(VCardEdit.this);
		if(s == JFileChooser.APPROVE_OPTION){
			if(fileChooser.getSelectedFile().length()>51200){
				Popups.errorPopup(
						I18N.gettext("vcard.Image_may_not_be_larger_then_50kB")
						,I18N.gettext("vcard.Image_to_large"));
			}
			else return fileChooser.getSelectedFile();
		}
		return null;
	}
	
	private void submitVCard()
	{
		Map tempPersonal = new HashMap(20);
		Map tempBusiness = new HashMap(20);
		for(Iterator i=personalFields.iterator();i.hasNext();)
		{
			Object[] temp = (Object[])i.next();
			String value =((JTextField)temp[1]).getText();
			if(!value.equals("")) tempPersonal.put(temp[0],value);
		}
		for(Iterator i=businessFields.iterator();i.hasNext();)
		{
			Object[] temp = (Object[])i.next();
			String value =((JTextField)temp[1]).getText();
			if(!value.equals("")) tempBusiness.put(temp[0],value);
		}
		if(photo!=null)
		{
			String[] p = new String[2];
			p[1] = encode(photo);
			tempPersonal.put("PHOTO",p);
		}
		else tempPersonal.put("PHOTO",personal.get("PHOTO"));
		
		if(logo!=null)
		{
			String[] p = new String[2];
			p[1] = encode(logo);
			tempBusiness.put("LOGO",p);
		}
		else tempBusiness.put("LOGO",business.get("LOGO"));
		
		backend.send(new InfoQuery("set",new VCard(tempPersonal,tempBusiness,null,null)));
		dispose();
	}
	
	private String encode(File file)
	{
       	InputStream is = null;
       	String encoded=null;
    	try {
    		is = new FileInputStream(file.getAbsolutePath());
            int len = (int)file.length();
            byte[] buf = new byte[len];
            is.read(buf);
	        encoded = Base64.encode(buf, len);
	        is.close();
    	}catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	return encoded;
	}
}
