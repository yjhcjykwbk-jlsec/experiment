package nu.fw.jeti.jabber.elements;


import java.util.*;

import nu.fw.jeti.events.PreferenceListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;


/**
 * Title:        im
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author E.S. de Boer
 * @version 1.0
 */

public class JetiPrivateExtension extends Extension implements IQXExtension
{
    private Map map;
    private Map messages ;
	private String xmlVer;
	public final static String XML_VERSION ="v1";

	public JetiPrivateExtension(){}

	public JetiPrivateExtension(Map map,Map messages)
	{
		this.messages  = messages;
		this.map = map;
		xmlVer = XML_VERSION;
	}

	public JetiPrivateExtension(JetiExtensionBuilder builder)
    {
		map = Collections.unmodifiableMap(builder.getMap());
		messages = Collections.unmodifiableMap(builder.getMapMessages());
		xmlVer = builder.getXmlVersion();
		if (xmlVer == null) xmlVer = "v1";//empty private space then newest version
    }

    public Map getMap()
    {
	    return map;
    }

	 public Map getMessages()
    {
	    return messages;
    }

	public String getXmlVersion()
    {
	    return xmlVer;
	}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("result"))
		{
			//main.loadPreferences(((J2MExtension)extension).getMap(),((J2MExtension)extension).getMessages(),((J2MExtension)extension).getXmlVersion());
			nu.fw.jeti.util.Preferences.load(this);
			for (Iterator j = backend.getListeners(PreferenceListener.class); j.hasNext();)
			{
				((PreferenceListener) j.next()).preferencesChanged();
			}
		}
		else if (iq.getType().equals("error"))
		{
			Popups.errorPopup(iq.getErrorDescription(), I18N.gettext("main.error.Preferences_load_Error"));
		}
	}

	public void appendToXML(StringBuffer retval)
	{
		retval.append("<j2m xmlns=\"j2m:prefs\"");
		if (map == null && messages == null)
		{ //short cut
			retval.append("/>");
			return;
		}

		appendAttribute(retval,"xmlVersion",xmlVer);
		if(map != null)
		{
			for (Iterator i=map.entrySet().iterator(); i.hasNext(); )
			{
				Map.Entry e = (Map.Entry) i.next();
				appendAttribute(retval,(String)e.getKey(),(String)e.getValue());
			}
		}
		retval.append(">");

		if(messages != null)
		{
			retval.append("<status>");
			for (Iterator i=messages.entrySet().iterator(); i.hasNext(); )
			{
				Map.Entry e = (Map.Entry) i.next();
				{
					retval.append("<"+ e.getKey());
					List tempList = (List)e.getValue();
					for(int tel=0;tel<tempList.size();tel++)
					{
						appendAttribute(retval,"s"+ tel,(String)tempList.get(tel));
					}
					retval.append("/>");
				}
			}
			retval.append("</status>");
		}
		retval.append("</j2m>");
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
