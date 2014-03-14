package org.androidpn.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * Activity工具类，用一个list包含所有创建的activity，在最后完全退出推送客户端时,清除所有activity
 * @author xiaobingo
 *
 */
public class ActivityUtil extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityUtil instance;
	
	private ActivityUtil(){
		
	}
	
	//单例模式中获取唯一的ActivityUtil实现
	public static ActivityUtil getInstance(){
		if (null==instance) {
			instance = new ActivityUtil();
		}
		return instance;
	}
	
	//添加activity到list中
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//遍历所有activity并finish
	public void exit(){
		for (Activity activity:activityList) {
			activity.finish();
		}
		System.exit(0);
	}
}
