package nu.fw.jeti.jabber.elements;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class IQLast extends Extension implements IQExtension
{
	private String seconds;
	
	public IQLast(){}
	
	public IQLast(String seconds)
	{
		this.seconds = seconds;
	}

	public String getSeconds(){return seconds;}
	
	public void execute(InfoQuery iq,Backend backend)
	{
		if (iq.getType().equals("get"))
		{
			//TODO idle time??
			//sendTime(infoQuery.getFrom(),infoQuery.getID());
		}
		else if (iq.getType().equals("result"))
		{
			lastSeenPopup(iq.getFrom().toStringNoResource(), getSeconds());
		}
		else if (iq.getType().equals("error"))
		{
			Popups.errorPopup(iq.getErrorDescription(), I18N.gettext("main.error.Last_Seen_Error"));
		}
	}
	
	private void lastSeenPopup(String jid,String seconds)
	{
		int second = 0;
		try{
			second = Integer.parseInt(seconds);
		}
		catch (NumberFormatException e)
		{
			Popups.popup(MessageFormat.format(I18N.gettext("main.popup.{0}_was_last_seen_{1}_seconds_ago"),new Object[]{jid,seconds})  ,"main.popup.Last_Seen",JOptionPane.INFORMATION_MESSAGE);	
		}
		Calendar calendar = Calendar.getInstance();
		//calendar.clear();   
		calendar.add(Calendar.SECOND,-second);
			 
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		Popups.popup(MessageFormat.format(I18N.gettext("main.popup.{0}_was_last_seen_on_{1}"),new Object[]{jid, dateFormat.format(calendar.getTime())}),I18N.gettext("main.popup.Last_Seen"),JOptionPane.INFORMATION_MESSAGE);
		//JOptionPane.showMessageDialog(main,display  +"\n Time: " + time + "\n Timezone: " + timezone ,jid + "Time",javax.swing.JOptionPane.INFORMATION_MESSAGE);

	}

	public void appendToXML(StringBuffer xml)
	{
		xml.append("<query xmlns=\"jabber:iq:last\"");
		appendAttribute(xml,"seconds",seconds);
		xml.append("/>");
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
