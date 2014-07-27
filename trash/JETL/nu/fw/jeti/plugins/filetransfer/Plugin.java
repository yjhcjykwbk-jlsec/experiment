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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import nu.fw.jeti.backend.roster.PrimaryJIDStatus;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.plugins.filetransfer.ibb.IBBExtension;
import nu.fw.jeti.plugins.filetransfer.ibb.IBBHandler;
import nu.fw.jeti.plugins.filetransfer.ibb.IBBReceive;
import nu.fw.jeti.plugins.filetransfer.socks5.Socks5Handler;
import nu.fw.jeti.plugins.filetransfer.socks5.Socks5Send;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

import org.xml.sax.Attributes;

/**
 * @author E.S. de Boer
 
 */
//Created on 20-okt-2004
public class Plugin implements Plugins
{
	private static Backend backend;
	private static Plugin plugin;
    private static JFileChooser fileChooser = new JFileChooser();
	private Map fileWindows = new HashMap(10);
	private static JMenuItem menuItem;
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "filetransfer.File_Transfer";
	public final static String MIN_JETI_VERSION = "0.6.1";
	public final static String NAME = "filetransfer";
	public final static String ABOUT = "by E.S. de Boer";
	
	
	public static void init(final Backend backend)
    {
		backend.getMain().addToOnlineRosterMenu(I18N.gettext("filetransfer.Transfer_File")+ "...",new RosterMenuListener()
		{
			public void actionPerformed(JIDStatus jidStatus,nu.fw.jeti.backend.roster.JIDStatusGroup group)
			{
				new SendFileWindow(backend,jidStatus.getCompleteJID()).show();
			}
		});

		/*you need the complete JID to transfer so typing in the JID is not usefull?
		menuItem = new JMenuItem(I18N.gettext("filetransfer.Transfer_File")+"...");
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendFile();
			}
		});
		backend.getMain().addToMenu(menuItem);
        */
        
		plugin = new Plugin();
		Plugin.backend = backend;
		backend.addExtensionHandler("http://jabber.org/protocol/si",new IQSiHandler());
		backend.addExtensionHandler("http://jabber.org/protocol/si/profile/file-transfer",new XSiFileTransferHandler());
		backend.addExtensionHandler("http://jabber.org/protocol/feature-neg",new ExtensionHandler()
		{
			private XData data;
			
			public void startHandling(Attributes attr)
			{
				data=null;
			}
			
			public void addExtension(Extension extension)
			{
				if(extension instanceof XData) data = (XData)extension;
			}

			public Extension build() throws InstantiationException
			{
				return data;
			}
		});
		backend.addExtensionHandler("http://jabber.org/protocol/ibb",new IBBHandler());
		backend.addExtensionHandler("http://jabber.org/protocol/bytestreams",new Socks5Handler());

        backend.getMain().getOnlinePanel().setTransferHandler(new FileTransferhandler());
    }
	
	public static void addGetFile(GetFileWindow w, JID jid)
	{
		plugin.fileWindows.put(jid,w);
	}
	
	public static GetFileWindow getGetFile(JID jid)
	{
		return (GetFileWindow)plugin.fileWindows.remove(jid);
	}

	
	public static void unload(Backend backend)
	{
		backend.getMain().removeFromRosterMenu(I18N.gettext("filetransfer.Transfer_File")+ "...");
		//backend.getMain().removeFromMenu(plugin.menuItem);
		backend.removeExtensionHandler("http://jabber.org/protocol/ibb");
		backend.removeExtensionHandler("http://jabber.org/protocol/si");
		backend.removeExtensionHandler("http://jabber.org/protocol/si/profile/file-transfer");
		backend.removeExtensionHandler("http://jabber.org/protocol/feature-neg");
		plugin = null;
	}
	
	public void unload() {}

	private void addDropHandler(JTree tree) {
        tree.setTransferHandler(new FileTransferhandler());
    }

	public static String sha(String sid,JID initiator,JID target)
	{
		System.out.println(sid.toString());
		System.out.println(initiator.toString());
		System.out.println(target.toString());
		
		
		MessageDigest sha=null;
		try
		{
			sha = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sha.update(sid.getBytes());
		sha.update(stringPrep(initiator).getBytes());
		return toString(sha.digest(stringPrep(target).getBytes()));
	}
	
	private static String stringPrep(JID jid)
	{//TODO make real stringprep
		StringBuffer j = new StringBuffer();
		if (jid.getUser() != null){
			j.append(jid.getUser().toLowerCase());
			j.append("@");
		}
		j.append(jid.getDomain().toLowerCase());
		if (jid.getResource() != null){
			j.append("/");
			j.append(jid.getResource());
		}
		return j.toString();
	}
	
	
	private static String toString(byte[] bytes)
	{//TODO make 1 instead of 3
		StringBuffer buf = new StringBuffer(bytes.length * 2);
		for(int i = 0; i < bytes.length; i++)
		{
			int hex = bytes[i];
			if (hex < 0) hex = 256 + hex;
			if (hex >=16) buf.append(Integer.toHexString(hex));
			else
			{
				buf.append('0');
				buf.append(Integer.toHexString(hex));
			}
		}
		return buf.toString().toLowerCase();
	}
	
	public static String getIP()
	{
		Enumeration interfaceEnum;
		try
		{
			interfaceEnum = NetworkInterface.getNetworkInterfaces();
			while (interfaceEnum.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface)
									interfaceEnum.nextElement();
				System.out.println(n.getDisplayName() + ":");
				Enumeration ipEnum = n.getInetAddresses();
				while (ipEnum.hasMoreElements())
				{
					InetAddress ip = (InetAddress) ipEnum.nextElement();
					if(ip instanceof Inet4Address)
					{
						System.out.println(ip.isLoopbackAddress());
						System.out.println(ip.isSiteLocalAddress());
						System.out.println("\t" + ip);
						if (!ip.isLoopbackAddress() && !ip.isSiteLocalAddress())
						{
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

    protected static JFileChooser getFileChooser() {
        return fileChooser;
    }

	public static String getSizeText(long size)
	{
		if(size<1024) return size + " bytes";
		double s;
		NumberFormat n =NumberFormat.getNumberInstance();
		n.setMaximumFractionDigits(2);
		s = size/1024d;
		if(s<1024) return n.format(s) + " kB";
		s = s/1024d;
		if(s<1024) return n.format(s) + " MB";
		s = s/1024d;
		return n.format(s) + " GB";
	}
/*
    public static void sendFile() {
		String recipient = JOptionPane.showInputDialog(
            backend.getMainFrame(),
            I18N.gettext("filetransfer.Send_file_to", "plguins"),
            I18N.gettext("filetransfer.Recipient?"),
            JOptionPane.QUESTION_MESSAGE);
		JID jid = null;

        try {
            if (recipient == null || recipient.length() == 0
                || null == (jid = JID.checkedJIDFromString(recipient))
                || null == jid.getUser()) {
                Popups.errorPopup(
                    I18N.gettext("filetransfer.No_recipient_specified"),
                    I18N.gettext("main.error.Error"));
                return;
            }
        } catch (InstantiationException ex) {
            Popups.errorPopup(
                ex.getMessage(),
                I18N.gettext("main.error.Wrong_Jabber_Identifier"));
            return;
        }

        new SendFileWindow(backend, jid).show();
    }
*/
    static private class FileTransferhandler extends TransferHandler {

        public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
            }
            return false;
        }

        public boolean importData(JComponent comp, Transferable t) {
            Object o = ((JTree)comp).getLastSelectedPathComponent();
            JID to;

			if (o instanceof JIDStatus) {
                to = ((JIDStatus)o).getCompleteJID();
            } else if (o instanceof PrimaryJIDStatus) {
                PrimaryJIDStatus p = (PrimaryJIDStatus)o;
                to = p.getJIDPrimaryStatus().getCompleteJID();
            } else {
                return false;
            }

            if (!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
            try {
                Collection files = (Collection) t.getTransferData(
                    DataFlavor.javaFileListFlavor);

                if (files == null || files.size() == 0) {
                    return false;
                }

                // Check that everything is regular files
                for (Iterator i = files.iterator(); i.hasNext(); ) {
                    File file = (File) i.next();
                    if (!file.isFile()) {
                        Popups.errorPopup(
                            I18N.gettext("filetransfer.Can_not_transfer_directories"),
                            I18N.gettext("main.error.Error"));
                        return false;
                    }
                }

                for (Iterator i = files.iterator(); i.hasNext(); ) {
                    File file = (File) i.next();
                    new SendFileWindow(backend, to, file).show();
                }

                return true;
            } catch (UnsupportedFlavorException e) {
                System.out.println("unsupported data");
            } catch (IOException e) {
                System.out.println("IOException");
            }
            return false;
        }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
