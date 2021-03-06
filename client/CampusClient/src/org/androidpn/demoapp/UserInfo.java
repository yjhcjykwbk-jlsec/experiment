package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Application;


public class UserInfo extends Application {

	private String myUserName;
	private String myUserPWD;
	private String myNotifierTitle;
	private String myNotifierMessage;
	private String myNotifierUri;
	private ArrayList<HashMap<String, String>> myNotifier = new ArrayList<HashMap<String,String>>();	

	//赋给通知列表一个初始值，在DemoAppActivity create的时候加载
	public void initUserInfo(){
		if (myNotifier.isEmpty()) {
			HashMap<String, String> addMap = new HashMap<String, String>();
		    addMap.put("ItemTitle", "关于CampusPuSH");
		    addMap.put("ItemMessage", "北京大学深圳研究生院|通信与信息安全实验室\n\n功能：订阅消息、接收推送消息、上传图片、观看视频、观看直播、实时通讯\n\nTips：\n登陆请使用在http://push.pkusz.edu.cn上注册的用户名和密码。\n订阅页面，不选中提交，代表取消订阅；\n视频直播，发布的直播视频名称是用户名_视频名称（暂不开放）");
		    addMap.put("ItemUri", "http://push.pkusz.edu.cn");
		    this.myNotifier.add(addMap);    
		}

	}

	
	public void addMyNotifier(HashMap<String, String> addMap){
		this.myNotifier.add(0,addMap);
	}

	public ArrayList<HashMap<String, String>> getMyNotifier() {
		return myNotifier;
	}

	public void setMyNotifier(ArrayList<HashMap<String, String>> myNotifier) {
		this.myNotifier = myNotifier;
	}

	public String getMyNotifierTitle(){
		return myNotifierTitle;
	}
	
	public void setMyNotifierTitle(String myNotifierTitle){
		this.myNotifierTitle = myNotifierTitle;
	}
	
	public String getMyNotifierMessage(){
		return myNotifierMessage;
	}
	
	public void setMyNotifierMessage(String myNotifierMessage){
		this.myNotifierMessage = myNotifierMessage;
	}
	
	public String getMyNotifierUri(){
		return myNotifierUri;
	}
	
	public void setMyNotifierUri(String myNotifierUri){
		this.myNotifierUri = myNotifierUri;
	}
	
	public String getMyUserName() {
		return myUserName;
	}
	public void setMyUserName(String myUserName) {
		this.myUserName = myUserName;
	}
	public String getMyUserPWD() {
		return myUserPWD;
	}
	public void setMyUserPWD(String myUserPWD) {
		this.myUserPWD = myUserPWD;
	}
	
	
}
