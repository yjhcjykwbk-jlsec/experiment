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

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserNotFoundException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/** 
 * This class is the implementation of UserDAO using Spring's HibernateTemplate.
 *
 * @author Sehwan Noh (devnoh@gmail.com)　xzg
 * table: apn_user apn_friend
 */
public class UserDaoHibernate extends HibernateDaoSupport implements UserDao {

    public User getUser(Long id) {
        return (User) getHibernateTemplate().get(User.class, id);
    }

    public User saveUser(User user) {
        getHibernateTemplate().saveOrUpdate(user);
        getHibernateTemplate().flush();
        return user;
    }

    
    public void removeUser(Long id) {
        getHibernateTemplate().delete(getUser(id));
    }

    public boolean exists(Long id) {
        User user = (User) getHibernateTemplate().get(User.class, id);
        return user != null;
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        return getHibernateTemplate().find(
                "from User u order by u.createdDate desc");
    }
    
   
    @SuppressWarnings("unchecked")
    public List<User> getUsersBySubscriptions(String subscription) throws UserNotFoundException{
    	System.out.println("find subscriptions:"+subscription);
		List<User> subUsers = getHibernateTemplate().find("from User where subscriptions like '%"+subscription+"%' or subscriptions like '%all%'");///////////////////////changed
		if (subUsers==null || subUsers.isEmpty()){
			throw new UserNotFoundException();
		}
		else{
			System.out.println("UserDaoHiberate.getUserby..:"+subUsers);
			return (subUsers);
		}    	    	
    }
    
    @SuppressWarnings("unchecked")
    public User getUserByUsername(String username) throws UserNotFoundException{
        List users = getHibernateTemplate().find("from User where username=?",
                username);
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("User '" + username + "' not found");
        } else {
            return (User) users.get(0);
        }
    }
    
    /**
     * @author  xzg
     * 获取好友
     */
    @SuppressWarnings("unchecked")
	public List<User> getFriends(long id) {
		// TODO Auto-generated method stub
        List<User> list=getHibernateTemplate().find(
               "select u from User u,Friend f where u.id=f.pk.id2 and f.pk.id1="+id+" and f.flag=true order by u.id desc");
          //     "select u from User u where u.id in (select f.pk.id2 from  Friend f where f.pk.id1="+id+" and f.pk.id2 in ( select f.pk.id1 from apn_friend where f.pk.id2="+id+"))");
          //	"select f1 from Friend f1 where f1.pk.id1="+id+" left join Friend f2  on f1.pk.id1=f2.pk.id2 and f1.pk.id2=f2.pk.id1 and f1.pk.id1="+id+" ");
//        		"select f1 from Friend f1 where f1.pk.id1="+id+" and f1.pk.id2 in ( select f2.pk.id1 from friend f2 where f2.pk.id1="+id+")");
//        List<User> userList=new ArrayList();
//        for(Array a : list){
//        	userList.add((User)(a[0]));
//        }
        return list;
	}

}
