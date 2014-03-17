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
package org.androidpn.server.console.controller;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.presence.PresenceManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.androidpn.server.util.*;
/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class UserController extends MultiActionController {

    private UserService userService;

    public UserController() {
        userService = ServiceLocator.getUserService();
    }
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PresenceManager presenceManager = new PresenceManager();
        List<User> userList = userService.getUsers();
        for (User user : userList) {
            if (presenceManager.isAvailable(user)) {
                // Presence presence = presenceManager.getPresence(user);
                user.setOnline(true);
            } else {
                user.setOnline(false);
            }
            // logger.debug("user.online=" + user.isOnline());
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userList);
        mav.setViewName("user/list");
        return mav;
    }
    private ModelAndView strView(String name,String value){
    	 ModelAndView mav = new ModelAndView();
         mav.addObject(name, "<xml>"+value+"</xml>");
         mav.setViewName("xml");
         return mav;
    }
    public ModelAndView listFriend(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
    	String idStr=request.getParameter("id");
    	
    	if(idStr==null||userService.getUser(idStr)==null) 
    		return strView("result",
				"<result>failed</result>" +
    			"<errno>1</errno><reason>id not valid</reason>");
    	int id=Integer.parseInt(idStr);
		 	List<User> userList = userService.getFriends(id);
		 	Xmler.getInstance().alias("user",User.class);
		 	return strView("result",
	 			"<result>succeed</result>"+
				""+Xmler.getInstance().toXML(userList)+"");
    }
    
    public ModelAndView getUser(HttpServletRequest request,
    		HttpServletResponse response) {
		String username=request.getParameter("username");
		try{
			User u=userService.getUserByUsername(username);
		 	Xmler.getInstance().alias("user",User.class);
			return strView("result",
				"<result>succeed</result>"+Xmler.getInstance().toXML(u)+"");
		}catch(Exception e){
			return strView("result",
				"<result>failed</result>");
			}
		}
    
    public ModelAndView addFriend(HttpServletRequest request,
    		HttpServletResponse response) throws Exception {
		String idStr1=request.getParameter("id1"), //id1 is yourself
			idStr2=request.getParameter("id2");
		
		if(idStr1==null||idStr2==null) 
			return strView("result",
				"<result>failed</result>"+
				"<errno>1</errno><reason>id1:"+idStr1+" or id2:"+idStr2+" not set</reason>");
		if(userService.getUser(idStr2)==null) 
			return strView("result",
				"<result>failed</result>"+
				"<errno>2</errno><reason>the target friend:"+idStr2+" not valid</reason>");
		Integer id1=Integer.parseInt(idStr1);
		Integer id2=Integer.parseInt(idStr2);
		if(id1==null||id2==null) 
			return strView("result",
				"<result>failed</result>"+
				"<errno>3</errno><reason>id1:"+idStr1+" or id2:"+idStr2+" not valid</reason>");
		userService.addFriend(id1,id2);
		return strView("result",
				"<result>succeed</result>");
    }
    
    
  //�Զ���,��http://push.pkusz.edu.cn��ȡ���û�������룬�����浽��ݿ�    
    public ModelAndView saveUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String userName = ServletRequestUtils.getStringParameter(request, "userName");
    	String userPWD = ServletRequestUtils.getStringParameter(request, "userPWD");
    	String userEmail = ServletRequestUtils.getStringParameter(request, "userEmail");
    	System.out.println("saveUser#new username"+userName);
    	System.out.println("saveUser#new pwd"+userPWD);
    	
    	try{
    		User us = userService.getUserByUsername(userName);
    		us.setUsername(userName);
	        us.setPassword(userPWD);
	        us.setEmail(userEmail);
	        us.setName(userName);
	        userService.saveUser(us);
    	}catch(UserNotFoundException eee){  
    		User newUser = new User();
	        newUser.setUsername(userName);
	        newUser.setPassword(userPWD);
	        newUser.setEmail(userEmail);
	        newUser.setName(userName);
	        userService.saveUser(newUser);
    	}   	
    	
    	return null;
    }

    public ModelAndView checkUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String androidName = ServletRequestUtils.getStringParameter(request, "androidName");
    	String androidPwd = ServletRequestUtils.getStringParameter(request, "androidPwd");
    	System.out.println("checkUser#androidName:"+androidName);
    	System.out.println("checkUser#androidPwd:"+androidPwd);
    	
		ServletOutputStream out = response.getOutputStream();
    	try{
    		User us = userService.getUserByUsername(androidName);
    		if(us.getPassword().equalsIgnoreCase(androidPwd)){
    			response.setContentType("text/plain");
    			out.print("check:success");
    			out.flush();
    		}
    		else{
    			response.setContentType("text/plain");
    			out.print("check:password failure");
    			out.flush();
    		}
    	}catch(UserNotFoundException e){
			response.setContentType("text/plain");
			out.print("check:not exist");
			out.flush();
    	}
        
    	return null;
    }
    
    public ModelAndView getSubscription(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String userName = ServletRequestUtils.getStringParameter(request, "userName");
		ServletOutputStream out = response.getOutputStream();
    	try{
    		User us = userService.getUserByUsername(userName);
    		response.setContentType("text/plain");
			out.print(us.getSubscriptions());  
			out.flush();
    	}catch(UserNotFoundException e){
    		System.out.println("getSubScription#exception:"+userName+" not found");
			response.setContentType("text/plain");
			out.print("check:not exist");
			out.flush();
    	}

    	return null;
    }
    
}
