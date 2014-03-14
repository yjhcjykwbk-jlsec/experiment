/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2001 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 */

package nu.fw.jeti.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Packet;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Log;

/**
 * @author E.S. de Boer
 */

public class LogWindow extends JFrame
{

	private JPanel jPanel1 = new JPanel();
	private JButton btnClose = new JButton();
	private JButton btnRefresh = new JButton();
	private JTabbedPane jTabbedPane1 = new JTabbedPane();
	private JTextArea txtXML = new JTextArea();
	private JScrollPane jScrollPane1 = new JScrollPane();
	private JTextArea txtXMLErrors = new JTextArea();
	private JScrollPane jScrollPane2 = new JScrollPane();
	private JTextArea txtErrors = new JTextArea();
	private JScrollPane jScrollPane3 = new JScrollPane();
	private int xml;
	private int xmlErrors;
	private int errors;
	private Backend backend;

	public LogWindow(Backend backend)
	{
		this.backend = backend;
		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		txtXML.setText(I18N.gettext("main.log.Please_wait_loading_log"));
		new Thread()
		{
			public void run()
			{
				try{
				StringBuffer buffer = new StringBuffer();
				List temp = Log.getXML();
				for (Iterator i = temp.iterator(); i.hasNext();)
				{
					buffer.append(i.next());
					buffer.append("\n");
					sleep(1);
				}
				txtXML.setText(buffer.toString());
				buffer = new StringBuffer();
				xml = temp.size();
				temp = Log.getXMLErrors();
				for (Iterator i = temp.iterator(); i.hasNext();)
				{
					buffer.append(i.next());
					buffer.append("\n");
					sleep(1);
				}
				txtXMLErrors.setText(buffer.toString());
				xmlErrors = temp.size();
				temp = Log.getErrors();
				buffer = new StringBuffer();
				for (Iterator i = temp.iterator(); i.hasNext();)
				{
					buffer.append(i.next());
					buffer.append("\n");
					sleep(1);
				}
				txtErrors.setText(buffer.toString());
				errors = temp.size();
				System.out.println("end thread");
				}catch(InterruptedException e){}
			}
		}.start();
	}
	
	private void jbInit() throws Exception
	{
		jTabbedPane1.setBorder(null);
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.log.JETI_Log"));
		btnClose.setText(I18N.gettext("Close"));
		Action cancelAction = new AbstractAction(I18N.gettext("Close"))
		{
			public void actionPerformed(ActionEvent e)
			{
				btnClose_actionPerformed(e);
			}
		};
		btnClose.setAction(cancelAction);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
				
		//btnRefresh.setText(I18N.gettext("main.log.Refresh"));  
		I18N.setTextAndMnemonic("main.log.Refresh",btnRefresh);
		btnRefresh.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnRefresh_actionPerformed(e);
			}
		});
		JButton btnClear = new JButton();
		I18N.setTextAndMnemonic("main.log.Clear_Log",btnClear);
		btnClear.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Log.clear();
				xml=0;
				txtXML.setText("");
				btnRefresh_actionPerformed(e);
			}
		});
		//JButton btnSend = new JButton(I18N.gettext("main.log.Send_XML"));
		JButton btnSend = new JButton();
		I18N.setTextAndMnemonic("main.log.Send_XML",btnSend);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
		//jPanel1.add(btnSend);
		jPanel1.add(btnClear, null);
		jPanel1.add(btnRefresh, null);
		jPanel1.add(btnClose, null);
		//send xml window
		JPanel pnlSendXML = new JPanel(new BorderLayout());
		pnlSendXML.add(new JLabel(I18N.gettext("main.log.Send_XML_to_the_Jabber_server,_use_this_at_your_own_risk")), BorderLayout.NORTH);
		final JTextArea txtXMLSend = new JTextArea();
		//txtXMLSend.
		pnlSendXML.add(new JScrollPane(txtXMLSend),BorderLayout.CENTER);
		btnSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!txtXMLSend.getText().equals(""))
				{
					backend.send(new Packet()
					{
						public void appendToXML(StringBuffer xml)
						{
							xml.append(txtXMLSend.getText());
							txtXMLSend.setText("");
						}
					});
				}
			}
		});
		pnlSendXML.add(btnSend,BorderLayout.SOUTH);
		getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
		//TODO mnemonics on tab
		jTabbedPane1.add(jScrollPane1, I18N.gettext("main.log.XML"));
		jTabbedPane1.add(jScrollPane2, I18N.gettext("main.log.XML_Errors"));
		jTabbedPane1.add(jScrollPane3, I18N.gettext("main.log.Errors"));
		jTabbedPane1.add(pnlSendXML, I18N.gettext("main.log.Send_XML"));
		jScrollPane3.getViewport().add(txtErrors, null);
		jScrollPane2.getViewport().add(txtXMLErrors, null);
		jScrollPane1.getViewport().add(txtXML, null);
		setSize(400, 400);
		setLocationRelativeTo(null);
	}

	void btnClose_actionPerformed(ActionEvent e)
	{
		this.dispose();
	}

	void btnRefresh_actionPerformed(ActionEvent e)
	{
		new Thread()
		{
			public void run()
			{
				List temp = Log.getXML();
				StringBuffer buffer = new StringBuffer();
				for (Iterator i = temp.listIterator(xml); i.hasNext();)
				{
					buffer.append(i.next());
					buffer.append("\n");
				}
				xml = temp.size();
				txtXML.append(buffer.toString());
				temp = Log.getXMLErrors();
				for (Iterator i = temp.listIterator(xmlErrors); i.hasNext();)
				{
					txtXMLErrors.append((String) i.next() + "\n");
				}
				xmlErrors = temp.size();
				temp = Log.getErrors();
				for (Iterator i = temp.listIterator(errors); i.hasNext();)
				{
					txtErrors.append((String) i.next() + "\n");
				}
				errors = temp.size();
			}
		}.start();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
