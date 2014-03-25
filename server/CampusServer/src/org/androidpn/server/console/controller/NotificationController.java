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

import java.util.Calendar;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.push.NotificationManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
/** 
 * A controller class to process the notification related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationController extends MultiActionController {
    private NotificationManager notificationManager;
    public static long timeStart;

    private UserService userService;
    
    public NotificationController() {
        notificationManager = new NotificationManager();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        // mav.addObject("list", null);
        mav.setViewName("notification/form");
        return mav;
    }

    
    
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	System.out.println("notification get====");
    	String subscriber = ServletRequestUtils.getStringParameter(request, "subscriber");  
    	String subscriptions = ServletRequestUtils.getStringParameter(request, "subscriptions");  
    	String apiKey = Config.getString("apiKey", "");
    	
    	ServletOutputStream out = response.getOutputStream();
    	
        userService = ServiceLocator.getUserService();
        try{
	        User us = userService.getUserByUsername(subscriber);
	        us.setSubscriptions(subscriptions);
	        userService.saveUser(us);
	        response.setContentType("text/plain");
			out.print("subscribe:success");  
			out.flush();
        }catch(UserNotFoundException e){
        	response.setContentType("text/plain");
			out.print("subscribe:failure");  
			out.flush();
        }catch(UserExistsException e){
        	response.setContentType("text/plain");
			out.print("subscribe:failure"); 
			out.flush();
        }
    	
        /*
    	ModelAndView mav0 = new ModelAndView();
        mav0.setViewName("redirect:notification.do");
        return mav0;
        */
        return null;
    	
    }    
    
    
    public ModelAndView send(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String apiKey = Config.getString("apiKey", "");

	        /**
	         */
	        System.out.println("NotificationController.send#"+request.getCharacterEncoding());  // UTF-8
	        if(request.getParameter("chat")!=null){
	        	//handle with a chat request
	        	String fromUsername="",
	        			toUsername=request.getParameter("toUsername"),
	        			msg=request.getParameter("message"),
	        			time=Calendar.getInstance().getTime().toString();
	        	System.out.println(toUsername+":"+msg);
	        	if(fromUsername==null||toUsername==null||msg==null) {
	        		return null;
	        	}
	        	notificationManager.sendMessage(apiKey, fromUsername, toUsername, msg, time);
	        	return null;
	        }
	        String feedTitle = ServletRequestUtils.getStringParameter(request, "feedTitle");
	        String feedContent = ServletRequestUtils.getStringParameter(request, "feedContent");
	        String feedLink = ServletRequestUtils.getStringParameter(request, "feedLink");
	        System.out.println("---------------------feedTitle--------------------:"+feedTitle);
	        System.out.println("---------------------feedContent------------------:"+feedContent);
	        System.out.println("---------------------feedLink------------------:"+feedLink);
	     
	        
	        // http://xx.xx.xx.xx/videoMonitor/monitor_laboratory/xxxx
	        if(feedLink.contains("videoMonitor")){
	        	//timeStart = System.currentTimeMillis();
        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "monitor_all"); 
        		response.setContentType("text/plain");
        		ServletOutputStream out = response.getOutputStream();
        		out.print("getNotice:success");  
    			out.flush();
	        }
	       
	        else if(feedLink.contains("push.pkusz.edu.cn")){
	        	/*if(feedLink.contains("videocourseware")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_videocourseware"); 
	        	}
	        	else
	        	*/ if(feedLink.contains("leisurevideo")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_leisurevideo"); 
	        	}
	        	else if(feedLink.contains("schoolvideo")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_schoolvideo"); 
	        	}
	        	else if(feedLink.contains("cievideo")){ 
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_cievideo"); 
	        	}
	        	else if(feedLink.contains("hsbcvideo")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_hsbcvideo"); 
	        	}
	        	else if(feedLink.contains("stlvideo")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_stlvideo"); 
	        	}
	        	else if(feedLink.contains("renwenvideo")){  
	        		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "video_renwenvideo"); 
	        	}
	        }
	              
	        /*
	    	else if(feedLink.contains("english.pkusz.edu.cn")){
	    			if(feedLink.contains("News&Bulletin")){ // News&Bulletin
	    				notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "english_news");
	    			}
	    	}
	    	*/
	    	
	    	else if(feedLink.contains("news.pkusz.edu.cn")){
	    			if(feedLink.contains("news")){  
	    				notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "news_yaowen");
	    			}
	    			/*
	    			else if(feedLink.contains("ר 
	    	    		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "news_zhuanti");
	    	    	}
	    			else if(feedLink.contains(" 
	    	    		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "news_renwu");
	    	    	}
	    			else if(feedLink.contains(" 
	    	    		notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "news_dianshi");
	    	    	}
	    	    	*/
	    	}
	    	
	    	else if(feedLink.contains("www.pkusz.edu.cn")){
	    		if(feedLink.contains("֪ͨnoti")){  
	    			notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, "pkusz_notification");
	    		}
	    	}            
	    	else {
	    		String feedSection = ServletRequestUtils.getStringParameter(request, "feedSection");
	    		if(feedSection!=null&&feedSection!="")
	    			notificationManager.sendMyNotifications(apiKey, feedTitle, feedContent, feedLink, feedSection);
	    		else{
	    			System.out.println("null feedsection");
	    		}
	    	}
    	return null;
    }

    public ModelAndView admin_send(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String apiKey = Config.getString("apiKey", "");

    	String broadcast = ServletRequestUtils.getStringParameter(request,"broadcast");
    	/**
    	 */
    	System.out.println(" broadcast "+broadcast);
    	if(broadcast!=null){
	        String username = ServletRequestUtils.getStringParameter(request,"username");
	        String title = ServletRequestUtils.getStringParameter(request, "title");
	        String message = ServletRequestUtils.getStringParameter(request, "message");
	        String uri = ServletRequestUtils.getStringParameter(request, "uri");
	        
	        if (broadcast.equalsIgnoreCase("Y")) {	        	
	        	timeStart = System.currentTimeMillis();
	        	
	            notificationManager.sendBroadcast(apiKey, title, message, uri);
	        }else if (broadcast.equalsIgnoreCase("A")) {
	            notificationManager.sendAllBroadcast(apiKey, title, message, uri);
	        }else if (broadcast.equalsIgnoreCase("N")){	        	
	            notificationManager.sendNotifications(apiKey, username, title, message, uri);
	        }
    	}
    	
    	ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:notification.do");
        return mav;
        
    }
}
