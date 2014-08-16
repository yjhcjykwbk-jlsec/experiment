/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import java.io.InputStream;
import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.androidpn.server.console.controller.NotificationController;
import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.model.User;
import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.CopyMessageUtil;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.hsqldb.lib.StringInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;

/**
 * This class is to manage sending the notifcations to the users.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationManager {

	private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

	private final Log log = LogFactory.getLog(getClass());

	private SessionManager sessionManager;
	
	private NotificationService notificationService;
	
	private UserService userService;
	
	public static int sessionCounter=0;
	public static long difTime=0;
	/**@deprecated
	 * xuzhigang
	 * @return
	 */
	private static XmlPullParser getParser(){
		XmlPullParser parser=null;
		 try {
	            parser = XmlPullParserFactory.newInstance().newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
	            //parser.setInput(connection.reader);
	        }
	        catch (XmlPullParserException xppe) {
	            xppe.printStackTrace();
	            return null;
	        }
	        return parser;
	}
	/**
	 * Constructor.
	 */
	public NotificationManager() {
		sessionManager = SessionManager.getInstance();
		notificationService = ServiceLocator.getNotificationService();
		userService = ServiceLocator.getUserService();
	}
	
	static NotificationManager instance=null;
	/**@deprecated
	 * 
	 * @return
	 */
	public static NotificationManager getInstance(){
		if(instance==null) {
			instance=new NotificationManager();
		}
		return instance;
	}
	/**
	 * send a xml message to user
	 * 
	 * @param apiKey
	 *            the API key
	 * @param title
	 *            the title
	 * @param message
	 *            the message details
	 * @param uri
	 *            the uri
	 */
	public void sendMessage(String apiKey, String fromUsername, 
			String toUsername, String message , String time) {
		log.debug("sendMSG()...");
		IQ notificationIQ = createNotificationIM(apiKey, fromUsername, message,time);
		ClientSession session = sessionManager.getSession(toUsername);
		NotificationMO notificationMO = new NotificationMO(apiKey, "",
				message, "chat");
		try {
			notificationMO.setUsername(session.getUsername());
			notificationMO.setClientIp(session.getHostAddress());
			notificationMO.setResource(session.getAddress().getResource());
			sessionCounter++;
		} catch (Exception e) {
			e.printStackTrace();
		}
		CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);
	
		if (session.getPresence().isAvailable()) {
			notificationMO.setStatus(NotificationMO.STATUS_SEND);
			notificationIQ.setTo(session.getAddress());
			session.deliver(notificationIQ);
		} else { 
			notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
		}
	
		try{
			notificationService.saveNotification(notificationMO);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!");
		}
		
	}
	
	/**
	 * @author xzg
	 * @param toUsername
	 * @param message
	 * store chat message in the form of notification 
	 */
	public void storeChatMsg(Message msg){
		String toUsername=msg.getTo()+"";
		NotificationMO notificationMO = new NotificationMO("chat", msg.getFrom()+"",
				msg.getBody(), msg.getID());
		notificationMO.setUsername(toUsername);
		notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);//not send
		try{
			notificationService.saveNotification(notificationMO);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!");
		}
	}
	
	/**
	 * @author xuzhigang
	 * @param toUsername
	 * @param message
	 * store chat message in the form of notification 
	 */
	public void resendChatMsg( NotificationMO notificationMO){
//		NotificationMO notificationMO = new NotificationMO("", "",
//				msg.toXML(), "chat");
		Message msg = new Message();
		msg.setFrom(notificationMO.getTitle());
		msg.setBody(notificationMO.getMessage());
		msg.setID(notificationMO.getUri());
		String toUsername = notificationMO.getUsername();
		msg.setTo(notificationMO.getUsername());
		ClientSession session = sessionManager.getSession(toUsername);
		if (session!=null&&session.getPresence().isAvailable()) {
			msg.setTo(toUsername);
			log.info("notificationMgr.resendChatMsg:session of user "+session.getAddress()+" sending...");
			session.process(msg);
			notificationMO.setStatus(NotificationMO.STATUS_SEND);
		}else {
			log.debug("notificationMgr.resendChatMsg:session of user "+toUsername+" not available");
		}
		try {
			notificationService.saveNotification(notificationMO);
		} catch (Exception e) {
			log.warn(" notifications insert to database failure!!");
		}
	}
	/**
	 * Broadcasts a newly created notification message to all connected users.
	 * 
	 * @param apiKey
	 *            the API key
	 * @param title
	 *            the title
	 * @param message
	 *            the message details
	 * @param uri
	 *            the uri
	 */
	public void sendBroadcast(String apiKey, String title, String message,
			String uri) {
		log.debug("sendBroadcast()...");
		List<NotificationMO> notificationMOs = new ArrayList<NotificationMO>();
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);

			for (ClientSession session : sessionManager.getSessions()) {
				NotificationMO notificationMO = new NotificationMO(apiKey, title,
						message, uri);
				try {
					notificationMO.setUsername(session.getUsername());
					notificationMO.setClientIp(session.getHostAddress());
					notificationMO.setResource(session.getAddress().getResource());
					sessionCounter++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);
	
				if (session.getPresence().isAvailable()) {
					notificationMO.setStatus(NotificationMO.STATUS_SEND);
					notificationIQ.setTo(session.getAddress());
					session.deliver(notificationIQ);
				} else { 
					notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
				}
				notificationMOs.add(notificationMO);
			} // for session 

	
			try{
				notificationService.createNotifications(notificationMOs);
			}catch(Exception e){
				log.warn(" notifications insert to database failure!!");
			}

	}
	
	public void sendAllBroadcast(String apiKey, String title, String message,
			String uri) {
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
		List<User> list = userService.listUsers();
		for (User user : list) {
			this.sendNotificationToUser(apiKey, user.getUsername(), title, message, uri,notificationIQ);
		}
		
	}
	
	public void sendMyNotifications(String apiKey, String title, String message, String uri, String subscription){
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
		try {
			List<User> list = userService.getSubscribeUsers(subscription);
			System.out.println("attention ,below users:"+list);
			for(User user : list){
				this.sendNotificationToUser(apiKey, user.getUsername(), title, message, uri,notificationIQ);
			}
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sends a newly created notification message to the specific user.
	 *   client
	 * @param apiKey
	 *            the API key
	 * @param title
	 *            the title
	 * @param message
	 *            the message details
	 * @param uri
	 *            the uri
	 */
	public void sendNotificationToUser(String apiKey, String username,
			String title, String message, String uri, IQ notificationIQ) {
		log.debug("sendNotifcationToUser()...");
		ClientSession session = sessionManager.getSession(username);
		NotificationMO notificationMO = new NotificationMO(apiKey, title,
				message, uri);
		notificationMO.setUsername(username);
		CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);
		if (session != null && session.getPresence().isAvailable()) {
			notificationIQ.setTo(session.getAddress());
			session.deliver(notificationIQ);
			notificationMO.setStatus(NotificationMO.STATUS_SEND);
			try {
				notificationMO.setClientIp(session.getHostAddress());
				notificationMO.setResource(session.getAddress().getResource());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
			log.info("notificationmgr.sendnotificationtouser: user not available");
		}
		try{
			notificationService.saveNotification(notificationMO);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!");
		}
	}
	/**
	 * xuzhigang
	 * @param notificationMO
	 * @param notificationIQ
	 * @param username
	 */
	public void resendNotificationToUser(NotificationMO notificationMO, IQ notificationIQ, String username) {
		log.debug("sendNotifcationToUser()...");
		ClientSession session = sessionManager.getSession(username);
		CopyMessageUtil.IQ2Message(notificationIQ, notificationMO);
		if (session != null && session.getPresence().isAvailable()) {
			notificationIQ.setTo(session.getAddress());
			session.deliver(notificationIQ);
			notificationMO.setStatus(NotificationMO.STATUS_SEND);
			try {
				notificationMO.setClientIp(session.getHostAddress());
				notificationMO.setResource(session.getAddress().getResource());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			notificationMO.setStatus(NotificationMO.STATUS_NOT_SEND);
			log.info("notificationmgr.sendnotificationtouser: user not available");
		}
		try{
			notificationService.saveNotification(notificationMO);
		}catch(Exception e){
			log.warn(" notifications insert to database failure!!");
		}
	}
	/**
	 * resend messages to user
	 * @author xuzhigang
	 */
	public void resendNotifications(String username){
		List<NotificationMO> notes=notificationService.getUnsentNotifications(username);
		if(notes==null) return;
		int i = 0;
		for (NotificationMO note : notes) {

			if (note.getApiKey().equals("chat")) {
				log.info("resendNOtifications: send chat msg to" +username);
				this.resendChatMsg(note);
			} else {
				IQ notificationIQ = createNotificationIQ(note.getApiKey(), note
						.getTitle(), note.getMessage(), note.getUri());
				// attention: note has an id, and this will update
				// notificationMO note, not recreate a note
				this.resendNotificationToUser(note, notificationIQ, username);
			}
			i++;
			if (i > 15) {
				// every time only push no more than 15 old messages, and use
				// many times to send all
				return;
			}
		}
	}
	/**
	 * Creates a new notification IQ and returns it.
	 */
	public IQ createNotificationIQ(String apiKey, String title,
			String message, String uri) {
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());
		// String id = String.valueOf(System.currentTimeMillis());

		Element notification = DocumentHelper.createElement(QName.get(
				"notification", NOTIFICATION_NAMESPACE));
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText(apiKey);
		notification.addElement("title").setText(title);
		notification.addElement("message").setText(message);
		notification.addElement("uri").setText(uri);
		notification.addElement("type").setText("note");//@notification 
		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);

		return iq;
	}
	
	/**
	 * Creates a new notification IQ and returns it.
	 */
	public IQ createNotificationIM(String apiKey, String fromUser,
			String message, String time) {
		Random random = new Random();
		String id = Integer.toHexString(random.nextInt());
		// String id = String.valueOf(System.currentTimeMillis());

		Element notification = DocumentHelper.createElement(QName.get(
				"notification", NOTIFICATION_NAMESPACE));
		notification.addElement("id").setText(id);
		notification.addElement("apiKey").setText(apiKey);
		notification.addElement("message").setText(message);
		notification.addElement("fromUser").setText(fromUser);
		notification.addElement("time").setText(time);
		notification.addElement("type").setText("chat");//@chat
		IQ iq = new IQ();
		iq.setType(IQ.Type.set);
		iq.setChildElement(notification);

		return iq;
	}
	
	public void sendNotifications(String apiKey, String username,
			String title, String message, String uri){
		IQ notificationIQ = createNotificationIQ(apiKey, title, message, uri);
		if(username.indexOf(";")!=-1){
			String[] users = username.split(";");
			for (String user : users) {
				this.sendNotificationToUser(apiKey, user, title, message, uri,notificationIQ);
			}
		}else{
			this.sendNotificationToUser(apiKey, username, title, message, uri,notificationIQ);
		}
	}
	
	public void sendOfflineNotification(NotificationMO notificationMO ) {
		log.debug("sendOfflineNotifcation()...");
		IQ notificationIQ = createNotificationIQ(notificationMO.getApiKey(), notificationMO.getTitle(), notificationMO.getMessage(), notificationMO.getUri());
		notificationIQ.setID(notificationMO.getMessageId());
		ClientSession session = sessionManager.getSession(notificationMO.getUsername());
		if (session != null && session.getPresence().isAvailable()) {
			notificationIQ.setTo(session.getAddress());
			session.deliver(notificationIQ);
			try{
				notificationMO.setStatus(NotificationMO.STATUS_SEND);
				//IP
				notificationMO.setClientIp(session.getHostAddress());
				notificationMO.setResource(session.getAddress().getResource());
				notificationService.updateNotification(notificationMO);
			}catch (Exception e) {
				log.warn(" update notification status failure !");
			}
		}
	}
}
