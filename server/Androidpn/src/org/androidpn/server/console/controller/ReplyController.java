package org.androidpn.server.console.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.console.vo.UserReplyVO;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class ReplyController extends MultiActionController {

    public ReplyController() {
    }
    
    //自定义,从android获取用户的留言 
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String userName = ServletRequestUtils.getStringParameter(request, "androidName");
    	String userReply = ServletRequestUtils.getStringParameter(request, "reply");
    	String displayReply = userName+" 的留言："+userReply;
    	System.out.println(displayReply);
    	List<UserReplyVO> replyList = new ArrayList<UserReplyVO> ();
    	UserReplyVO ur = new UserReplyVO();
    	ur.setUserName(userName);
    	ur.setReplyMessage(userReply);
    	replyList.add(ur);
		ModelAndView mav = new ModelAndView();
	    mav.addObject("userReply",replyList);
	    mav.setViewName("reply/list");
	    return mav;
    }
    
    
}
