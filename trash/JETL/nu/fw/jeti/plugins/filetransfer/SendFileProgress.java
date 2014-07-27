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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import nu.fw.jeti.backend.roster.Roster;
import nu.fw.jeti.events.ErrorListener;
import nu.fw.jeti.events.IQResultListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.elements.XDataBuilder;
import nu.fw.jeti.jabber.elements.XDataField;
import nu.fw.jeti.jabber.elements.XDataFieldBuilder;
import nu.fw.jeti.plugins.filetransfer.ibb.IBBSend;
import nu.fw.jeti.plugins.filetransfer.socks5.Socks5Send;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

//24-0kt-2004
public class SendFileProgress extends JFrame implements IQResultListener
{
    private Backend backend;
    private JID to;
    private StreamSend streamSend;
    private javax.swing.Timer timer;
    private String id;
    private File file;
    private boolean begun = false;

    private JButton btnAbort = new JButton();
    private JLabel toTxt = new JLabel();
    private JLabel fileTxt = new JLabel();
    private JLabel sizeTxt = new JLabel();
    private JLabel statusTxt = new JLabel();
    private JProgressBar progressBar = new JProgressBar();

    public SendFileProgress(Backend backend, JID to, File file,
                            String description) {
        this.backend = backend;
        this.to = to;
        this.file = file;
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
        JIDStatus js = Roster.getJIDStatus(to);
        if(js!=null)toTxt.setText(js.getNick());
        else toTxt.setText(to.toString()); 
        	
        fileTxt.setText(file.toString());
        sizeTxt.setText(Plugin.getSizeText(file.length()));
        statusTxt.setText(I18N.gettext("filetransfer.Opening_connection") + "...");

        pack();
        setLocationRelativeTo(null);

        progressBar.setMaximum((int)(file.length()/1024));

        id = "file"+backend.getIdentifier();
        XDataBuilder xdb = new XDataBuilder();
        xdb.type ="form";
        XDataFieldBuilder xdfb = new XDataFieldBuilder();
        xdfb.var="stream-method";
        xdfb.type="list-single";
        if (Preferences.getBoolean("filetransfer","bytestreams.enable",true)) {
            xdfb.addOption("http://jabber.org/protocol/bytestreams");
        }
        if (Preferences.getBoolean("filetransfer", "ibb.enable", true)) {
            xdfb.addOption("http://jabber.org/protocol/ibb");
        }
        xdb.addField(xdfb.build());
        XData xdata=null;
        try {
            xdata = xdb.build();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        }
        IQSi si = new IQSi(
            id, "http://jabber.org/protocol/si/profile/file-transfer",null, 
            xdata,
            new XSiFileTransfer(file.getName(), null, null, file.length(),
                                description, 0, 0));
        backend.send(
            new InfoQuery(to,"set","filetransfer"+backend.getIdentifier(),si),
            this, 0);
		
        backend.addListener(ErrorListener.class,this);
    }

    private void jbInit() throws Exception
    {
        setIconImage(
            nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
        setTitle(I18N.gettext("filetransfer.File_Transfer"));

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 5, 0, 3);
        GridBagConstraints c2 = (GridBagConstraints)c.clone();
        c2.gridwidth = GridBagConstraints.REMAINDER;
        c2.weightx = 1.0;

        JLabel lbl = new JLabel(I18N.gettext("filetransfer.To"));
        getContentPane().add(lbl, c);
        getContentPane().add(toTxt, c2);
        
        lbl = new JLabel(I18N.gettext("filetransfer.File_Name"));
        getContentPane().add(lbl, c);
        getContentPane().add(fileTxt, c2);

        lbl = new JLabel(I18N.gettext("filetransfer.File_Size"));
        getContentPane().add(lbl, c);
        getContentPane().add(sizeTxt, c2);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(5, 5, 0, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(statusTxt, c);

        progressBar.setPreferredSize(new Dimension(300, 17));
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 0, 5);
        getContentPane().add(progressBar, c);

        getRootPane().setDefaultButton(btnAbort);
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 5, 5, 5);
        getContentPane().add(btnAbort, c);

        Action abortAction =
            new AbstractAction(I18N.gettext("filetransfer.Abort")) {
            public void actionPerformed(ActionEvent e) {
                btnAbort_actionPerformed(e);
            }
        };
        btnAbort.setAction(abortAction);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.getActionMap().put("cancel", abortAction);
        layeredPane.getInputMap(
            JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "abort");
    }

    void btnAbort_actionPerformed(ActionEvent e)
    {
        if(streamSend!=null)
        {
            timer.stop();
            streamSend.cancel();
        }
        this.dispose();
    }
    
    public void iqResult(InfoQuery query)
    {
    	if(query.getType().equals("result"))
    	{
            IQSi iqSi =(IQSi) query.getIQExtension();
            XSiFileTransfer sifi = iqSi.getSiprofile();
            if(sifi!=null)
            {//TODO range
    			
            }
            String streamMethod=null;
            XData data= iqSi.getXDataForm();
            if(data.hasFields())
            {
                for(Iterator i = data.getFields();i.hasNext();)
                {
                    XDataField datafield= (XDataField)i.next();
                    if("stream-method".equals(datafield.getVar()))
                    {
                        streamMethod = datafield.getValue();
                    }
                }
            }
            if("http://jabber.org/protocol/ibb".equals(streamMethod)) {
                streamSend = new IBBSend(file,backend,this,to);
            } else if("http://jabber.org/protocol/bytestreams".equals(streamMethod)) {
                streamSend = new Socks5Send(file,backend,this,to);
            } else {
                Popups.messagePopup(I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted"),I18N.gettext("filetransfer.File_Transfer"));
                this.dispose();
                return;
            }
            initTimer();
        }
    	else if(query.getType().equals("error"))
    	{
            String msg;
            if(query.getErrorCode()==403) {
                msg = MessageFormat.format(
                    I18N.gettext("filetransfer.{0}_did_not_accept_the_file"),
                    new Object[] { Roster.getJIDStatus(to).getNick() });
            } else if (query.getErrorCode()==404) {
                msg = MessageFormat.format(
                    I18N.gettext("filetransfer.{0}_is_not_online"),
                    new Object[] { Roster.getJIDStatus(to).getNick() });
            } else if (query.getErrorCode()==400) {
                msg = I18N.gettext("filetransfer.No_compatible_transfer_protocols_found");
            } else if (query.getErrorCode()==501) {
                msg = MessageFormat.format(
                    I18N.gettext("filetransfer.{0}_does_not_support_file_transfer_or_is_offline"),
                    new Object[] { Roster.getJIDStatus(to).getNick() });
            } else {
                msg = I18N.gettext("filetransfer.Problem_during_file_transfer,_transfer_aborted");
            }
            Popups.messagePopup(msg, file.getName());
            this.dispose();
    	}
    }

    private void updateProgress() {
        int bytes=0;
        bytes = (int)streamSend.getBytes()/1024;
        if (bytes >0 && !begun) {
            statusTxt.setText(I18N.gettext("filetransfer.Transferring") + "...");
            begun = true;
        }
        progressBar.setValue(bytes);
    }

    private void initTimer()
    {
        timer = new javax.swing.Timer(1000,new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateProgress();
            }
        });
        timer.start();
    }

    public void stop()
    {
        timer.stop();
        dispose();
    }

    public void done()
    {
        timer.stop();
        updateProgress();
        streamSend = null;
        if(Preferences.getBoolean("filetransfer", "closeOnComplete", false))
        {
        	dispose();
        }
        else
        {
	        statusTxt.setText(I18N.gettext("filetransfer.Transfer_complete"));
	        btnAbort.setText(I18N.gettext("filetransfer.Close"));
	        progressBar.setValue(progressBar.getMaximum());
        }
    }

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
