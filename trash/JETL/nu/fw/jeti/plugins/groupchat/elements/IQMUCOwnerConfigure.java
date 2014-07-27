package nu.fw.jeti.plugins.groupchat.elements;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.XDataCallback;
import nu.fw.jeti.jabber.XDataPanel;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.IQExtension;
import nu.fw.jeti.jabber.elements.InfoQuery;
import nu.fw.jeti.jabber.elements.XData;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author Martin Forssen
 */
public class IQMUCOwnerConfigure extends IQMUC implements IQMUCInterface
{
    public IQMUCOwnerConfigure(Backend backend, JID roomJID) {
        this(backend, roomJID, null);
    }

    public IQMUCOwnerConfigure(Backend backend, JID roomJID, XData xdata) {
        super("owner", backend, roomJID, xdata);
        send("set", this);
    }
	
    public void execute(InfoQuery iq,Backend backend, IQMUC muc)
    {
        //System.err.println("IQMUCOwnerConfigure.execute(): " + iq.getType());
        if (iq.getType().equals("result")) {
            if(muc.getXData() != null) {
            	XDataFrame x = new XDataFrame(backend,muc.getXData(),iq.getFrom(),iq.getID());
				x.show();
            }
        } else if (iq.getType().equals("error")) {
            Popups.errorPopup(iq.getErrorDescription(), "Register Error");
        }
    }

    public void timeout() {};

    class XDataFrame extends JFrame implements XDataCallback
    {
        private Backend backend;
        private String id;
        private JID jid;
		 
        public XDataFrame(Backend backend,XData xData,JID jid, String id)
        {
            this.id = id;
            this.backend = backend;
            this.jid =jid;
            setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
            if(xData.getTitle() !=null) setTitle(xData.getTitle());
            else setTitle(I18N.gettext("groupchat.Configure_Room"));
            setContentPane(new XDataPanel(xData,this));
            this.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    cancelForm();
                }
            });
            //pack();
            setSize(550,700);
        }
		
        public void sendForm(XData xdata) {
            new IQMUCOwnerConfigure(backend, jid, xdata);
            this.dispose(); 		
        }

        public void cancelForm() {
            new IQMUCOwnerConfigure(backend, jid, new XData("cancel"));
            this.dispose(); 
        }
    }
}	


//class XDataFrame extends JFrame implements XDataCallback
//{
//	private Backend backend;
//	private String id;
//	private JID jid;
//	 
//	public XDataFrame(Backend backend,XData xData,JID jid, String id)
//	{
//		this.id = id;
//		this.backend = backend;
//		this.jid =jid;
//		setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
//		if(xData.getTitle() !=null) setTitle(xData.getTitle());
//		else setTitle(I18N.gettext("groupchat.Configure_Room"));
//		setContentPane(new XDataPanel(xData,this));
//		this.addWindowListener(new java.awt.event.WindowAdapter()
//		{
//			public void windowClosing(WindowEvent e)
//			{
//				cancelForm();
//			}
//		});
//		pack();
//		   
////		int screenX = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
////		int screenY = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
////		int x = getX();
////		int y = getY();
////		System.out.println(x + ":" + screenX + ":");
////		if(x>screenX) x = 500;
////		if(y>screenY) y=screenY-50;
//		setSize(550,700);
//		show();
//	}
//	
//	public void sendForm(XData xdata)
//	{
//		backend.send(new InfoQuery(jid,"set",id,new IQMUCOwner(xdata)));
//		this.dispose(); 		
//	}
//
//	public void cancelForm()
//	{
//		backend.send(new InfoQuery(jid,"set",id,new IQMUCOwner(new XData("cancel"))));
//		this.dispose(); 
//	}
//}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
