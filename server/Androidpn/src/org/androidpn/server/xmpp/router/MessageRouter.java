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
package org.androidpn.server.xmpp.router;

import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This class is to route Message packets to their corresponding handler.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class MessageRouter {
	private final Log log = LogFactory.getLog(getClass());
	private SessionManager sessionManager = SessionManager.getInstance();
	private UserService userService;

	/**
	 * Constucts a packet router.
	 */
	public MessageRouter() {
		userService = ServiceLocator.getUserService();
	}

	/**
	 * Routes the Message packet.
	 * many times the Router will give the packet to handlers, such as persenseUpdateHandler
	 * 
	 * @param packet the packet to route
	 */
	public void route(Message packet) {
		//@TODO  we must implements this...
		//below is copied from presenseRouter

		log.info("route packet");
		if (packet == null) {
			throw new NullPointerException();
		}
		ClientSession session = sessionManager.getSession(packet.getFrom());
		if (!isOL(packet.getFrom())) {
			log.info("session not on line or invalid");
			return;
		} else {
			//        	if(isOL(packet.getTo())){
			//        		log.info("forward a packet:"+packet.toXML());
			//				session.process(packet);
			//        	}
			//        	else{
			
			//confirm you have received
			log.info("create a reply iq to"+packet.getID());
			IQ iq=new IQ();
			iq.setID(packet.getID());
			iq.setType(IQ.Type.result);
			session.process(iq);
			
			if (isValid(packet.getTo().toString())) {
//				log.info("store a packet for future delivery to "
//						+ packet.getTo());
				//@todo
				session.process(packet);
			} else
				log.info("packet to User " + packet.getTo() + " is invalid");
		}
		//        }
	}

	private boolean isValid(String username) {
		if (username == null)
			return false;
		try {
			userService.getUserByUsername(username);
			return true;
		} catch (UserNotFoundException e) {
			return false;
		}
	}

	/*
	 * judge if the id is valid and online
	 */
	private boolean isOL(JID id) {
		ClientSession session = sessionManager.getSession(id);
		log.debug("session:"
				+ (session == null ? "null" : session.getStrStatus()));
		return (session != null && session.getStatus() == Session.STATUS_AUTHENTICATED);
	}

}
