// Created on 14-sep-2003
package nu.fw.jeti.plugins.cam;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.util.I18N;

/**
 * @author E.S. de Boer
 *
 */
public class CamSendWindow extends JFrame
{
  private Box panel1 = Box.createVerticalBox();
  private JLabel jLabel1 = new JLabel();
  private JTextField txtDescription = new JTextField();
  private JLabel jLabel2 = new JLabel();
  private JTextField txtURL = new JTextField();
  private JLabel jLabel3 = new JLabel();
  private JTextField txtRefresh = new JTextField();
  private JPanel jPanel1 = new JPanel();
  private JButton btnCancel = new JButton();
  private JButton btnOK = new JButton();
  private Backend backend;
  private JIDStatus jidStatus;

  public CamSendWindow(JIDStatus jidStatus,Backend backend)
  {
	this.backend =backend;
	this.jidStatus = jidStatus; 
	try
	{
	  jbInit();
	  pack();
	}
	catch(Exception ex)
	{
	  ex.printStackTrace();
	}
  }

  void jbInit() throws Exception
  {
  	setTitle(I18N.gettext("cam.Send_Webcam"));
  	setIconImage(StatusIcons.getImageIcon("jeti").getImage());
	getRootPane().setDefaultButton(btnOK);
	jPanel1.setAlignmentX(0.0f);
	this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    panel1.add(Box.createHorizontalGlue());
    jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
    I18N.setTextAndMnemonic("cam.Description",jLabel1);
    jLabel1.setLabelFor(txtDescription);
	jLabel2.setHorizontalAlignment(SwingConstants.LEFT);
	I18N.setTextAndMnemonic("cam.Webcam_URL",jLabel2);
	jLabel2.setLabelFor(txtURL);
	jLabel3.setHorizontalAlignment(SwingConstants.LEFT);
	I18N.setTextAndMnemonic("cam.Refresh_time_in_seconds",jLabel3);
	jLabel3.setLabelFor(txtRefresh);
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
	
	btnOK.setText(I18N.gettext("OK"));
	btnOK.addActionListener(new java.awt.event.ActionListener()
	{
	  public void actionPerformed(ActionEvent e)
	  {
		btnOK_actionPerformed(e);
	  }
	});
	getContentPane().add(panel1, BorderLayout.CENTER);
	panel1.add(jLabel1, null);
	panel1.add(txtDescription, null);
	panel1.add(jLabel2, null);
	panel1.add(txtURL, null);
	panel1.add(jLabel3, null);
	panel1.add(txtRefresh, null);
	panel1.add(jPanel1, null);
	jPanel1.add(btnOK, null);
	jPanel1.add(btnCancel, null);
  }

	void btnOK_actionPerformed(ActionEvent e)
	{
		if(txtURL.getText().equals("")) return;
		URL url = null;
		int refresh = 60;
		try
		{
			url = new URL(txtURL.getText());
		}
		catch (MalformedURLException e1)
		{
			JOptionPane.showMessageDialog(this,I18N.gettext("cam.Wrong_URL"),I18N.gettext("cam.Wrong_URL"),JOptionPane.ERROR_MESSAGE);
			return;
		} 
		if(!txtRefresh.getText().equals(""))
		{
			try
			{
				refresh = Integer.parseInt(txtRefresh.getText());
			}
			catch(NumberFormatException e2) 
			{
				refresh = 60;
			}
		}
		backend.send(new InfoQuery(jidStatus.getCompleteJID(),"set",new IQCam(new IQXOOB(url,txtDescription.getText()),refresh))); 
		dispose();
	}

	void btnCancel_actionPerformed(ActionEvent e)
	{
		this.dispose();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
