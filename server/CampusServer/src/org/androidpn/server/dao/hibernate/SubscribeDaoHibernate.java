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
package org.androidpn.server.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.server.dao.*;
import org.androidpn.server.model.FriendPK;
import org.androidpn.server.model.User;
import org.androidpn.server.model.App;
import org.androidpn.server.model.Subscribe;
import org.androidpn.server.model.SubscribePK;
import org.androidpn.server.service.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/** 
 * @author xzg
 */
public class SubscribeDaoHibernate extends HibernateDaoSupport implements SubscribeDao {
    protected final Log log = LogFactory.getLog(getClass());
	//subscribeUser 是订阅者
    public List<User> getSubscribeUsers(long id){
    	log.info("getSubscribeUsers("+id+")");
		List<User> subUsers = getHibernateTemplate().find(
				"select u from Subscribe s, App a where s.pk.appid="+id+" and s.pk.userid=u.id");
    	return subUsers;
    }
    public List<App> getUserSubscribes(long userId){
    	log.info("getUserSubscribs("+userId+")");
		List<App> subs = getHibernateTemplate().find(
				"select a from Subscribe s, App a where s.pk.userid="+userId+" and s.pk.appid=a.id");
    	return subs;
    }
    //subscribe 是订阅
    public void  addSubscribe(long userId,long appId){ 
    	Subscribe s=new Subscribe(new SubscribePK(userId,appId));
    	getHibernateTemplate().saveOrUpdate(s);
    }
	public void delSubscribe(long userId,long appId){
		Subscribe s=new Subscribe(new SubscribePK(userId,appId));
    	getHibernateTemplate().delete(s);
	}
	
	//app　是可订阅的app
	public List<App> listApps(){
		List<App> apps = getHibernateTemplate().find(
				"from App a");
    	return apps;
	}
    public App getApp(long appid){
    	List<App> apps=getHibernateTemplate().find(
    			"from App a where a.id="+appid);
    	return apps.get(0);
    }
    public App getAppByName(String appName){
    	List<App> apps=getHibernateTemplate().find(
    			"from App a where a.name="+appName);
    	return apps.get(0);
    }
}
