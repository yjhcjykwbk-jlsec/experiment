package nu.fw.jeti.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.util.I18N;
//import org.jabber.jabberbeans.util.JID;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class CommentWindow extends JFrame
{
    private JPanel jPanel1 = new JPanel();
    private JRadioButton radioComment = new JRadioButton();
    private JRadioButton radioBug = new JRadioButton();
    private JPanel jPanel2 = new JPanel();
	private Backend backend;
	//JPanel jPanel4 = new JPanel();
	//JCheckBox checkReply = new JCheckBox();
	//JCheckBox checkAnonymous = new JCheckBox();
	JPanel jPanel3 = new JPanel();
	JButton btnSend = new JButton();
	JButton btnCancel = new JButton();
	JPanel jPanel5 = new JPanel();
	private JCheckBox[] aryCheckBox = new JCheckBox[5];
	JLabel jLabel1 = new JLabel();
    //private JLabel jLabel2 = new JLabel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextArea jTextArea1 = new JTextArea();

    public CommentWindow(Backend backend)
    {
		this.backend = backend;
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		//pack();
		setSize(300,450);
		setLocationRelativeTo(null);
    }
    private void jbInit() throws Exception
    {
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle(I18N.gettext("main.comment.Comment/Bug"));
		ButtonGroup radio = new ButtonGroup();
        radioComment.setSelected(true);
        //radioComment.setText(I18N.gettext("main.comment.Comment/Request"));
        I18N.setTextAndMnemonic("main.comment.Comment/Request",radioComment);
        radioComment.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                radioComment_actionPerformed(e);
            }
        });
        //radioBug.setText(I18N.gettext("main.comment.Bug"));
        I18N.setTextAndMnemonic("main.comment.Bug",radioBug);
        radioBug.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                radioBug_actionPerformed(e);
            }
        });
		//jPanel4.setLayout(new VerticalLayout());
		//jPanel4.setLayout(new BoxLayout(jPanel4,BoxLayout.Y_AXIS));
		//jPanel4.setOpaque(false); 
		//checkReply.setAlignmentX((float) 0.5);
        //checkAnonymous.setVisible(false);
        //checkAnonymous.setAlignmentX((float) 0.5);
		//btnSend.setMnemonic('S'); // TODO: mnemonic here
		btnSend.setText(I18N.gettext("Send"));
		getRootPane().setDefaultButton(btnSend);
		btnSend.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnSend_actionPerformed(e);
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
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
				
		jLabel1.setText(I18N.gettext("main.comment.Include"));
		jPanel5.add(jLabel1, null);

		JCheckBox tempBox = new JCheckBox();
		tempBox.setName("0");
		tempBox.setText(I18N.gettext("main.comment.JETI_Version:")  + " " + nu.fw.jeti.backend.Start.VERSION);
		I18N.setMnemonic("main.comment.JETI_Version:",tempBox);
		tempBox.setSelected(true);
		aryCheckBox[0] = tempBox;
		jPanel5.add(tempBox);
		tempBox = new JCheckBox();
		tempBox.setName("1");
		tempBox.setText(I18N.gettext("main.comment.OS:") + " " + System.getProperty("os.name") +" "+ System.getProperty("os.version"));
		I18N.setMnemonic("main.comment.OS:",tempBox);
		aryCheckBox[1] = tempBox;
		tempBox.setSelected(true);
		jPanel5.add(tempBox);
		tempBox = new JCheckBox();
		tempBox.setName("2");
		tempBox.setText(I18N.gettext("main.comment.Architecture:") + " " + System.getProperty("os.arch"));
		I18N.setMnemonic("main.comment.Architecture:",tempBox);
		aryCheckBox[2] = tempBox;
		tempBox.setSelected(true);
		jPanel5.add(tempBox);
		tempBox = new JCheckBox();
		tempBox.setName("3");
		tempBox.setText(I18N.gettext("main.comment.Java_Vendor:") + " " + System.getProperty("java.vendor"));
		I18N.setMnemonic("main.comment.Java_Vendor:",tempBox);
		aryCheckBox[3] = tempBox;
		tempBox.setSelected(true);
		jPanel5.add(tempBox);
		tempBox = new JCheckBox();
		tempBox.setName("4");
		tempBox.setText(I18N.gettext("main.comment.Java_Version:")  + " " +  System.getProperty("java.version"));
		I18N.setMnemonic("main.comment.Java_Version:",tempBox);
		aryCheckBox[4] = tempBox;
		tempBox.setSelected(true);
		jPanel5.add(tempBox);


		jPanel5.setAlignmentX((float) 0.5);
		jPanel5.setVisible(false);
		jPanel2.add(jPanel5, null);
		//jPanel2.add(jPanel4, null);
		//jPanel4.add(jLabel2, null);
		//jPanel4.add(checkReply, null);
		//jPanel4.add(checkAnonymous, null);
		jPanel5.setLayout(new BoxLayout(jPanel5,BoxLayout.Y_AXIS));

		jPanel2.add(jPanel3, null);
		jPanel3.add(btnSend, null);
		jPanel3.add(btnCancel, null);
        this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(jTextArea1, null);
        this.getContentPane().add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(radioComment, null);
        jPanel1.add(radioBug, null);
        this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
		jPanel2.setLayout(new BoxLayout(jPanel2,BoxLayout.Y_AXIS));
        radio.add(radioComment);
        radio.add(radioBug);
        
    }

    void btnSend_actionPerformed(ActionEvent e)
    {
		String message = jTextArea1.getText();
		if (message == null || message.equals("")) return;
		if(radioBug.isSelected())
		{
			for(int i =0;i<aryCheckBox.length;i++)
			{
				if (aryCheckBox[i].isSelected()) message += "\n" + aryCheckBox[i].getText();
			}
		}
		//if(checkReply.isSelected()) message += "\n" + checkReply.getText();

		backend.send(new Message(message,"Jeti Comment",new JID("jeti","jabber.org",null)));
		//backend.sendMessage(message,new JID("j2m","jabber.org",null));
		this.dispose();
    }

    void btnCancel_actionPerformed(ActionEvent e)
    {
		this.dispose();
    }

    void radioComment_actionPerformed(ActionEvent e)
    {
		jPanel5.setVisible(false);
		//pack();
    }

    void radioBug_actionPerformed(ActionEvent e)
    {
		jPanel5.setVisible(true);
		//pack();
    }


}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
