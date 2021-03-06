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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.console.vo.SubscriptionsVO;
import org.androidpn.server.service.*;
import org.androidpn.server.util.Config;
import org.androidpn.server.util.Xmler;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.androidpn.server.model.App;
/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class SubscriptionsController extends MultiActionController {

    private UserService userService;
    private AppService appService;
    public SubscriptionsController() {
        userService = ServiceLocator.getUserService();
        appService = ServiceLocator.getAppService();
    }

    @SuppressWarnings("unchecked")
	public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
//    	int count_all=0;                 
//        int count_news_yaowen=0;			 
//        int count_pkusz_notification=0;    
//        int count_video_schoolvideo=0;    
//        int count_video_cievideo=0;   
//        int count_video_hsbcvideo=0;      
//        int count_video_stlvideo=0;      
//        int count_video_renwenvideo=0;     
//        int count_video_leisurevideo=0;    
    	Map<String,Integer> subCnt=new HashMap<String,Integer>();
        List<User> userList = userService.listUsers();
        for (User user : userList) {        	
        	List<App> userSubs = userService.getUserSubscribes(user.getId());//user.getSubscriptions();
        	if(!userSubs.isEmpty()){
	        	for(App app:userSubs) {
	        		String sub=app.getName();
	        		if(sub==null||sub=="") continue;
	        		System.out.println(sub+"");
	        		if(subCnt.containsKey(sub)){
	        			subCnt.put(sub, subCnt.get(sub)+1);
	        		}
	        		else subCnt.put(sub, 1);
	        	}
        	}
        	
        }

        List<SubscriptionsVO> subscriptionsList = new ArrayList<SubscriptionsVO>();
        for(Map.Entry<String, Integer>entry:subCnt.entrySet()){
        	 SubscriptionsVO vo = new SubscriptionsVO();
        	 vo.setSubscriptionName(entry.getKey());
        	 vo.setCount(entry.getValue());
        	 subscriptionsList.add(vo);
        }
        Collections.sort(subscriptionsList);
        
        ModelAndView mav = new ModelAndView();
        mav.addObject("subscriptionsList", subscriptionsList);
        mav.setViewName("subscriptions/list");
        return mav;
    }
   
    
    /**
     * 增加订阅的http接口
     * subscriptions.do(action=addSubscribe,username,appid) 
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author xu
     */
    public ModelAndView addSubscribe(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	System.out.println("notification get====");
    	String userName = ServletRequestUtils.getStringParameter(request, "username");  
    	Long appId = new Long(ServletRequestUtils.getStringParameter(request, "appid"));  
    	String apiKey = Config.getString("apiKey", "");
    	
    	ServletOutputStream out = response.getOutputStream();
    	
        userService = ServiceLocator.getUserService();
        try{
	        User us = userService.getUserByUsername(userName);      
	        userService.addSubscribe(us.getId(), appId);
	        response.setContentType("text/plain");
			out.print("subscribe:success");  
			out.flush();
        }catch(UserNotFoundException e){
        	response.setContentType("text/plain");
			out.print("subscribe:failure");  
			out.flush();
        } 
        return null;
    }
    
    /**
     * 取消订阅的http接口
     * subscriptions.do(action=delSubscribe,username,appid) 
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @author xu
     */
    public ModelAndView delSubscribe(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	System.out.println("notification get====");
    	String userName = ServletRequestUtils.getStringParameter(request, "username");  
    	Long appId = new Long(ServletRequestUtils.getStringParameter(request, "appid"));  
    	String apiKey = Config.getString("apiKey", "");
    	
    	ServletOutputStream out = response.getOutputStream();
    	
        userService = ServiceLocator.getUserService();
        try{
	        User us = userService.getUserByUsername(userName);      
	        userService.delSubscribe(us.getId(), appId);
	        response.setContentType("text/plain");
			out.print("unsubscribe:success");  
			out.flush();
        }catch(UserNotFoundException e){
        	response.setContentType("text/plain");
			out.print("unsubscribe:failure");  
			out.flush();
        } 
        return null;
    }    
    
    
    /**
     * 获取所有可订阅应用
     * subscriptions.do(action=listApps) 
     */
    public ModelAndView listApps(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	System.out.println("notification get====");
    	String apiKey = Config.getString("apiKey", "");
    	
    	//ServletOutputStream out = response.getOutputStream();
    	PrintWriter out=response.getWriter();//OutputStream();
    	List<App> apps=appService.listApps();
    	
    	response.setContentType("text/plain");
    	
    	if(apps==null){
    		out.print("<result>failed</result>");
    		out.flush();
    	}
    	else{ 
    		String strApps="<xml><result>succeed</result>"+
    				""+Xmler.getInstance().toXML(apps)+"</xml>";
    		System.out.println(strApps);
    		out.print(strApps);
    		out.flush();
    	}
    	return null;
    }    
    
}
