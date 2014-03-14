package nu.fw.jeti.jabber.elements;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
  * @author E.S. de Boer
 * @version 1.0
 */

public class IQTime  extends Extension implements IQExtension
{
	private String tz;
	private String utc;
	private String display;

	public IQTime(){}

	public IQTime(String utc, String tz,String display)
    {
		this.tz = tz;
		this.utc = utc;
		this.display = display;
    }

	public String getTZ(){return tz;}

	public String getUTC(){return utc;}

	public String getDisplay(){return display;}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		String type = iq.getType();
		if (type.equals("get")) {
			sendTime(iq.getFrom(), iq.getID(),backend);
		} else if (type.equals("result")) {
			timePopup(iq.getFrom().toStringNoResource(), getDisplay(),
                      getUTC(),getTZ());
		} else if (type.equals("error")) {
			Popups.errorPopup(iq.getErrorDescription(), I18N.gettext("main.error.Time_Error"));
		}
	}

	private void timePopup(String jid, String display, String time,
                           String timezone) {
		String info = MessageFormat.format(
            I18N.gettext("main.popup.Local_time_for_{0}_is"),
            new Object[]{jid});
        String timeFormatted = null;
        if (time.length() > 0 && timezone.length() > 0) {
            try {
                DateFormat peerFormat =
                    new java.text.SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
                peerFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date peerDate = peerFormat.parse(time);
                DateFormat localFormat = DateFormat.getDateTimeInstance();
                localFormat.setTimeZone(TimeZone.getTimeZone(timezone));
                timeFormatted = localFormat.format(peerDate);
            } catch (ParseException e) {
                // Do Nothing
            }
        }
        if (timeFormatted == null) {
            timeFormatted = display;
        }
        if (timezone.length() > 0) {
            timeFormatted += "\n" +
                MessageFormat.format(
                    I18N.gettext("main.popup.{0}_is_in_timezone_{1}"),
                    new Object[]{jid,timezone});
        }
		Popups.popup(info + "\n" + timeFormatted,
                     jid + ' ' +  I18N.gettext("main.popup.Time"),
                     JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void sendTime(JID to, String id,Backend backend)
	{
		Date date = new Date();
		DateFormat dateFormat =
            new java.text.SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        TimeZone tz = dateFormat.getTimeZone();
		String tzName = dateFormat.getTimeZone().getDisplayName(
            tz.inDaylightTime(date), TimeZone.SHORT);
		dateFormat.setCalendar(
            Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		IQTime time =
            new IQTime(dateFormat.format(date), tzName, date.toString());
		backend.send(new InfoQuery(to,"result",id,time));
	}

	public void appendToXML(StringBuffer xml)
    {
        xml.append("<query xmlns=\"jabber:iq:time\"");
		if(tz == null && utc == null && display ==null)
		{ //short cut
		    xml.append("/>");
			return;
		}
		xml.append('>');
		appendElement(xml,"utc",utc);
		appendElement(xml,"tz",tz);
		appendElement(xml,"display",display);
		xml.append("</query>");
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
