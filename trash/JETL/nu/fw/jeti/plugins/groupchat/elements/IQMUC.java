package nu.fw.jeti.plugins.groupchat.elements;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
import nu.fw.jeti.util.Popups;

/**
 * @author Martin Forssen
 */
public class IQMUC extends Extension implements IQExtension
{
    static private Map requests = new HashMap(5);
    static private int idCount = 0;
    private String instance;
    private Backend backend;
    private JID roomJID;
    private XData xdata;
    private List items;
    private int errorCode;
    private String errorDescription;
    private Map timeoutTimers = new HashMap(20);
    private Timer timer = new Timer(true);

    public IQMUC(String instance, Backend backend, JID roomJID, XData xdata) {
        this.instance = instance;
        this.backend = backend;
        this.roomJID = roomJID;
        this.xdata = xdata;
    }

    public IQMUC(Backend backend, JID roomJID) {
        this.backend = backend;
        this.roomJID = roomJID;
    }

    public IQMUC(String instance, XData xdata) {
        this.instance = instance;
        this.xdata = xdata;
    }
    
    public IQMUC(String instance, List items,
                 int errorCode, String errorDescription) {
        this.instance = instance;
        this.items = items;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
    
    public XData getXData() {
        return xdata;
    }

    public List getItems() {
        return items;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public synchronized void send(String type, IQMUCInterface handler) {
        String id = "Jeti_Groupchat_" + idCount++;
        InfoQuery iq = new InfoQuery(roomJID,type,id,this);
        timeout(iq);
        requests.put(id,handler);
        backend.send(iq);
    }

    public void execute(InfoQuery iq,Backend backend) {
        IQMUCInterface e = (IQMUCInterface)requests.remove(iq.getID());
        if (e != null) {
            e.execute(iq, backend, this);
        }
    }

    public void appendToXML(StringBuffer xml) {
        // XXX
        xml.append("<query xmlns= 'http://jabber.org/protocol/muc#"
                   + instance + "'>");
        if (xdata !=null) {
            xdata.appendToXML(xml);
        }
        if (items != null) {
            for (Iterator i = items.iterator(); i.hasNext();) {
                ((XMUCUser)i.next()).appendToXML(xml);
            }
        }
        xml.append("</query>");
    }

    public synchronized void error(String id,JID jid) {
        TimerTask t =(TimerTask) timeoutTimers.remove(id);
        if (t != null)t.cancel();
        IQMUCInterface e = (IQMUCInterface)requests.remove(id);
        if (e != null) {
            e.timeout();
        }
    }

    // times out a request
    private void timeout(final InfoQuery query) {
        TimerTask t = new TimerTask() {
            public void run() {
                error(query.getID(),query.getTo());
            }
        };
        timer.schedule(t,10000);
        timeoutTimers.put(query.getID(),t);
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
