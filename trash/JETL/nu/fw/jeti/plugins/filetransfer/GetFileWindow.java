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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

//29-okt-2004

public class GetFileWindow extends JFrame
{
	private JProgressBar progressBar = new JProgressBar();
    private JButton btnDownload = new JButton();
    private JButton btnCancel = new JButton();
	private Backend backend;
	private String streamMethod;	
    private InfoQuery iq;
    private OutputStream out;
    private Timer timer;
    private StreamReceive streamReceive;
    private String fromName;
    private String fileName;
    private long fileSize;
    private String description;

    private int ibbMaxSize;
    
    public GetFileWindow(Backend backend,InfoQuery iq)
    {
    	ibbMaxSize = Preferences.getInteger("filetransfer", "ibbMaxSize",
                4096);
    	this.iq=iq;
    	this.backend = backend;
        if (init()) {
        	jbInit();
    		pack();
    		setLocationRelativeTo(null);
    		setVisible(true);
      }
	}
    
    private boolean init()
    {
    	IQSi iqSi =(IQSi) iq.getIQExtension();
    	
	    ArrayList streamOptions = new ArrayList();
		XData data= iqSi.getXDataForm();
		if(data.hasFields())
		{
			for(Iterator i = data.getFields();i.hasNext();)
			{
				XDataField datafield= (XDataField)i.next();
				if("stream-method".equals(datafield.getVar()))
				{
					if(datafield.hasOptions())
					{
						for(Iterator j = datafield.getOptionsIterator();j.hasNext();)
						{
							streamOptions.add(((Object[])j.next())[1]);
						}
					}
				}
			}
		}
		String value=null;
		
		XSiFileTransfer sifi = iqSi.getSiprofile();
		if(sifi!=null)
		{//TODO range		
			if(streamOptions.contains("http://jabber.org/protocol/bytestreams")
               && Preferences.getBoolean("filetransfer", "bytestreams.enable",
                                         true)) {
                value="http://jabber.org/protocol/bytestreams";
            }
	        // select inband for files < ibbMaxSize
			if(sifi.getSize()<ibbMaxSize
               && Preferences.getBoolean("filetransfer", "ibb.enable", true)
               && streamOptions.contains("http://jabber.org/protocol/ibb")) {
				value="http://jabber.org/protocol/ibb";
			}
			if(value==null) {
				XMPPError error = new XMPPError("cancel",400);
				error.addError(new XMPPErrorTag("bad-request"));
				error.addError(new XMPPErrorTag("no-valid-streams","http://jabber.org/protocol/si"));
				backend.send(new InfoQuery(iq.getFrom(),iq.getID(),error));
				return false;
			}
			streamMethod = value;
						
			JIDStatus j = backend.getJIDStatus(iq.getFrom());
			if(j==null) {
                fromName = iq.getFrom().toString();
			} else {
                fromName = j.getNick();
            }
            fileName = sifi.getName();
            fileSize = sifi.getSize();
        	progressBar.setMaximum((int)(fileSize/1024));
            description = sifi.getDescription();
		}
        return true;
	}


	private void jbInit()
    {
		setIconImage(StatusIcons.getImageIcon("jeti").getImage());
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

        JLabel lbl = new JLabel(I18N.gettext("filetransfer.From"));
        getContentPane().add(lbl, c);
        lbl = new JLabel(fromName);
        getContentPane().add(lbl, c2);

        lbl = new JLabel(I18N.gettext("filetransfer.File_Name"));
        getContentPane().add(lbl, c);
        lbl = new JLabel(fileName);
        getContentPane().add(lbl, c2);
        
        lbl = new JLabel(I18N.gettext("filetransfer.File_Size"));
        getContentPane().add(lbl, c);
        lbl = new JLabel(Plugin.getSizeText(fileSize));
        getContentPane().add(lbl, c2);

        if (description != null && description.length() > 0) {
            System.out.println("Desc: " + description.length());
            System.out.println("Desc: <" + description + ">");
            lbl = new JLabel(I18N.gettext("filetransfer.Description"));
            getContentPane().add(lbl, c);
            JTextArea d = new JTextArea(description);
            d.setEditable(false);
            getContentPane().add(new JScrollPane(d), c2);
        }

        c2.anchor = GridBagConstraints.CENTER;
        progressBar.setPreferredSize(new Dimension(300, 17));
        getContentPane().add(progressBar, c2);

        c2.fill = GridBagConstraints.NONE;
        JPanel panel = new JPanel();
        btnDownload.setText(I18N.gettext("filetransfer.Download"));
        getRootPane().setDefaultButton(btnDownload);
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnDownload_actionPerformed(e);
            }
        });
        Action cancelAction =  new AbstractAction(I18N.gettext("Cancel")) {
			public void actionPerformed(ActionEvent e) {
				btnCancel_actionPerformed(e);
			}
		};
		btnCancel.setAction(cancelAction);
        panel.add(btnDownload);
        panel.add(btnCancel);
        getContentPane().add(panel, c2);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JLayeredPane layeredPane = getLayeredPane();
		layeredPane.getActionMap().put("cancel", cancelAction);
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, "cancel");
    }

    void btnDownload_actionPerformed(ActionEvent e)
    {
    	JFileChooser fileChooser = Plugin.getFileChooser();
    	fileChooser.setDialogTitle("Save " + fileName);
    	String dir = Preferences.getString("filetransfer", "downloadDir",null);
    	if(dir!=null)fileChooser.setCurrentDirectory(new File(dir));
    	fileChooser.setSelectedFile(new File(fileName));
    	int a = fileChooser.showSaveDialog(backend.getMainWindow());
		if(a != JFileChooser.APPROVE_OPTION) return;
		File file = fileChooser.getSelectedFile();
		Preferences.putString("filetransfer", "downloadDir",file.getParent());
		
		if(file.exists())
		{
			int opt =JOptionPane.showConfirmDialog(this,MessageFormat.format(
					I18N.gettext("filetransfer.{0}_already_exist,_overwrite?")
					, new String[]{file.toString()})
					,I18N.gettext("filetransfer.File_Transfer")
					,JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(opt == JOptionPane.NO_OPTION) return;
		}
		try{
			out = new FileOutputStream(file);
		}catch(FileNotFoundException e2)
		{
	//		timer.stop();
			Popups.errorPopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_could_not_be_openend_in_write_mode"),
					new Object[]{file.getAbsolutePath()}),
					I18N.gettext("filetransfer.File_Transfer"));
			return;
		}
					
    	XDataBuilder xdb = new XDataBuilder();
		xdb.type ="submit";
		xdb.addField(new XDataField("stream-method",streamMethod));
		XData xdata=null;
		try
		{
			xdata = xdb.build();
		} catch (InstantiationException e1)
		{
			e1.printStackTrace();
		}
		backend.send(new InfoQuery(iq.getFrom(),"result",iq.getID(),new IQSi(xdata,null)));
		Plugin.addGetFile(this,iq.getFrom());
		fileName = file.getName();
		btnDownload.setEnabled(false);
	}

	void btnCancel_actionPerformed(ActionEvent e)
    {
		if(btnCancel.getText().equals(I18N.gettext("filetransfer.Close")))
		{
			dispose();
		}
		else if(streamReceive!=null)
		{
			timer.stop();
		    streamReceive.cancel();
		}
		else
		{
			XMPPError error = new XMPPError("cancel",403);
			error.addError(new XMPPErrorTag("forbidden"));
			//TODO<text xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'>Offer Declined</text>
			backend.send(new InfoQuery(iq.getFrom(),iq.getID(),error));
		}
		dispose();
    }
	
	public OutputStream getOutputStream()
	{
		return out;
	}
	
	public StreamReceive getStreamReceive()
	{
		return streamReceive;
	}
	
	public void startDownloading(StreamReceive receive)
	{
		this.streamReceive = receive;
		initTimer();
	}
	
   private void initTimer()
    {
    	timer = new javax.swing.Timer(1000,new ActionListener()
		    {
				public void actionPerformed(ActionEvent evt)
				{
					long bytes = streamReceive.getBytes();
					progressBar.setValue((int)(bytes/1024));
					int percent = (int) (((double)bytes/fileSize)*100);
					setTitle(percent + "% " +  fileName);
				}
		    }
		);
		timer.start();
    }
	
	public void stopDownloading()
	{
		if(Preferences.getBoolean("filetransfer", "closeOnComplete", false))
		{
			dispose();
		}
		else
		{
			btnCancel.setText(I18N.gettext("filetransfer.Close"));
			progressBar.setValue(progressBar.getMaximum());
			setTitle("100% " +  fileName);
		}
		timer.stop();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
