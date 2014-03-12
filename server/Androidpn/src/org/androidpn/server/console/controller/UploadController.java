package org.androidpn.server.console.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.console.vo.UserReplyVO;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import org.apache.struts2.ServletActionContext;
//import org.apache.struts.

/** 
 * A controller class to process the user related requests.  
 *
 * @author xiaobingo
 */
public class UploadController extends MultiActionController {

    public UploadController() {
    }
    
    public ModelAndView getUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	System.out.println("请求的字符编码是："+request.getCharacterEncoding());  // UTF-8
		String uploader0 = ServletRequestUtils.getStringParameter(request, "name"); //上传者
		String uploader = new String(uploader0.getBytes("8859_1"),"GB2312");
		String description0 = ServletRequestUtils.getStringParameter(request, "description"); //上传内容描述
		String description = new String(description0.getBytes("8859_1"),"GB2312");
		String photoName0 = ServletRequestUtils.getStringParameter(request, "photoName"); //上传图片名字
		String photoName = new String(photoName0.getBytes("8859_1"),"GB2312");
		System.out.println("-----用户"+uploader+"上传了新内容，图片名："+photoName+"，图片描述："+description+"-----");
		//重命名upload的图片名字，上传者_camera/gallery_接收日期_描述
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd-HHmmss"); //HH表示24小时，hh表示12小时制
		String time=df.format(date);
		int index = photoName.lastIndexOf(".");
		String photoNameSuffix = photoName.substring(index); //图片后缀名
		//如果是拍照上传的图片
		if(photoName.startsWith("camera_")){
			photoName = uploader+"_camera_"+time+"_"+description+photoNameSuffix;
		}
		//本地上传的图片
		else{
			photoName = uploader+"_gallery_"+time+"_"+description+photoNameSuffix;
		}
		
		ServletInputStream inputStream = request.getInputStream();
		File f = new File("D:/CampusPuSH_upload/"+photoName);
		//File f = new File(ServletActionContext.getServletContext().getRealPath("/uploadImages")+"/"+"aaa.jpg");
		//File dir = new File(request.getSession().getServletContext().getRealPath("WEB-INF/upload/1.txt"));
		//dir.mkdir(); //创建目录
		FileOutputStream fos = new FileOutputStream(f);
		byte[] buf=new byte[1024];
		  int len;
		  while((len=inputStream.read(buf))>0){
		   fos.write(buf, 0, len);
		   fos.flush();
		  }
		  fos.close();
				
		
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("text/plain");
		out.print("upload:success"); //上传成功
		out.flush();
		//保存数据库
		
    	return null;    	  	
    }
    
    //自定义,从android获取用户的上传图片
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
