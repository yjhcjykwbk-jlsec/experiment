package org.androidpn.server.service.impl;
import java.util.List;
import org.androidpn.server.model.App;
import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.dao.SubscribeDao;
import org.androidpn.server.service.*;
public class AppServiceImpl implements AppService{
	private SubscribeDao subscribeDao;
	public void setSubscribeDao(SubscribeDao subDao) {
		this.subscribeDao = subDao;
	}
    public App getApp(Long appId){
    	return subscribeDao.getApp(appId);
    }
  
    public App getAppByAppname(String appName) throws AppNotFoundException{
    	return subscribeDao.getAppByName(appName);
    }

    public List<App> listApps(){
    	return subscribeDao.listApps();
    }
}
