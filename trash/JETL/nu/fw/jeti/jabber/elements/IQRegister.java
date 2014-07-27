package nu.fw.jeti.jabber.elements;
import java.util.Iterator;
import java.util.Map;

import nu.fw.jeti.events.RegisterListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class IQRegister extends Extension implements IQExtension
{
	private boolean remove =false;
	private Map fields;
	private XData xdata;


	public IQRegister(){}
	
	public IQRegister(XData xdata){this.xdata = xdata;}

	public IQRegister(boolean remove,Map fields,XData xdata)
	{
		this.remove = remove;
		this.fields =fields;
		this.xdata = xdata;
	}

	public IQRegister(boolean remove,Map fields)
	{
		this.remove = remove;
		this.fields =fields;
	}

	public boolean getRemove(){return remove;}

	public Map getFields(){return fields;}
	
	public XData getXData(){return xdata;}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			for (Iterator j = backend.getListeners(RegisterListener.class); j.hasNext();)
			{
				((RegisterListener) j.next()).register(this,iq.getID());
			}

		}
		else if (iq.getType().equals("error"))
		{
			Popups.errorPopup(iq.getErrorDescription(), I18N.gettext("main.error.Register_Error"));
		}
	}


	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns=\"jabber:iq:register\"");
		if(fields == null && !remove && xdata == null)
		{ //short cut
			xml.append("/>");
			return;
		}
		xml.append('>');
		appendElement(xml,fields);
		appendElement(xml,"remove",remove);
		if(xdata != null) xdata.appendToXML(xml); 
		xml.append("</query>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
