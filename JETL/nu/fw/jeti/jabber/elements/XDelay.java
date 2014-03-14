package nu.fw.jeti.jabber.elements;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import nu.fw.jeti.jabber.JID;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class XDelay extends Extension implements XExtension
{
	private String stamp;
	private JID from;
	private static DateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd'T'hh:mm:ss");

	public XDelay(){}

	public XDelay(String timeStamp, JID from)
	{
		this.from = from;
		this.stamp = timeStamp;
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public String getTimeStamp(){return stamp;}
	
	public Date getDate()
	{
		try
		{
			return dateFormat.parse(stamp);
		} catch (java.text.ParseException e1)
		{
			e1.printStackTrace();
		}
		return null;
	}

	public JID getFrom(){return from;}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<x xmlns=\"jabber:x:delay\"");
//		if(tz == null && utc == null && display ==null)
//		{ //short cut
//			xml.append("/>");
//			return;
//		}
		appendAttribute(xml,"from",from);
		appendAttribute (xml,"stamp",stamp);
		xml.append('>');
		xml.append("</x>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
