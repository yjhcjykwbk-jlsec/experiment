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
package org.androidpn.server.xmpp.handler;

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Roster;
import org.xmpp.packet.Roster.Subscription;
import java.util.List;
/** 
 * This class is to handle the TYPE_IQ jabber:iq:roster protocol.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class IQRosterHandler extends IQHandler {
    
    private static final String NAMESPACE = "jabber:iq:roster";
    protected final Log log = LogFactory.getLog(getClass());
    private UserService userService;
    /**
     * Constructor.
     */
    public IQRosterHandler() {     
    	userService=ServiceLocator.getUserService();
    }

    /**
     * Handles the received IQ packet.
     * 
     * @param packet the packet
     * @return the response to send back
     * @throws UnauthorizedException if the user is not authorized
     */
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        // TODO
    	IQ reply=null;
    	log.info("handleIQ");
    	 ClientSession session = sessionManager.getSession(packet.getFrom());
    	 if(session==null||session.getStatus()!= Session.STATUS_AUTHENTICATED){
    		  log.error("Session not valid: did you login ? " + packet.getFrom());
              reply = IQ.createResultIQ(packet);
              reply.setChildElement(packet.getChildElement().createCopy());
              reply.setError(PacketError.Condition.internal_server_error);
              reply.setChildElement("errno","1");
              return reply;
    	 }
    	 
    	 //获取联系人
    	 if (IQ.Type.get.equals(packet.getType())) {
    		 log.info("getting roster");
             Roster rost=new Roster();
             rost.setID(packet.getID());
             rost.setType(IQ.Type.result);
             String username=packet.getFrom()+"";
             username=username.substring(0,username.indexOf('@'));
             List<User> friends = userService.getFriends(username);
             if(friends!=null){
	             for (User u : friends){
	            	 String s=u.getName();
	            	 rost.addItem(s, Subscription.from);
	             }
             }
             return reply;
         } 
    	 
    	 //设置联系人
    	 else {
    		 log.debug("iq type not supported yet:"+packet.getType());
    		 reply = IQ.createResultIQ(packet);
             reply.setChildElement(packet.getChildElement().createCopy());
             reply.setChildElement("errno","2");
             return reply;
    	 }
//    	 else if (IQ.Type.set.equals(packet.getType())) {
//             try {
//                 Element query = packet.getChildElement();
//                 if (query.element("remove") != null) {
//                     if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
//                         // TODO
//                     } else {
//                         throw new UnauthorizedException();
//                     }
//                 } else {
//                     String username = query.elementText("username");
//                     String password = query.elementText("password");
//                     String email = query.elementText("email");
//                     String name = query.elementText("name");
//
//                     // Verify the username
//                     if (username != null) {
//                         Stringprep.nodeprep(username);
//                     }
//
//                     // Deny registration of users with no password
//                     if (password == null || password.trim().length() == 0) {
//                         reply = IQ.createResultIQ(packet);
//                         reply.setChildElement(packet.getChildElement()
//                                 .createCopy());
//                         reply.setError(PacketError.Condition.not_acceptable);
//                         return reply;
//                     }
//
//                     if (email != null && email.matches("\\s*")) {
//                         email = null;
//                     }
//
//                     if (name != null && name.matches("\\s*")) {
//                         name = null;
//                     }
//
//                     User user;
//                     if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
//                         user = userService.getUser(session.getUsername());
//                     } else {
//                         user = new User();
//                     }
//                     user.setUsername(username);
//                     user.setPassword(password);
//                     user.setEmail(email);
//                     user.setName(name);
//                     userService.saveUser(user);
//
//                     reply = IQ.createResultIQ(packet);
//                 }
//             } catch (Exception ex) {
//                 log.error(ex);
//                 reply = IQ.createResultIQ(packet);
//                 reply.setChildElement(packet.getChildElement().createCopy());
//                 if (ex instanceof UserExistsException) {
//                     reply.setError(PacketError.Condition.conflict);
//                 } else if (ex instanceof UserNotFoundException) {
//                     reply.setError(PacketError.Condition.bad_request);
//                 } else if (ex instanceof StringprepException) {
//                     reply.setError(PacketError.Condition.jid_malformed);
//                 } else if (ex instanceof IllegalArgumentException) {
//                     reply.setError(PacketError.Condition.not_acceptable);
//                 } else {
//                     reply.setError(PacketError.Condition.internal_server_error);
//                 }
//             }
//         }
//
//         // Send the response directly to the session
//         if (reply != null) {
//             session.process(reply);
//         }
//         return null;
//        return null;
    }
    
    /**
     * Returns the namespace of the handler.
     * 
     * @return the namespace
     */
    public String getNamespace() {
        return NAMESPACE;
    }

}
