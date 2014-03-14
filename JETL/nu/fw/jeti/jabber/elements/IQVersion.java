package nu.fw.jeti.jabber.elements;

import java.text.MessageFormat;

import javax.swing.JOptionPane;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;


/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQVersion extends Extension implements IQExtension
{
	private String os;
	private String name;
	private String version;

	public IQVersion(){}

	public IQVersion(String name, String version,String os)
    {
		this.os = os;
		this.name = name;
		this.version = version;
    }

	public String getOS(){return os;}

	public String getName(){return name;}

	public String getVersion(){return version;}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		String type = iq.getType();
		if (type.equals("get"))
		{
			IQVersion version = new IQVersion("JETI", nu.fw.jeti.backend.Start.VERSION, System.getProperty("os.name"));
			backend.send(new InfoQuery(iq.getFrom(),"result", iq.getID(),version));
		}
		else if (type.equals("result"))
		{
			versionPopup(iq.getFrom().toStringNoResource(), getName(), getVersion(), getOS());
			//System.out.println(iq.getFrom().toStringNoResource() + " " +  getName() + " " +  getVersion() + " " + getOS());
		}
		else if (type.equals("error"))
		{
			Popups.errorPopup(iq.getErrorDescription(), I18N.gettext("main.error.Version_Error"));
		}
		
	}
	
	private void versionPopup(String jid,String name,String version,String os)
	{
		Popups.popup(MessageFormat.format(I18N.gettext("main.popup.{0}_uses:_{1},_version:_{2},_On:_{3}"),new Object[]{jid,name,version,os}),I18N.gettext("main.popup.Version"),JOptionPane.INFORMATION_MESSAGE);
	}
		
	public void appendToXML(StringBuffer xml)
    {
        xml.append("<query xmlns=\"jabber:iq:version\"");
		if(name == null && version == null && os ==null)
		{ //short cut
		    xml.append("/>");
			return;
		}
		xml.append('>');
		appendElement(xml,"name",name);
		appendElement(xml,"version",version);
		appendElement(xml,"os",os);
		xml.append("</query>");
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
