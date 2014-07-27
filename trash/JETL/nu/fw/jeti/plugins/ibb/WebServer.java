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
package nu.fw.jeti.plugins.ibb;

 /*
 * An example of a very simple, multi-threaded HTTP server.
 * Implementation notes are in WebServer.html, and also
 * as comments in the source code.
 */
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.swing.*;

import nu.fw.jeti.backend.roster.Roster;
import nu.fw.jeti.events.ErrorListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;


public class WebServer extends JFrame implements ErrorListener
{
 	private JPanel jPanel1 = new JPanel();
	//private URL url;
	private Backend backend;
	private JID  jid;
    private JLabel jLabel1 = new JLabel();
    private JTextArea txtDescription = new JTextArea();
    private JButton btnSelect = new JButton();
    private JTextField txtFilename = new JTextField();
    private JPanel jPanel2 = new JPanel();
    private JProgressBar jProgressBar1 = new JProgressBar();
    private JLabel jLabel2 = new JLabel();
    private JPanel jPanel3 = new JPanel();
    private JButton btnSend = new JButton();
    private JButton btnCancel = new JButton();
	private Worker draadje;
	private javax.swing.Timer timer;
	//private String id;


	public WebServer(Backend backend,JID to)
	{
		this.backend = backend;
		jid = to;
		try
        {
			jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		pack();
	}



	private void jbInit() throws Exception
    {
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		getRootPane().setDefaultButton(btnSend);
        //jLabel1.setPreferredSize(new Dimension(300, 17));
        //txtDescription.setPreferredSize(new Dimension(300, 90));
        this.setTitle(I18N.gettext("filetransfer.File_Transfer"));
        //txtFilename.setPreferredSize(new Dimension(200, 21));

        jLabel1.setText(I18N.gettext("filetransfer.Description"));
		//jLabel1.setAlignmentX(jLabel1.RIGHT_ALIGNMENT);
		jLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        btnSelect.setPreferredSize(new Dimension(43, 21));
       // btnSelect.setMnemonic('S');
        btnSelect.setText("...");
        btnSelect.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnSelect_actionPerformed(e);
            }
        });
        jPanel2.setLayout(new BorderLayout());
        //jLabel2.setText(I18N.gettext("filetransfer.File_Name"));
        I18N.setTextAndMnemonic("filetransfer.File_Name",jLabel2);
        jLabel2.setLabelFor(txtFilename);
		//btnSend.setMnemonic('S');
        //btnSend.setText(I18N.gettext("filetransfer.Send"));
        I18N.setTextAndMnemonic("filetransfer.Send",btnSend);
        btnSend.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnSend_actionPerformed(e);

            }
        });
        //btnCancel.setText(I18N.gettext("Cancel","fileTransfer"));
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
        
        txtDescription.setAlignmentX((float) 0.0);
        txtDescription.setPreferredSize(new Dimension(200, 100));
        txtFilename.setMinimumSize(new Dimension(4, 17));
        txtFilename.setPreferredSize(new Dimension(4, 17));
        this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
		jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));
		jPanel1.add(jLabel1);
        jPanel1.add(txtDescription);
        jPanel1.add(jPanel2);
        jPanel2.add(txtFilename, BorderLayout.CENTER);
        jPanel2.add(btnSelect, BorderLayout.EAST);
        jPanel2.add(jLabel2, BorderLayout.NORTH);
        jPanel2.add(jProgressBar1, BorderLayout.SOUTH);
		jPanel3.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		jPanel2.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		//txtDescription.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        jPanel1.add(jPanel3);
        jPanel3.add(btnSend, null);
        jPanel3.add(btnCancel, null);
    }

    void btnSelect_actionPerformed(ActionEvent e)
    {
		JFileChooser fileChooser = new JFileChooser();
		int s = fileChooser.showOpenDialog(this);
		if(s != JFileChooser.APPROVE_OPTION) return; //cancel
		txtFilename.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

	void btnSend_actionPerformed(ActionEvent e)
    {
		File file = new File(txtFilename.getText());
		if(!file.exists())
		{
			//System.out.println("file not readable");
			Popups.errorPopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_does_not_exist"), new Object[] { txtFilename.getText() }),I18N.gettext("filetransfer.File_Error"));
			return;
		}
		if(!file.canRead())
		{
			//System.out.println("file not readable");
			Popups.errorPopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_is_not_readable"), new Object[] { txtFilename.getText() }),"File Error");
			return;
		}
		
		//abort text not possible due to cancel action?
		btnCancel.setText(I18N.gettext("filetransfer.Abort"));
		//btnCancel.setMnemonic('a');
		jProgressBar1.setMaximum((int)(file.length()/2048));
		draadje = new Worker(file,backend,this,jid);
		timer = new javax.swing.Timer(1000,new ActionListener()
		    {
				public void actionPerformed(ActionEvent evt)
				{
					int bytes = draadje.getBytes();
					//lblDownload.setText(bytes+"/"+connection.getContentLength() + " bytes");
				    jProgressBar1.setValue(bytes);
					//System.out.println("timer timed");
				}
		    }
		);
		draadje.start();
		timer.start();

//		URL url =null;
//		try
//		{
//		    url = new URL("http",InetAddress.getLocalHost().getHostAddress(),server.getLocalPort() ,"/"+file.getName());
//		}catch (IOException e2)
//		{//should not happen
//			e2.printStackTrace();
//			return;
//		}
//		//System.out.println(url);
//		//id = backend.sendFile(txtDescription.getText(),url,jid);
//		backend.send(new InfoQuery(jid,"set",backend.getIdentifier(), new IQXOOB(url,txtDescription.getText())));
		backend.addListener(ErrorListener.class,this);
		btnSend.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

    void btnCancel_actionPerformed(ActionEvent e)
    {
		if(draadje!=null)
		{
			timer.stop();
		    draadje.interrupt();
			backend.removeListener(ErrorListener.class,this);
		}
		this.dispose();
    }

	public void error(int errorCode, String errorText)
	{
		if(draadje!=null)
		{
			timer.stop();
			draadje.interrupt();
			backend.removeListener(ErrorListener.class,this);
			Popups.messagePopup(MessageFormat.format(I18N.gettext("filetransfer.{0}_did_not_accept_the_file."), new Object[] { Roster.getJIDStatus(jid).getNick() } ),I18N.gettext("filetransfer.File_Transfer"));
		}
		this.dispose();
	}

	public void stop()
	{
		timer.stop();
		backend.removeListener(ErrorListener.class, this);
		dispose();
	}

    class Worker extends Thread {
        final static int BUF_SIZE = 4096;
        private File file;
	
        private WebServer window;
        private boolean downloaded=false;
        private int bytes;
        private Backend backend;
        private JID to;


        /* buffer to use for requests */
        byte[] buf;
 
        Worker(File file,Backend backend,WebServer ws,JID to) {
            buf = new byte[BUF_SIZE];
            this.file = file;
            //port = server.getLocalPort();
            this.backend = backend;
            window =ws;
            this.to = to;
        
        }

        public synchronized void run() 
        {
            String sid = backend.getIdentifier();
            backend.send(new InfoQuery(to,"set",new IBBExtension(sid,BUF_SIZE)));
           
            try
            {//simulate wait replace by real id get
                sleep(1000);
                sendFile(sid);
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
		
			
            window.stop();

        }
   
        void sendFile(String sid) throws IOException,InterruptedException  {
            InputStream is = null;
            is = new FileInputStream(file.getAbsolutePath());
            //OutputStream out = (OutputStream) ps;
            try {
                int n;
                while ((n = is.read(buf)) > 0) {
                    String encoded = Base64.encode(buf);
                    //System.out.println(encoded);
                    //ps.write(buf, 0, n);
                    backend.send(new Message(null,to,new IBBExtension(sid,bytes,encoded)));          
                    bytes++;
                    if (Thread.interrupted()) throw new InterruptedException();
                    yield();
                }
                backend.send(new InfoQuery(to,"set",new IBBExtension(sid)));
                downloaded = true;
            } finally {
                is.close();
            }
            System.out.println("file sent");
        }

        public int getBytes()
        {
            return bytes;
        }
    }
}




/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
