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
 * @author Sehwan Noh (devnoh@gmail.com)
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

    
    //ɾ���û�
    public void removeUser(Long id) {
        getHibernateTemplate().delete(getUser(id));
    }

    //�ж��Ƿ�����û�
    public boolean exists(Long id) {
        User user = (User) getHibernateTemplate().get(User.class, id);
        return user != null;
    }

    //���������û�
    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        return getHibernateTemplate().find(
                "from User u order by u.createdDate desc");
    }
    
   
    //��ݶ������������û�
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
    
    //����û�������û�
    @SuppressWarnings("unchecked")
    public User getUserByUsername(String username) throws UserNotFoundException {
        List users = getHibernateTemplate().find("from User where username=?",
                username);
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("User '" + username + "' not found");
        } else {
            return (User) users.get(0);
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<User> getFriends(int id) {
		// TODO Auto-generated method stub
        List<User> list=getHibernateTemplate().find(
                "select u from User u,Friend f where u.id=f.pk.id2 and f.pk.id1="+id+" order by u.id desc");
//        List<User> userList=new ArrayList();
//        for(Array a : list){
//        	userList.add((User)(a[0]));
//        }
        return list;
	}

}
