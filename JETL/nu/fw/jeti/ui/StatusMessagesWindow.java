package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class StatusMessagesWindow extends JFrame
{
	//status messages editor
//	public final static int CHAT=0;
//	public final static int AVAILABLE=1;
//	public final static int DND=2;
//	public final static int AWAY=3;
//	public final static int XA=4;
    private JPanel jPanel1 = new JPanel();
    private JToggleButton btnChat = new JToggleButton();
    private JToggleButton btnAvailable = new JToggleButton();
    private JToggleButton btnDnD = new JToggleButton();
    private JToggleButton btnAway = new JToggleButton();
    private JToggleButton btnXA = new JToggleButton();
    private JPanel jPanel2 = new JPanel();
    private JButton btnOK = new JButton();
    private JButton btnCancel = new JButton();
    private JPanel jPanel3 = new JPanel();
    private JPanel jPanel4 = new JPanel();
    private JButton btnAdd = new JButton();
    private JButton btnRemove = new JButton();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JPanel[] messagePanels = new JPanel[5];
    //private JButton btnUp = new JButton();
	private JButton btnUp = new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.SOUTH);
	private JButton btnDown = new javax.swing.plaf.basic.BasicArrowButton(SwingConstants.NORTH);
    //private JButton btnDown = new JButton();
	private int selectedStatus = Presence.AVAILABLE;
	private JTextField selectedTextField;

    public StatusMessagesWindow()
    {
		for(int tel=0;tel<5;tel++)
		{
			JPanel tempPanel = new JPanel();
		    //tempPanel.setLayout(new VerticalLayout(0,VerticalLayout.BOTH));
			tempPanel.setLayout(new BoxLayout(tempPanel,BoxLayout.Y_AXIS));

			List tempList = nu.fw.jeti.util.Preferences.getStatusMessages(tel+1);
		    for(int i=0;i<tempList.size();i++)
			{
				JTextField tempTextField =new JTextField((String)tempList.get(i));
				tempTextField.addFocusListener(new java.awt.event.FocusAdapter()
				{
					public void focusGained(FocusEvent e)
					{
						selectedTextField =(JTextField)e.getSource();
					}
				});
				tempPanel.add(tempTextField);
			}
			messagePanels[tel] = tempPanel;
		}

        try
        {
            jbInit();
			pack();
			setLocationRelativeTo(null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

//	private String getStatus(int status)
//	{
//		switch (status)
//		{
//		    case 0: return "chat";
//			case 1: return "available";
//			case 3: return "dnd";
//			case 2: return "away";
//			default: return "xa";
//		}
//
//	}

    private void jbInit() throws Exception
    {
		setTitle(I18N.gettext("main.statusmessages.Manage_Status_Messages"));
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
        Dimension dim = new Dimension(Integer.MAX_VALUE, 27);
	    btnChat.setMaximumSize(dim);
        //btnChat.setPreferredSize(new Dimension(121, 27));
        btnChat.setText(I18N.gettext("main.presence.Free_for_Chat"));
        btnChat.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                messageButtons_actionPerformed(e);
            }
        });
        btnAvailable.setMaximumSize(new Dimension(dim));
        //btnAvailable.setPreferredSize(new Dimension(121, 27));
        btnAvailable.setSelected(true);
        btnAvailable.setText(I18N.gettext("main.presence.Available"));
        btnAvailable.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                messageButtons_actionPerformed(e);
            }
        });
        btnDnD.setMaximumSize(dim);
        //btnDnD.setPreferredSize(new Dimension(121, 27));
        btnDnD.setText(I18N.gettext("main.presence.Do_not_Disturb"));
        btnDnD.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                messageButtons_actionPerformed(e);
            }
        });
        btnAway.setMaximumSize(dim);
        //btnAway.setPreferredSize(new Dimension(121, 27));
        btnAway.setText(I18N.gettext("main.presence.Away"));
        btnAway.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                messageButtons_actionPerformed(e);
            }
        });
        btnXA.setText(I18N.gettext("main.presence.Extended_Away"));
        btnXA.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                messageButtons_actionPerformed(e);
            }
        });
		//jPanel1.setLayout(new VerticalLayout());
		jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));
        //btnOK.setMnemonic('O');
        btnOK.setText(I18N.gettext("OK"));
        getRootPane().setDefaultButton(btnOK); 
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
             
        jPanel3.setLayout(new BorderLayout());
        //btnAdd.setMnemonic('A'); 
        //btnAdd.setText(I18N.gettext("main.statusmessages.Add"));
        I18N.setTextAndMnemonic("main.statusmessages.Add",btnAdd);
        btnAdd.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnAdd_actionPerformed(e);
            }
        });
        //btnRemove.setMnemonic('R');
        //btnRemove.setText(I18N.gettext("main.statusmessages.Remove"));
        I18N.setTextAndMnemonic("main.statusmessages.Remove",btnRemove);
        btnRemove.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnRemove_actionPerformed(e);
            }
        });

        //btnUp.setIcon(StatusIcons.getImageIcon("arrowUp"));
        btnUp.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnUp_actionPerformed(e);
            }
        });
        //btnDown.setIcon(StatusIcons.getImageIcon("arrowDown"));
        btnDown.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnDown_actionPerformed(e);
            }
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.getContentPane().add(jPanel1, BorderLayout.WEST);
        jPanel1.add(btnChat, null);
        jPanel1.add(btnAvailable, null);
        jPanel1.add(btnDnD, null);
        jPanel1.add(btnAway, null);
        jPanel1.add(btnXA, null);
        this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
        jPanel2.add(btnOK, null);
        jPanel2.add(btnCancel, null);
        this.getContentPane().add(jPanel3, BorderLayout.CENTER);
        jPanel3.add(jPanel4,  BorderLayout.SOUTH);
        jPanel4.add(btnAdd, null);
        jPanel4.add(btnRemove, null);
        jPanel4.add(btnUp, null);
        jPanel4.add(btnDown, null);
        jPanel3.add(jScrollPane1, BorderLayout.CENTER);

        jScrollPane1.getViewport().add(messagePanels[selectedStatus-1]);

    }

    void messageButtons_actionPerformed(ActionEvent e)
    {
		btnChat.setSelected(false);
		btnAvailable.setSelected(false);
		btnDnD.setSelected(false);
		btnAway.setSelected(false);
		btnXA.setSelected(false);
		JToggleButton toggleButton = (JToggleButton)e.getSource();
		toggleButton.setSelected(true);
		String buttonText = toggleButton.getText();
		if(buttonText.equals(I18N.gettext("main.presence.Free_for_Chat"))) selectedStatus=Presence.FREE_FOR_CHAT;
		else if(buttonText.equals(I18N.gettext("main.presence.Available"))) selectedStatus=Presence.AVAILABLE;
		else if(buttonText.equals(I18N.gettext("main.presence.Do_not_Disturb"))) selectedStatus=Presence.DND;
		else if(buttonText.equals(I18N.gettext("main.presence.Away"))) selectedStatus=Presence.AWAY;
		else selectedStatus=Presence.XA;
		jScrollPane1.getViewport().add(messagePanels[selectedStatus-1]);
		jScrollPane1.validate();

    }

    void btnAdd_actionPerformed(ActionEvent e)
    {
		JTextField tempTextField =new JTextField();
			tempTextField.addFocusListener(new java.awt.event.FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
					selectedTextField =(JTextField)e.getSource();
				}
			});
		messagePanels[selectedStatus-1].add(tempTextField);
		messagePanels[selectedStatus-1].validate();
		jScrollPane1.validate();
    }

    void btnRemove_actionPerformed(ActionEvent e)
    {
		//if(messagePanels[selectedStatus].co
		if(selectedTextField !=null)
		{
			messagePanels[selectedStatus-1].remove(selectedTextField);
		    messagePanels[selectedStatus-1].validate();
		    messagePanels[selectedStatus-1].updateUI() ;
		}
    }

    void btnUp_actionPerformed(ActionEvent e)
    {
		JPanel  tempPanel =messagePanels[selectedStatus-1];
		java.awt.Component[] components =  tempPanel.getComponents();
		for(int tel=0;tel<components.length -1;tel++)
		{
		    if(components[tel].equals(selectedTextField))
			{
				JTextField tempTextField = (JTextField)components[tel+1];
				String message = selectedTextField.getText();
				selectedTextField.setText(tempTextField.getText());
				tempTextField.setText(message);
				selectedTextField = tempTextField;
				tempPanel.validate();
				return;
			}
		}
	}

    void btnDown_actionPerformed(ActionEvent e)
    {
		JPanel  tempPanel =messagePanels[selectedStatus-1];
		java.awt.Component[] components =  tempPanel.getComponents();
		for(int tel=1;tel<components.length;tel++)
		{
		    if(components[tel].equals(selectedTextField))
			{
				JTextField tempTextField = (JTextField)components[tel-1];
				String message = selectedTextField.getText();
				selectedTextField.setText(tempTextField.getText());
				tempTextField.setText(message);
				selectedTextField = tempTextField;
				tempPanel.validate();
				return;
			}
		}
    }

    void btnCancel_actionPerformed(ActionEvent e)
    {
		this.dispose();
    }

    void btnOK_actionPerformed(ActionEvent e)
    {
		for(int tel=0;tel<5;tel++)
		{
			java.awt.Component[] components = messagePanels[tel].getComponents();
			List tempList = new ArrayList(components.length);
			//for(int i=components.length-1;i>=0;i--)
			for(int i=0;i<components.length;i++)
			{
				tempList.add(((JTextField)components[i]).getText());
			}
			//main.savePreference(getStatus(tel),tempList);
			Preferences.saveStatusMessages(tel+1,tempList);
		}
		Preferences.saveToServer();  
		StatusButton.reloadMessages();
		this.dispose();
    }

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
