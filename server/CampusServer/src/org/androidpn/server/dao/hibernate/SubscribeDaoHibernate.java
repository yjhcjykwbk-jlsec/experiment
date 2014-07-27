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
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/** 
 * This class is the implementation of UserDAO using Spring's HibernateTemplate.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class SubscribeDaoHibernate extends HibernateDaoSupport implements SubscribeDao {
    public List<User> getListeners(long id){
    	System.out.println("find sub users of app:"+id);
		List<User> subUsers = 
			getHibernateTemplate().find(
					"select u from User u,Subscribe s where s.appid="+id+" and s.userid=u.id");
    	return subUsers;
    }
    public List<App> getSubscribes(long userId){
    	System.out.println("find sub  of user:"+userId);
		List<App> subs = 
			getHibernateTemplate().find(
					"select a from Subscribe s, App a where s.userid="+userId+" and s.appid=a.id");
    	return subs;
    }
    public void  addListener(long userId,long appId){ 
    	Subscribe s=new Subscribe(new SubscribePK(userId,appId));
    	getHibernateTemplate().saveOrUpdate(s);
    }
	public void delListener(long userId,long appId){
		Subscribe s=new Subscribe(new SubscribePK(userId,appId));
    	getHibernateTemplate().delete(s);
	}
}
