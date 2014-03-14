// Created on 14-sep-2003
package nu.fw.jeti.plugins.cam;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import nu.fw.jeti.images.StatusIcons;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.IQXOOB;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.plugins.RosterMenuListener;
import nu.fw.jeti.util.I18N;


/**
 * @author E.S. de Boer
 *
 */
public class Plugin extends JFrame implements Plugins
{
	public final static String VERSION = "0.2";
	public final static String DESCRIPTION = "cam.Shows_an_image_from_a_website_and_refreshes_every_x_times";
	public final static String MIN_JETI_VERSION = "0.5.1";
	public final static String NAME = "cam";
	public final static String ABOUT = "by E.S. de Boer";
	private Timer timer;
			
	public static void init(final Backend backend)
	{
		backend.getMain().addToRosterMenu(I18N.gettext("cam.Show_Webcam"),new RosterMenuListener ()
		{
			public void actionPerformed(JIDStatus jidStatus,nu.fw.jeti.backend.roster.JIDStatusGroup group)
			{
				new CamSendWindow(jidStatus,backend).show();
			}
		});
		backend.addExtensionHandler("http://jeti.tk/cam",new IQCamHandler());
	}
			
	public void init(IQXOOB oob,int refresh)
	{	
		setTitle(oob.getDescription());
		setIconImage(StatusIcons.getImageIcon("jeti").getImage());
		//try
		{
			final URL url = oob.getURL(); 
			//new URL("http://vierkooningen.oli.tudelft.nl/cam01.jpg");
			//final URL url = new URL("http://www.cs.vu.nl/~ppsnijde/Pryme.jpg");
			final ImageIcon imageIcon = new ImageIcon(); 
			imageIcon.setImage(Toolkit.getDefaultToolkit().createImage(url));
			final JLabel label = new JLabel(imageIcon);
			timer = new Timer(refresh*1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					
					imageIcon.setImage(Toolkit.getDefaultToolkit().createImage(url));
										
					label.setIcon(null);
//					pack();
//					try
//					{
//						Thread.sleep(100);
//					}
//					catch (InterruptedException e2)
//					{
//						
//						e2.printStackTrace();
//					}
					label.setIcon(imageIcon);
					//pack();
				}
			});
			timer.start();  
			getContentPane().add(label);
		}
//		catch (MalformedURLException e)
//		{
//			JOptionPane.showMessageDialog(this,"Wrong URL for webcam image","Wrong URL",JOptionPane.ERROR_MESSAGE); 
//			//e.printStackTrace();
//		}
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				timer.stop();
				dispose(); 
			}
		});		
    	pack();
    	show();
	}
	
	public static void unload(Backend backend)
	{
		backend.getMain().removeFromRosterMenu(I18N.gettext("cam.Show_Webcam"));
		backend.removeExtensionHandler("http://jeti.tk/cam");
	}
	
	public void unload()
	{
		timer.stop();
		dispose();
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
