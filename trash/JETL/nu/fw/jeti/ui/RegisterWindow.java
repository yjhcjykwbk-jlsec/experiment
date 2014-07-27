package nu.fw.jeti.ui;

import nu.fw.jeti.backend.NewAccount;
import nu.fw.jeti.jabber.*;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.IQRegister;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.util.I18N;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

//TODO add xdata to register constructor, update languages
/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class RegisterWindow extends JFrame implements XDataCallback 
{
    private JPanel jPanel1 = new JPanel();
    private JPanel jPanel2 = new JPanel();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
	private JPasswordField txtPassword;
	private JPanel pnlPassword = new JPanel();
    private JLabel jLabel3 = new JLabel();
	private JPanel pnlUsername = new JPanel();
    private JLabel jLabel2 = new JLabel();
	private JTextField txtUsername;
	private JPanel pnlInstructions = new JPanel();
    private JTextArea txtInstructions = new JTextArea();
    private JLabel jLabel1 = new JLabel();
	private String key;
	private Map map;
	private Backend backend;
	private NewAccount backend2;
	private boolean newAccount;
	private JID from;
	private JCheckBox checkBox = new JCheckBox(I18N.gettext("Remove"));
	private String id;
	public static final int STRUT_SIZE = 5;


	public RegisterWindow(NewAccount backend,IQRegister register,String username,String password)
    {
    	newAccount = true;
		Map forms = register.getFields();
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.register.Register"));
		//I18N.setTextAndMnemonic("Remove",checkBox);
		//checkBox.setText("Remove");
		map = new LinkedHashMap(15);
		this.backend2 = backend;
		key =(String) forms.remove("key");
        try
        {
            if(forms.containsKey("instructions"))
			{
				String instructions = (String)forms.remove("instructions");
			    txtInstructions.setText(instructions);
				txtInstructions.setEnabled(false);
			    jLabel1.setText(I18N.gettext("main.register.Instructions"));
				jLabel1.setPreferredSize(new Dimension(80, 17));
				pnlInstructions.setLayout(new BorderLayout());
				pnlInstructions.add(jLabel1, BorderLayout.WEST  );
				pnlInstructions.add(txtInstructions, BorderLayout.CENTER);
				/*
				java.awt.FontMetrics font = getFontMetrics((Font)UIManager.get("TextArea.font"));
				int naamLengte = (font.stringWidth(message));
				if(naamLengte > 400)
				{//message is to long enable autowrap
					font.getHeight();
					txtInstructions.setPreferredSize(new Dimension(400    ))
					txtInstructions.setLineWrap(true);
					return;
				}
				*/

				jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
				jPanel2.add(pnlInstructions);

			}
			if(forms.containsKey("password"))
			{
				//txtPassword = new JPasswordField((String)forms.remove("password"));
				forms.remove("password");
				txtPassword = new JPasswordField(password);
			    //txtPassword.setText();
			}
			if(forms.containsKey("username"))
			{
				//txtUsername = new JTextField((String)forms.remove("username"));
				forms.remove("username");
				txtUsername = new JTextField(username);
			}
			jbInit();
			if(forms.containsKey("registered"))
			{
				forms.remove("registered");
				checkBox.setEnabled(true);
			}
			for (Iterator i=forms.entrySet().iterator(); i.hasNext(); ) {
			    Map.Entry e = (Map.Entry) i.next();
			    //System.out.println(e.getKey() + ": " + e.getValue());

				JTextField text;
				//if(((String)e.getKey()).equals("password")) text = new JPasswordField();
			    text = new JTextField();

				//text.setPreferredSize(new Dimension(200, 21));
				text.setText((String)e.getValue());

				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				map.put(e.getKey(),text);
				JLabel label = new JLabel((String)e.getKey());
				label.setPreferredSize(new Dimension(80, 17));
				panel.add(label, BorderLayout.WEST);
				panel.add(text,BorderLayout.CENTER);
				jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
				jPanel2.add(panel);
		    }
			jPanel2.add(checkBox);
	    }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		pack();
		setLocationRelativeTo(null);
	}

	public RegisterWindow(Backend backend,IQRegister register,JID jid,String id)
	{
		from = jid;
		this.id = id;
		this.backend = backend;
		XData xData =register.getXData(); 
		if(xData !=null)
		{
			setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
			if(xData.getTitle() !=null) setTitle(xData.getTitle());
			else setTitle(I18N.gettext("main.register.Register"));
			setContentPane(new XDataPanel(xData,this));
			this.addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					cancelForm();
				}
			});
			pack();
			setLocationRelativeTo(null);
			show();   
		}
		else if(register.getFields()!=null)
		{
			oldRegister(register);
		}
	}
	
	private void oldRegister(IQRegister register)
	{
		newAccount = false;
		Map forms = register.getFields();
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.register.Register"));
		map = new LinkedHashMap(15);

		key =(String) forms.remove("key");

		try
		{
			if(forms.containsKey("instructions"))
			{
				String instructions = (String)forms.remove("instructions");
				txtInstructions.setText(instructions);
				txtInstructions.setEnabled(false);
				jLabel1.setText(I18N.gettext("main.register.Instructions"));
				jLabel1.setPreferredSize(new Dimension(80, 17));
				pnlInstructions.setLayout(new BorderLayout());
				pnlInstructions.add(jLabel1, BorderLayout.WEST  );
				pnlInstructions.add(txtInstructions, BorderLayout.CENTER);
				/*
				java.awt.FontMetrics font = getFontMetrics((Font)UIManager.get("TextArea.font"));
				int naamLengte = (font.stringWidth(message));
				if(naamLengte > 400)
				{//message is to long enable autowrap
					font.getHeight();
					txtInstructions.setPreferredSize(new Dimension(400    ))
					txtInstructions.setLineWrap(true);
					return;
				}
				*/

				jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
				jPanel2.add(pnlInstructions);

			}
			if(forms.containsKey("password"))
			{
				txtPassword = new JPasswordField((String)forms.remove("password"));
				//txtPassword.setText();
			}
			if(forms.containsKey("username"))
			{
				txtUsername = new JTextField((String)forms.remove("username"));
			}
			jbInit();
			if(forms.containsKey("registered"))
			{
				forms.remove("registered");
				checkBox.setEnabled(true);
			}
			for (Iterator i=forms.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry) i.next();
			//	System.out.println(e.getKey() + ": " + e.getValue());

				JTextField text;
				//if(((String)e.getKey()).equals("password")) text = new JPasswordField();
				text = new JTextField();

				//text.setPreferredSize(new Dimension(200, 21));
				text.setText((String)e.getValue());

				JPanel panel = new JPanel();
				panel.setLayout(new BorderLayout());
				map.put(e.getKey(),text);
				JLabel label = new JLabel((String)e.getKey());
				label.setPreferredSize(new Dimension(80, 17));
				panel.add(label, BorderLayout.WEST);
				panel.add(text,BorderLayout.CENTER);
				jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
				jPanel2.add(panel);
			}
			jPanel2.add(checkBox);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		pack();
		setLocationRelativeTo(null);
		show();
	}


    private void jbInit() throws Exception
    {
		getRootPane().setDefaultButton(btnOK);
		I18N.setTextAndMnemonic("OK",btnOK);
        btnOK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnOK_actionPerformed(e);
            }
        });
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
        
        //txtInstructions.setPreferredSize(new Dimension(200, 17));
		checkBox.setEnabled(false);

        //txtUsername.setPreferredSize(new Dimension(200, 21));
        //txtPassword.setPreferredSize(new Dimension(200, 21));

		if(txtUsername!=null)
		{
			jLabel2.setPreferredSize(new Dimension(80, 17));
			jLabel2.setText(I18N.gettext("main.register.Username"));
			pnlUsername.setLayout(new BorderLayout());
			pnlUsername.add(jLabel2, BorderLayout.WEST);
			pnlUsername.add(txtUsername, BorderLayout.CENTER);
			jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
			jPanel2.add(pnlUsername);
		}
		if(txtPassword!=null)
		{
			jLabel3.setPreferredSize(new Dimension(80, 17));
			jLabel3.setText(I18N.gettext("main.register.Password"));
			pnlPassword.setLayout(new BorderLayout());
			pnlPassword.add(jLabel3, BorderLayout.WEST);
			pnlPassword.add(txtPassword, BorderLayout.CENTER);
			jPanel2.add(Box.createVerticalStrut(STRUT_SIZE));
			jPanel2.add(pnlPassword);
		}

        //jPanel2.setLayout(new VerticalLayout(5,VerticalLayout.BOTH));
		jPanel2.setLayout(new BoxLayout(jPanel2,BoxLayout.Y_AXIS));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.getContentPane().add(jPanel2, BorderLayout.CENTER);
		jPanel1.add(btnOK, null);
        jPanel1.add(btnCancel, null);
        this.getContentPane().add(jPanel1,  BorderLayout.SOUTH);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    void btnCancel_actionPerformed(ActionEvent e)
    {
		this.dispose();
    }

    void btnOK_actionPerformed(ActionEvent e)
    {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			btnOK.setEnabled(false);
			btnCancel.setEnabled(false);

			LinkedHashMap tmap = new LinkedHashMap(16);
			if(txtUsername!=null)
			{
				tmap.put("username",txtUsername.getText().trim());
			}
			if(txtPassword!=null)
			{
				tmap.put("password",txtPassword.getText());
			}
			for (Iterator i=map.entrySet().iterator(); i.hasNext(); )
			{
			    Map.Entry entry = (Map.Entry) i.next();
				String value = ((javax.swing.JTextField)entry.getValue()).getText().trim();
				if(value .equals("")) continue;
			    tmap.put(entry.getKey(),value);
			}
			tmap.put("key",key);
		if(newAccount)
		{
			backend2.sendRegister(new IQRegister(false,tmap),this);
		}
		else
		{
			//if(checkBox.isSelected())  backend.sendRemove(from,key);
			//else backend.sendRegister(map,from,key);
			if(checkBox.isSelected())  backend.send(new InfoQuery(from,"set",new IQRegister(true,null)));
			else backend.send(new InfoQuery(from,"set",new IQRegister(false,tmap)));
			this.dispose();
		}
	}
	
	public void sendForm(XData xdata)
	{
		backend.send(new InfoQuery(from,"set",id,new IQRegister(xdata)));
		this.dispose(); 		
	}
	
	public void cancelForm()
	{
		backend.send(new InfoQuery(from,"set",id,new IQRegister(new XData("cancel"))));
		this.dispose(); 
	}

	public void login(nu.fw.jeti.backend.Connect jb,String server)
	{
		//setVisible(false);
		nu.fw.jeti.util.Popups.messagePopup(I18N.gettext("main.register.registration_succeded"),I18N.gettext("main.register.Register"));
//	    jb.connect(txtUsername.getText().trim(),txtPassword.getText(),server,"J²M");
		this.dispose();
	}

	public void error(String errorCode)
	{
		setVisible(true);
		setCursor(Cursor.getDefaultCursor());
		nu.fw.jeti.util.Popups.errorPopup(MessageFormat.format(I18N.gettext("main.error.registration_aborted,_code_{0}"), new Object[] { errorCode }),I18N.gettext("main.register.Register"));
		btnOK.setEnabled(true);
		btnCancel.setEnabled(true);
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
