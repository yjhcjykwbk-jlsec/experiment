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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.console.vo.SubscriptionsVO;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class SubscriptionsController extends MultiActionController {

    private UserService userService;
    public SubscriptionsController() {
        userService = ServiceLocator.getUserService();
    }

    @SuppressWarnings("unchecked")
	public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
//    	int count_all=0;                  //�����0��
//        int count_news_yaowen=0;			 //�����1��
//        int count_pkusz_notification=0;   //�����2��
//        int count_video_schoolvideo=0;    //�����3��
//        int count_video_cievideo=0;       //�����4��
//        int count_video_hsbcvideo=0;      //�����5��
//        int count_video_stlvideo=0;       //�����6��
//        int count_video_renwenvideo=0;    //�����7��
//        int count_video_leisurevideo=0;   //�����8��
    	Map<String,Integer> subCnt=new HashMap<String,Integer>();
        List<User> userList = userService.getUsers();
        for (User user : userList) {        	
        	String subscriptions = user.getSubscriptions();
        	//System.out.println("���û���subscriptions�ǣ�"+subscriptions);
        	if(subscriptions!=null){
	        	String[] userSub = subscriptions.split(";");
	        	for(int i=0;i<userSub.length;i++) {
	        		if(userSub[i]==null||userSub[i].equals("")||userSub[i].equals("null")) continue;
	        		System.out.println(userSub[i]);
	        		if(subCnt.containsKey(userSub[i])){
	        			subCnt.put(userSub[i], subCnt.get(userSub[i])+1);
	        		}
	        		else subCnt.put(userSub[i], 1);
	        	}
        	}
        	
        }
//        System.out.println("����������Ŀ�Ĺ��У�"+count_all+" ��");
//        System.out.println("��������Ҫ����Ŀ�Ĺ��У�"+count_news_yaowen+" ��");
//        System.out.println("����֪ͨ������Ŀ�Ĺ��У�"+count_pkusz_notification+" ��");
//        System.out.println("����У԰��Ƶ��Ŀ�Ĺ��У�"+count_video_schoolvideo+" ��");
//        System.out.println("������ϢѧԺ��Ƶ��Ŀ�Ĺ��У�"+count_video_cievideo+" ��");
//        System.out.println("���Ļ����ѧԺ��Ƶ��Ŀ�Ĺ��У�"+count_video_hsbcvideo+" ��");
//        System.out.println("���Ĺ�ʷ�ѧԺ��Ƶ��Ŀ�Ĺ��У�"+count_video_stlvideo+" ��");
//        System.out.println("��������ѧԺ��Ƶ��Ŀ�Ĺ��У�"+count_video_renwenvideo+" ��");
//        System.out.println("����������Ƶ��Ŀ�Ĺ��У�"+count_video_leisurevideo+" ��");

        List<SubscriptionsVO> subscriptionsList = new ArrayList<SubscriptionsVO>();
        for(Map.Entry<String, Integer>entry:subCnt.entrySet()){
        	System.out.println("����"+entry.getKey()+"���У�"+entry.getValue()+"��");
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
      
    
}
