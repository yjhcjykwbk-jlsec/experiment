/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
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

package nu.fw.jeti.backend;

import java.text.MessageFormat;
import java.util.Iterator;

import javax.swing.JOptionPane;

import nu.fw.jeti.events.IQResultListener;
import nu.fw.jeti.events.MessageEventListener;
import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.*;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Log;
import nu.fw.jeti.util.Popups;


/**
 * distributes the xml packets received from the server
 * @author E.S. de Boer
 */

public class Jabber implements PacketReceiver
{
	private Backend backend;
	private Discovery browse;
	private IQTimerQueue iqTimerQueue;

	public Jabber(Backend backend, Discovery browse, IQTimerQueue timerQueue)
	{
		iqTimerQueue = timerQueue;
		this.backend = backend;
		this.browse = browse;
	}

	//does nothing
	public void inputDeath()
	{}

	public void streamError()
	{
		backend.streamError();
	}

	public void setJabberHandler(JabberHandler jh)
	{}

	public Handlers getHandlers()
	{
		return null;
	}

	public void receivePackets(Packet packet)
	{
		//System.out.println("received");
		if (packet instanceof InfoQuery) infoQuery((InfoQuery) packet);
		else if (packet instanceof Message) message((Message) packet);
		else if (packet instanceof Presence) presence((Presence) packet);
	}
	
	/*------------------------------InfoQuery------------------------*/
	private void infoQuery(InfoQuery infoQuery)
	{
		IQResultListener iqrListener = iqTimerQueue.getInfoQueryListener(infoQuery.getID());
		if(iqrListener!=null)
		{
			System.out.println("iq result!");
			iqrListener.iqResult(infoQuery);
		}
		else
		{
			if (infoQuery.hasExtensions())
			{
				IQExtension extension = infoQuery.getIQExtension();
				if (extension instanceof IQDiscoInfo)
				{//move to execute
					if (infoQuery.getType().equals("result"))
					{
						browse.discoveryInfoResult(infoQuery.getFrom(), infoQuery.getID(), (IQDiscoInfo) extension);
					}
					else if (infoQuery.getType().equals("error"))
					{
						Log.xmlReceivedError("Disco error " + infoQuery.getErrorCode() + " " + infoQuery.getErrorDescription() + " from " + infoQuery.getFrom());
						browse.discoError(infoQuery.getID(), infoQuery.getFrom());
					}
				}
				else if (extension instanceof IQDiscoItems)
				{//move to execute
					if (infoQuery.getType().equals("result"))
					{
						browse.discoveryItemResult(infoQuery.getFrom(), infoQuery.getID(), (IQDiscoItems) extension);
					}
					else if (infoQuery.getType().equals("error"))
					{
						Log.xmlReceivedError("Disco error " + infoQuery.getErrorCode() + " " + infoQuery.getErrorDescription() + " from " + infoQuery.getFrom());
						browse.discoError(infoQuery.getID(), infoQuery.getFrom());
					}
				}
				else if (extension instanceof IQBrowse)
				{
					if (infoQuery.getType().equals("result"))
					{
						browse.browseResult(infoQuery.getFrom(), infoQuery.getID(), (IQBrowse) extension);
					}
					else if (infoQuery.getType().equals("error"))
					{
						Log.xmlReceivedError("Browse error " + infoQuery.getErrorCode() + " " + infoQuery.getErrorDescription() + " from " + infoQuery.getFrom());
						browse.browseError(infoQuery.getID(), infoQuery.getFrom());
					}
				}
				else extension.execute(infoQuery, backend);
			} else if (infoQuery.getType().equals("set")) {
                XMPPError err = new XMPPError("cancel", 501);
                err.addError(new XMPPErrorTag("feature-not-implemented"));
                backend.send(new InfoQuery(infoQuery.getFrom(), 
                                           infoQuery.getID(), err));
            }
		}
	}
	
	
	/*------------------------------InfoQuery------------------------
	private void infoQuery(InfoQuery infoQuery)
	{
		if (infoQuery.hasExtensions())
		{
			String type = infoQuery.getType();
			for (Iterator i = infoQuery.getExtensions(); i.hasNext();)
			{
				Extension extension = (Extension) i.next();
				if (extension instanceof IQXRoster)
				{
					//make method?
					for (Iterator j = backend.getListeners(CompleteRosterListener.class); j.hasNext();)
					{
						((CompleteRosterListener) j.next()).rosterReceived(infoQuery, (IQXRoster) extension);
					}
				}
				else if (extension instanceof IQTime)
				{
					if (type.equals("get"))
					{
						sendTime(infoQuery.getFrom(), infoQuery.getID());
					}
					else if (type.equals("result"))
					{
						IQTime time = (IQTime) extension;
						Popups.timePopup(infoQuery.getFrom().toStringNoResource(), time.getDisplay(), time.getUTC(), time.getTZ());
					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Time Error");
					}
				}
				else if (extension instanceof IQLast)
				{
					if (type.equals("get"))
					{
						//idle time??
						//sendTime(infoQuery.getFrom(),infoQuery.getID());
					}
					else if (type.equals("result"))
					{
						IQLast last = (IQLast) extension;
						Popups.lastSeenPopup(infoQuery.getFrom().toStringNoResource(), last.getSeconds());
					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Last Seen Error");
					}
				}
				else if (extension instanceof IQVersion)
				{
					if (type.equals("get"))
					{
						sendVersion(infoQuery.getFrom(), infoQuery.getID());
					}
					else if (type.equals("result"))
					{
						IQVersion version = (IQVersion) extension;
						Popups.versionPopup(infoQuery.getFrom().toStringNoResource(), version.getName(), version.getVersion(), version.getOS());
					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Version Error");
					}
				}
				else if (extension instanceof IQPrivate)
				{
					if (type.equals("result"))
					{
						//main.loadPreferences(((J2MExtension)extension).getMap(),((J2MExtension)extension).getMessages(),((J2MExtension)extension).getXmlVersion());
						nu.fw.jeti.util.Preferences.load((JetiPrivateExtension) ((IQPrivate) extension).getExtension());
						for (Iterator j = backend.getListeners(PreferenceListener.class); j.hasNext();)
						{
							((PreferenceListener) j.next()).preferencesChanged();
						}
					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Preferences load Error");
					}
				}
				else if (extension instanceof IQBrowse)
				{
					if (type.equals("result"))
					{
						browse.browseResult(infoQuery.getID(), (IQBrowse) extension);
					}
					else if (type.equals("error"))
					{
						Log.xmlReceivedError("Browse error " + infoQuery.getErrorCode() + " " + infoQuery.getErrorDescription() + " from " + infoQuery.getFrom());
						browse.error(infoQuery.getID(), infoQuery.getFrom());
					}
				}
				else if (extension instanceof IQRegister)
				{
					if (type.equals("result"))
					{
						for (Iterator j = backend.getListeners(RegisterListener.class); j.hasNext();)
						{
							((RegisterListener) j.next()).register((IQRegister) extension,infoQuery.getID());
						}
		
					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Register Error");
					}
				}
				else if (extension instanceof XMUC)
				{
					if (type.equals("result"))
					{
						XData data = ((XMUC)extension).getXData();
						if(data != null)
						{
							new XDataFrame(backend,data,infoQuery.getFrom(),infoQuery.getID()); 							 
						}

					}
					else if (type.equals("error"))
					{
						Popups.errorPopup(infoQuery.getErrorDescription(), "Register Error");
					}
				}
				else if (extension instanceof IQXOOB)
				{
					if (type.equals("set"))
					{
						for (Iterator j = backend.getListeners(OOBListener.class); j.hasNext();)
						{
							((OOBListener) j.next()).oob(infoQuery.getFrom(),infoQuery.getID(),(IQXOOB)extension);
						}
		
					}
					else if (type.equals("error"))
					{
						for (Iterator j = backend.getListeners(ErrorListener.class); j.hasNext();)
						{
							((ErrorListener) j.next()).error(infoQuery.getErrorCode(),infoQuery.getErrorDescription());
						}
					}
				}
				else if (extension instanceof IQCam)
				{
					if (type.equals("set"))
					{
						IQCam cam =(IQCam)extension;
						
						if(Start.isPluginLoaded("cam"))
						{
							((nu.fw.jeti.plugins.Cam)Start.newPluginInstance("cam")).init(cam.getOob(),cam.getRefesh());
						} 
						//new nu.fw.jeti.plugins.cam.Plugin().init(cam.getOob(),cam.getRefesh());
					}
					else if (type.equals("error"))
					{
						System.err.println(infoQuery.getErrorDescription());
					}
				}
			}
		}
	}
	-*/
	
	
	/*-------------------------------Message-------------------------*/
	private void message(Message message)
	{
		if (message.getBody() != null || message.getType().equals("groupchat"))
		{
			for (Iterator j = backend.getListeners(MessageListener.class); j.hasNext();)
			{
				((MessageListener) j.next()).message(message);
			}
		}
		if (message.hasExtensions())
		{
			for (Iterator i = message.getExtensions(); i.hasNext();)
			{
				Extension extension = (Extension) i.next();
				if (extension instanceof XExecutableExtension)
				{
					((XExecutableExtension)extension).execute(message, backend);					
				}
				
				else if (extension instanceof XMessageEvent)
				{//make executable?
					//goes wrong with groupchat
					//System.out.println( "exts" + extension);
					if (message.getBody() == null)
					{
						//no body so composing event
						for (Iterator j = backend.getListeners(MessageEventListener.class); j.hasNext();)
						{
							((MessageEventListener) j.next()).onComposing(message.getFrom(), message.getThread(), (XMessageEvent) extension);
						}
		
						//main.onComposing(message.getFromAddress(),message.getThread());
					}
					else
					{
						//body so request to sent events //goes wrong with groupchat
						for (Iterator j = backend.getListeners(MessageEventListener.class); j.hasNext();)
						{
							((MessageEventListener) j.next()).requestComposing(message.getFrom(), message.getID(), message.getThread());
						}
						//main.requestComposing(message.getIdentifier(),message.getThread());
					}
				}
				
				
//				if (extension instanceof XConference)
//				{
//					final XConference xc =((XConference)extension);
//					Popups.OptionChoosed choose = new Popups.OptionChoosed()
//					{
//						public void optionChoosed(int option)
//						{
//							if (option == javax.swing.JOptionPane.NO_OPTION)
//							{
//								JID temp = xc.getRoom();  
//								JID jid = new JID(temp.getUser(),temp.getDomain(),backend.getMyJID().getUser());
//								backend.send(new Presence(jid,"available"));//,new XMUC()));
//								GroupchatWindow gcw = new GroupchatWindow(backend,jid);
//								gcw.show();
//							} 
//						}
//					};
//			
//					Object[] options = { "Decline", "Accept" };
//					Popups.showOptionDialog(
//						message.getFrom().getUser() + " invites you to " +  xc.getRoom(),
//						"Groupchat Invitation",
//						javax.swing.JOptionPane.YES_NO_OPTION,
//						javax.swing.JOptionPane.QUESTION_MESSAGE,
//						null,
//						options,
//						options[1],
//						choose);
//				}
			}
		}
	}

	//----------------------------------Presence----------------------------------------------------\\
	private void presence(Presence presence)
	{
		//presence for groupchat eg
		PresenceListener presenceListener = backend.getPresenceListener(presence.getFrom());
		if(presenceListener != null)
		{
			presenceListener.presenceChanged(presence); 
 			return;
		}
				
		String type = presence.getType();
		if (type.equals("available") || type.equals("unavailable"))
		{
			for (Iterator j = backend.getListeners(PresenceListener.class); j.hasNext();)
			{
				((PresenceListener) j.next()).presenceChanged(presence);
			}
		}
		else if ("subscribe".equals(type))
		{
			JID from = presence.getFrom();
			if (from.getUser() == null)
			{
				//server
				sendSubscribed(from, "subscribed", presence.getID());
				if (backend.getJIDStatus(presence.getFrom()) == null)
				{
					backend.send(new Presence(from, "subscribe"));
					IQXRoster roster = new IQXRoster(new RosterItem(from, null, null, "subscribe", null));
					backend.send(new InfoQuery("set", roster));
				}
			} else {
                fireOnSubscriptionRequestEvent(from, presence.getID());
            }
		} else if ("unsubscribed".equals(type)) {
            Popups.messagePopup(presence.getFrom()
                                + " unsuscribed you from his presence",
                                "Unsubscribed");
		} else if ("error".equals(type)) {
//            XMPPError er = presence.getXMPPError();
//            if (er==null) return;
//            er.getXMPPErrors()
            
            //if ("item-not-found".equals(xmpptype)) {
            if (presence.getErrorCode()==404) {
				String msg = MessageFormat.format(
                    I18N.gettext("main.error.User_{0}_could_not_be_found"),
                    new Object[]{presence.getFrom()});
                Popups.showMessageDialog(
                    msg, I18N.gettext("main.error.Subscription_error"),
                    JOptionPane.ERROR_MESSAGE);
                IQXRoster roster = new IQXRoster(
                    new RosterItem(presence.getFrom(), null, "remove",
                                   null, null));
                backend.send(new InfoQuery("set", roster));
            } else {
                Log.xmlReceivedError("Presence error "
                                     + presence.getErrorCode() + " "
                                     + presence.getErrorDescription()
                                     + " from " + presence.getFrom());
            }
		}
	}

	private void fireOnSubscriptionRequestEvent(final JID from, final String id)
	{

		JIDStatus jidStatus = backend.getJIDStatus(from);
		if (jidStatus != null)
		{
			Popups.OptionChoosed choose = new Popups.OptionChoosed()
			{
				public void optionChoosed(int option)
				{
					//System.out.println(option);
					String type = "subscribed";
					if (option == javax.swing.JOptionPane.YES_OPTION)
						type = "unsubscribed";
					sendSubscribed(from, type, id);
				}
			};

			Object[] options = { I18N.gettext("main.popup.Deny_subscription"), I18N.gettext("OK") };
			Popups.showOptionDialog(
				MessageFormat.format(I18N.gettext("main.popup.{0}_wants_to_subscribe_to_your_presence"),new Object[]{jidStatus.getNick()}),
				I18N.gettext("main.popup.Subscription_request"),
				javax.swing.JOptionPane.YES_NO_OPTION,
				javax.swing.JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1],
				choose);
		}
		else
		{
			Popups.OptionChoosed choose = new Popups.OptionChoosed()
			{
				public void optionChoosed(int option)
				{
					//System.out.println(option);
					String type = "subscribed";
					if (option == javax.swing.JOptionPane.NO_OPTION)
					{
						//add to roster  //frame to addcontact ipv null??
						new nu.fw.jeti.ui.AddContact(from, null, backend).show();
					}
					else
						type = "unsubscribed";
					sendSubscribed(from, type, id);

					//					no more no filed (distracts users)
					//					else if (option == javax.swing.JOptionPane.CANCEL_OPTION || option == javax.swing.JOptionPane.CLOSED_OPTION)
					//					{//add to not filed make different
					//					    //subscribe(from,null,"Friends");
					//						IQXRoster roster = new IQXRoster(new RosterItem(from,null,null,null,null));
					//						backend.send(new InfoQuery("set",roster));
					//					}
				}
			};
			Object[] options = { I18N.gettext("main.popup.Deny_subscription"), I18N.gettext("main.popup.Add_to_roster") };
			Popups.showOptionDialog(
				MessageFormat.format(I18N.gettext("main.popup.{0}_wants_to_subscribe_to_your_presence"),new Object[]{from}),
				I18N.gettext("main.popup.Subscription_request"),
				javax.swing.JOptionPane.YES_NO_OPTION,
				javax.swing.JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1],
				choose);
		}
	}

	private void sendSubscribed(JID from, String type, String id)
	{
		//id so need builder
		try
		{
			PresenceBuilder pb = new PresenceBuilder();
			pb.type = type;
			pb.setId(id);
			pb.setTo(from);
			backend.send(pb.build());
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
	}
	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */

