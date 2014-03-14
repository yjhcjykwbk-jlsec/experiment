package nu.fw.jeti.ui;

import nu.fw.jeti.events.BrowseListener;
import nu.fw.jeti.events.DiscoveryListener;
import nu.fw.jeti.events.RegisterListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.jabber.elements.DiscoveryInfo;
import nu.fw.jeti.jabber.elements.IQBrowse;
import nu.fw.jeti.jabber.elements.IQRegister;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.util.I18N;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class RegisterServices extends JFrame implements DiscoveryListener, RegisterListener
{
	private JList jList1;
	private JPanel jPanel1 = new JPanel();
	private JButton btnRegister = new JButton();
	private JButton btnCancel = new JButton();
	private Map services = new HashMap();
	private Backend backend;
	private JTextField txtServer = new JTextField();
	private JButton btnChangeServer = new JButton();
	private JID server;
	private JID registerJID;
	private int numberOfServices;
	
	public RegisterServices(Backend backend)
	{
		this.backend = backend;
		server = new JID(backend.getMyJID().getDomain());
		//Collections.sort(services); //object[] not sortable
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		backend.addListener(RegisterListener.class,this);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		backend.getItems(server,this);
	}

	private void jbInit() throws Exception
	{
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
        //btnRegister.setMnemonic('R');
        //btnRegister.setText(I18N.gettext("main.manageservices.Register"));
        I18N.setTextAndMnemonic("main.manageservices.Register",btnRegister);
        btnRegister.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnRegister_actionPerformed(e);
            }
        });
        btnCancel.setAlignmentX(0.5f);
        Action cancelAction = new AbstractAction(I18N.gettext("Cancel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				btnCancel_actionPerformed(e);
			}
		};
        btnCancel.setAction(cancelAction);
                
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.getActionMap().put("cancel", cancelAction);
        layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke,"cancel");
                
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				backend.removeListener(RegisterListener.class,RegisterServices.this);
				dispose();
			}
		});
        this.setTitle(I18N.gettext("main.manageservices.Manage_Services"));
        //DefaultListModel listModel = new DefaultListModel();
		/*
		for(Iterator i = services.listIterator();i.hasNext();)
		{
			listModel.addElement((String)((Object[])i.next())[0]);
		}
		*/
		jList1 = new JList(new DefaultListModel());
		jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                jList1_valueChanged(e);
            }
        });
		jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		btnChangeServer.setAlignmentX(0.5f);
       //btnChangeServer.setMnemonic('S');
		//btnChangeServer.setText(I18N.gettext("main.manageservices.Change_Server"));
		I18N.setTextAndMnemonic("main.manageservices.Change_Server",btnChangeServer);
		btnChangeServer.addActionListener(new java.awt.event.ActionListener()
		{
		  public void actionPerformed(ActionEvent e)
		  {
			btnChangeServer_actionPerformed(e);
		  }
		});
		JLabel label = new JLabel(); 
		label.setAlignmentX(0.5f);
		//jLabel1.setText(I18N.gettext("main.manageservices.Server"));
		I18N.setTextAndMnemonic("main.manageservices.Server",label);
		label.setLabelFor(txtServer);
		this.getContentPane().add(jList1, BorderLayout.CENTER);
        this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        //jPanel1.add(btnRegister, null);
		jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));
        jPanel1.add(label);
        jPanel1.add(txtServer);
		jPanel1.add(btnChangeServer);
		jPanel1.add(btnCancel);
		pack();
	}

	void btnCancel_actionPerformed(ActionEvent e)
	{
		backend.removeListener(RegisterListener.class,this);
		this.dispose();
	}

	void btnRegister_actionPerformed(ActionEvent e)
	{
    	backend.removeListener(RegisterListener.class,this);
		this.dispose();
	}

	void jList1_valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting())
        return;

		JList theList = (JList)e.getSource();
		if (!theList.isSelectionEmpty())
		{
			registerJID = (JID)services.get(theList.getSelectedValue());
		    if(registerJID!=null) backend.send(new InfoQuery(registerJID,"get",backend.getIdentifier(),new IQRegister()));
		}
		theList.clearSelection();
	}

	void btnChangeServer_actionPerformed(ActionEvent e)
	{
    	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	JID temp = null;
    	try
		{
    		temp = JID.checkedJIDFromString(txtServer.getText());
		}
    	catch (InstantiationException ex)
        {
			nu.fw.jeti.util.Popups.errorPopup(ex.getMessage(), I18N.gettext("main.error.invalid_server"));
        }
		if(temp == null) return;
		server = temp;
		backend.getItems(server,this);
	}
    
	public void discoveryItemResult(JID jid,DiscoveryItem item)
	{
		//System.out.println(browse);
		if (!item.hasItems()) nu.fw.jeti.util.Popups.errorPopup(I18N.gettext("main.error.No_services"), I18N.gettext("main.error.Register_Services"));
		else
		{
			numberOfServices=0;
			DefaultListModel model = (DefaultListModel) jList1.getModel();
			services.clear();
			model.clear();
			for(Iterator i = item.getItems();i.hasNext();)
			{
				DiscoveryItem di = (DiscoveryItem)i.next();
				String name = di.getName();
				if(name==null) name= di.getJID().toStringNoResource();
				//put in services so errors do not prevent register
				services.put(name,di.getJID());
				model.addElement(name);
				backend.getInfo(di.getJID(),this);
				numberOfServices++;
			}
		}
		pack();
		setLocationRelativeTo(null);
	}
	  
	public void discoveryInfoResult(JID jid, DiscoveryInfo item)
	{
		//DefaultListModel model = (DefaultListModel) jList1.getModel();
		numberOfServices--;
		System.out.println(numberOfServices);
		if (numberOfServices <= 0) setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		DefaultListModel model = (DefaultListModel) jList1.getModel();
		String name = item.getName();
		if(name!=null && model.removeElement(jid.toStringNoResource()))
		{
			model.addElement(name);
		}
		if (item.hasFeatures())
		{
			for (Iterator j = item.getFeatures(); j.hasNext();)
			{
				String namespace = (String) j.next();
				if (namespace.equals("jabber:iq:register"))
				{
					if(name!=null) services.put(item.getName(),jid);
					else services.put(jid.toStringNoResource(),jid);
				}
				//else{} remove from services
			}
		}
	}
	  
	  /*
	  public void browseResult(IQBrowse browse)
	  {
		  //System.out.println(browse);
		  if(browse.getJID() == null) nu.fw.jeti.util.Popups.errorPopup(I18N.gettext("main.error.Server_doesn't_support_browse"),I18N.gettext("main.error.Service_error"));
		  else if(browse.getJID().equals(server))
		  {//current server browse
				services.clear();
				DefaultListModel model =(DefaultListModel)jList1.getModel();
				model.clear();
				if(!browse.hasChildItems())
				{
					nu.fw.jeti.util.Popups.errorPopup(I18N.gettext("main.error.No_services"),I18N.gettext("main.error.Register_Services"));
					backend.removeListener(RegisterListener.class,this);
					this.dispose();
					return;
				}
				else {
					for(Iterator i = browse.getItems();i.hasNext();)
					{
						IQBrowse item = (IQBrowse)i.next();
						if(item.hasFeatures())
						{
							for(Iterator j = item.getFeatures();j.hasNext();)
							{
								String namespace = (String)j.next();
								if(namespace.equals("jabber:iq:register"))
								{//object 0 could be removed
									Object temp[] = new Object[2];
									temp[0] = item.getName();
									temp[1] = item.getJID();
									services.add(temp);
									model.addElement(item.getName());
								}
							}
						}
					}
				}
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			pack();
			setLocationRelativeTo(null);
	  }
	  */

	public void register(IQRegister register,String id)
	{

		new RegisterWindow(backend,register,registerJID,id);
		  /*
		  Map map = new HashMap(20);
		  String key = null;
		  //for(Iterator i = register.getFields()
			for(	  Enumeration keys = register.getNames();keys.hasMoreElements();)
		  {
			  key = (String)keys.nextElement();
			  map.put(key ,register.getValue(key));
			  //System.out.println(key +   "   " + register.getValue(key));
		  }
		  RegisterWindow registerWindow = new RegisterWindow(this,map,newAccount,cp.getFromAddress());
		  registerWindow.show();
		  */
	}	
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
