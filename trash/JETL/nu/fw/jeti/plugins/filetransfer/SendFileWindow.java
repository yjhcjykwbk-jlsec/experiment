/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 
package nu.fw.jeti.plugins.filetransfer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

//24-0kt-2004
public class SendFileWindow extends JFrame
{
	private Backend backend;
	private JID  jid;
    private JLabel jLabel1 = new JLabel();
    private JTextArea txtDescription = new JTextArea(4, 30);
    private JButton btnSelect = new JButton();
    private JTextField txtFilename = new JTextField();
    private JLabel jLabel2 = new JLabel();
    private JPanel btnPanel = new JPanel();
    private JButton btnSend = new JButton();
    private JButton btnCancel = new JButton();
	private StreamSend streamSend;
	private javax.swing.Timer timer;
	private String id;
	private File file;

	public SendFileWindow(Backend backend,JID to) {
        this(backend, to, new File(""));
    }

	public SendFileWindow(Backend backend, JID to, File file) {
		this.backend = backend;
        this.file = file;
		jid = to;
		try {
			jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
		pack();
		setLocationRelativeTo(null);
        toFront();
		//TODO check if si supported by using disco
	}



	private void jbInit() throws Exception
    {
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		getRootPane().setDefaultButton(btnSend);
        setTitle(I18N.gettext("filetransfer.File_Transfer"));

		txtFilename.setText(file.toString());

        I18N.setTextAndMnemonic("filetransfer.Description", jLabel1);
        jLabel1.setLabelFor(txtDescription);
		jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelect.setText("...");
        btnSelect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnSelect_actionPerformed(e);
            }
        });
        I18N.setTextAndMnemonic("filetransfer.File_Name",jLabel2);
        jLabel2.setLabelFor(txtFilename);
		I18N.setTextAndMnemonic("filetransfer.Send",btnSend);
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
        
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 5, 0, 3);
        c.weightx = 1.0;

        getContentPane().add(jLabel2, c);

        c.gridwidth = 1;
        getContentPane().add(txtFilename, c);

        c.weightx = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        getContentPane().add(btnSelect, c);

        getContentPane().add(jLabel1, c);

        c.weightx = 1.0;
        c.weighty = 1.0;
        getContentPane().add(new JScrollPane(txtDescription), c);

        c.weighty = 0.0;

        btnPanel.add(btnSend, null);
        btnPanel.add(btnCancel, null);
        getContentPane().add(btnPanel, c);
    }

    void btnSelect_actionPerformed(ActionEvent e)
    {
    	JFileChooser fileChooser = Plugin.getFileChooser();
    	String dir =Preferences.getString("filetransfer", "uploadDir",null);
    	if(dir!=null)fileChooser.setCurrentDirectory(new File(dir));
		int s = fileChooser.showOpenDialog(this);
		if(s != JFileChooser.APPROVE_OPTION) {
            return; //cancel
        }
		File file = fileChooser.getSelectedFile();
		Preferences.putString("filetransfer", "uploadDir",file.getParent());
		txtFilename.setText(file.getAbsolutePath());
    }

	void btnSend_actionPerformed(ActionEvent e)
    {
		file = new File(txtFilename.getText());
        if (!file.isAbsolute()) {
            file = new File(Plugin.getFileChooser().getCurrentDirectory(),
                            txtFilename.getText());
        }
		if(!file.exists())
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_does_not_exist"), new Object[] { txtFilename.getText() }),I18N.gettext("filetransfer.File_Error"));
			return;
		}
		if(!file.canRead())
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_is_not_readable"), new Object[] { txtFilename.getText() }),"File Error");
			return;
		}
		
        new SendFileProgress(backend,jid,file,txtDescription.getText()).show();
        this.dispose();
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
