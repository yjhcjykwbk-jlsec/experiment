package nu.fw.jeti.plugins.ibb;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.*;

import nu.fw.jeti.backend.roster.Roster;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.util.Base64;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

//TODO translate filetransfer warnings, or wait for socks filetransfer?
public class GetFileWindow extends JFrame
{
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JTextArea jTextArea1 = new JTextArea();
    private JLabel jLabel2 = new JLabel();
    private JTextField jTextField1 = new JTextField();
    private JPanel jPanel2 = new JPanel();
    private JButton btnGetSize = new JButton();
    private JPanel jPanel3 = new JPanel();
    private JButton btnDownload = new JButton();
    private JButton btnCancel = new JButton();
	private URL url;
    private JProgressBar jProgressBar1 = new JProgressBar();
	private String id;
	private JID jid;
	private Backend backend;
	//private URLConnection connection = null;
	private int length;
	private Draadje draadje;
    private JLabel lblDownload = new JLabel();
	


    public GetFileWindow(JID jid,Backend backend, IBBExtension ibb)
    {
		//JOptionPane.showConfirmDialog(null,"download "+ urlString )
		this.id =id;
		this.jid = jid;
		this.backend = backend;
		try
        {
			jbInit();
			JIDStatus j = Roster.getJIDStatus(jid);
			if(j!=null)jTextArea1.setText(j.getNick());
			else jTextArea1.setText(jid.toString());
			//jTextField1.setText(url.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
		pack();
		draadje = new Draadje(this);
		draadje.start();
		addData(ibb.getData());
	}

	private void jbInit() throws Exception
    {
		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		getRootPane().setDefaultButton(btnDownload);
        jLabel1.setPreferredSize(new Dimension(300, 17));
        //jLabel1.setText(I18N.gettext("Description"));
        I18N.setTextAndMnemonic("filetransfer.Description",jLabel1);
        jTextArea1.setAlignmentX((float) 0.0);
        jTextArea1.setPreferredSize(new Dimension(300, 17));
        jTextArea1.setEditable(false);

        jLabel2.setPreferredSize(new Dimension(300, 17));
        I18N.setTextAndMnemonic("filetransfer.URL",jLabel2);
        jTextField1.setAlignmentX((float) 0.0);
        jTextField1.setPreferredSize(new Dimension(300, 21));
        jTextField1.setEditable(false);
		//btnGetSize.setMnemonic('G'); 
        //btnGetSize.setText(I18N.gettext("GetSize"));
        I18N.setTextAndMnemonic("filetransfer.GetSize",btnGetSize);
        btnGetSize.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnGetSize_actionPerformed(e);
            }
        });
        //btnDownload.setMnemonic('D'); 
        btnDownload.setText(I18N.gettext("filetransfer.Download"));
        getRootPane().setDefaultButton(btnDownload);
        btnDownload.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                btnDownload_actionPerformed(e);
            }
        });
        //btnCancel.setMnemonic('C'); 
        //btnCancel.setText(I18N.gettext("Cancel"));
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
     
        this.setTitle(I18N.gettext("filetransfer.File_Transfer"));
        jPanel2.setAlignmentX((float) 0.0);
        jPanel3.setAlignmentX((float) 0.0);
        jProgressBar1.setAlignmentX((float) 0.0);
        lblDownload.setToolTipText("");
        this.getContentPane().add(jPanel1,  BorderLayout.CENTER);
		jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));
        jPanel1.add(jLabel1, null);
        jPanel1.add(jTextArea1, null);
        jPanel1.add(jLabel2, null);
        jPanel1.add(jTextField1, null);
        jPanel1.add(jPanel2, null);
        jPanel2.add(btnGetSize, null);
        jPanel2.add(lblDownload, null);
        jPanel1.add(jProgressBar1, null);
        jPanel1.add(jPanel3, null);
        jPanel3.add(btnDownload, null);
        jPanel3.add(btnCancel, null);
    }

    void btnGetSize_actionPerformed(ActionEvent e)
    {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Thread t = new Thread()
		{
			public void run()
			{
				//if(connection == null)
				HttpURLConnection connection = null;
				{
					try
					{
						connection  = (HttpURLConnection)url.openConnection();
						connection.setRequestMethod("HEAD");
						//connection.setRequestMethod()
					}
					catch (IOException e2)
					{
						dispose();
						Popups.errorPopup(url.toExternalForm() + " could not be reached","File transfer");
						return;
					}
				}
				/*
				try{
				((HttpURLConnection)connection).setRequestMethod("HEAD");
				}
				catch(ProtocolException e2){e2.printStackTrace();}
				*/
				length = connection.getContentLength()/1024;
				Runnable updateAComponent = new Runnable() {
				public void run(){
					lblDownload.setText(length + " kB " + length / 1024 + " MB");
					setCursor(Cursor.getDefaultCursor());
					}};
				SwingUtilities.invokeLater(updateAComponent);
		    }
		};
		t.start();

	}

    void btnDownload_actionPerformed(ActionEvent e)
    {
		//final GetFileWindow w = this;
		draadje = new Draadje(this);
		
		draadje.start();
		
	}

	void btnCancel_actionPerformed(ActionEvent e)
    {
		if(draadje !=null)
		{
			
			draadje.interrupt();
		}
		//backend.sendError("406","Not Acceptable",jid,id);
		//backend.send(new InfoQuery(to,error    ) )
//		InfoQueryBuilder iqb = new InfoQueryBuilder();
//			iqb.setErrorCode(406);
//			iqb.setErrorDescription("Not Acceptable");
//			iqb.setId(id);
//			iqb.setTo(jid);
//			iqb.setType("error");
//			backend.send(iqb.build());
		backend.send(new InfoQuery(jid,"error",id,null,"Not Acceptable",406)); 

		dispose();
    }

	public void addData(String data)
	{
		System.out.println("data added");
		draadje.addData(data);
	}
	
	public void stopDownloading()
	{
		draadje.stopDownloading();
		System.out.println("download complete");
	}


	class Draadje extends Thread
	{
		private GetFileWindow getFileWindow;
		private int bytes=0;
		private LinkedList queue = new LinkedList();
		private volatile boolean isDownloading=true;

	    Draadje(GetFileWindow w)
		{
			getFileWindow =w;
		}
		
	    public void addData(String data)
	    {
	    	synchronized(queue)
			{
				queue.addLast(data);
				queue.notifyAll();
			}
	    }
	    
	    public void stopDownloading()
	    {
	    	isDownloading = false;
			synchronized(queue){queue.notifyAll();}
	    }
	    

		public void run()
		{
			JFileChooser fileChooser = new JFileChooser();
			int s = fileChooser.showSaveDialog(getFileWindow);
			if(s != JFileChooser.APPROVE_OPTION) return; //cancel
			

		    //check if enough space on hd

			btnDownload.setEnabled(false);
			btnGetSize.setVisible(false);
			btnCancel.setText("Abort");
			btnCancel.setMnemonic('a');
					
			BufferedOutputStream out=null;
			try{
				
					try{
						out = new BufferedOutputStream (new FileOutputStream(fileChooser.getSelectedFile()));
					}catch(FileNotFoundException e2)
					{
						Popups.errorPopup(fileChooser.getSelectedFile().getAbsolutePath() + " could not be openend in write mode","File transfer");
					}
					if(out!=null)
					{	
						try{
							while(!queue.isEmpty() || isDownloading)
							{
								String base64Data;
								synchronized(queue)
								{
									if (queue.isEmpty())
									{
										try
										{
											System.out.println("waiting");
											queue.wait();
										}
										catch(InterruptedException e)
										{//bug when thrown? called when interrupted
											e.printStackTrace();
											return;
										}
										continue;
									}
									base64Data = (String)queue.removeFirst();
									System.out.println("data read");
								}	
								//System.out.println(base64Data);
								//System.out.println(Base64.decode2(base64Data));
								
								byte[] data = Base64.decode(base64Data);
															
								
								System.out.println("data converted");
								out.write(data, 0, data.length);
								System.out.println("data written");
								//bytes++;
								if (Thread.interrupted()) throw new InterruptedException();
								//yield();
							}
												
							
							//download ok
							//backend.send(new InfoQuery(jid,"result",id,null)); 
						}catch (IOException e2)  //te weinig schijruimte
						{
							Popups.errorPopup(e2.getMessage() + " while downloading " +  url.toExternalForm(),"File transfer");
							//download not ok
							backend.send(new InfoQuery(jid,"error",id,null,"Not Acceptable",406)); 
						}
					}
					dispose();
					System.out.println("downloaded");
			}catch (InterruptedException e3) {}
			try
			{
				if(out!=null)out.close();
			}catch (IOException e2){}
		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
