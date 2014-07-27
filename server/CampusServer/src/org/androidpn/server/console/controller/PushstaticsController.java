package org.androidpn.server.console.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.console.vo.PushStaticsVO;
import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class PushstaticsController extends MultiActionController {

	private NotificationService notificationService;
	private List<NotificationMO> subNotificationList;	
	public List<NotificationMO> notificationList;
	private int pageTotal;
    public PushstaticsController() {
    	notificationService = ServiceLocator.getNotificationService();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	
    	notificationList = notificationService.getNotifications();
    	
    	 String pageNumberStr = request.getParameter("pageNumber");  
    	    int pageNum = 1;   

    	    if(pageNumberStr!=null && !pageNumberStr.isEmpty())  
    	    {  
    	        pageNum = Integer.parseInt(pageNumberStr);  
    	    }  
   	    
    	    int pageSize = 90; 
    	    
    		List<PushStaticsVO> staticsList = getPushstaticsList(pageNum, pageSize); 
    		int pageTotal = getPageTotal();
            ModelAndView mav = new ModelAndView();
            mav.addObject("pushstaticsList", staticsList);
            mav.addObject("pageNumber", pageNum); 
            mav.addObject("pageTotal", pageTotal);
            mav.setViewName("pushstatics/list");
            return mav;
    		}
    	
	public List<PushStaticsVO> getPushstaticsList(int pageNum, int pageSize) {
		System.out.println(pageNum+";"+pageSize);
    	List<PushStaticsVO> staticsList = new ArrayList<PushStaticsVO>();
		int start = (pageNum - 1) * pageSize;
		int end = start + pageSize;
		System.out.println(start+"~~~~"+end);
		if(start<notificationList.size()&&end<notificationList.size()){
			subNotificationList = notificationList.subList(start,end);
		}

		int messageCount=0;
    	String messageId0 = "";
    	String messageId1;
    	Date messageCreateDate;
    	String messageTitle;
    	int count_target,count_notsend,count_send,count_receive,count_view;
    	if(subNotificationList.size()>0){
    		pageTotal = pageNum+1;
    		for (NotificationMO subNotification : subNotificationList){    		
        		messageId1 = subNotification.getMessageId();    
        		
        		if(messageId1.equals(messageId0)){
        			continue;
        		}
        		else{
        			messageCount++;  
        			messageTitle = subNotification.getTitle();
        			messageCreateDate = subNotification.getCreateTime();
        			PushStaticsVO vo = new PushStaticsVO();
        			vo.setStaticsId(messageCount);  
        			vo.setMessageId(messageId1);
        			vo.setMessageCreateDate(messageCreateDate);
        			vo.setMessageTitle(messageTitle);
        			count_notsend = notificationService.queryCountByStatus("0", messageId1);
        			vo.setCount_notsend(count_notsend);    			        		
        			count_send = notificationService.queryCountByStatus("1", messageId1);
            		vo.setCount_send(count_send);
            		count_receive = notificationService.queryCountByStatus("2", messageId1);
            		vo.setCount_receive(count_receive);
            		count_view = notificationService.queryCountByStatus("3", messageId1);
            		vo.setCount_view(count_view);
            		count_target = count_notsend+count_send+count_receive+count_view;
            		vo.setCount_target(count_target);
            		staticsList.add(vo);  
            		messageId0 = messageId1;  
        		}
        	}  
    	}
    	
		return staticsList;	
	}
	
	public int getPageTotal(){
		return pageTotal;
	}
    
    
    }

