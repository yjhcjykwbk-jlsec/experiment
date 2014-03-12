package org.androidpn.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * Activity�����࣬��һ��list�������д�����activity���������ȫ�˳����Ϳͻ���ʱ,�������activity
 * @author xiaobingo
 *
 */
public class ActivityUtil extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityUtil instance;
	
	private ActivityUtil(){
		
	}
	
	//����ģʽ�л�ȡΨһ��ActivityUtilʵ��
	public static ActivityUtil getInstance(){
		if (null==instance) {
			instance = new ActivityUtil();
		}
		return instance;
	}
	
	//���activity��list��
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//��������activity��finish
	public void exit(){
		for (Activity activity:activityList) {
			activity.finish();
		}
		System.exit(0);
	}
}
