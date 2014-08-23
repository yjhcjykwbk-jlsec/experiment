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
package org.androidpn.server.service.impl;

import java.util.List;

import javax.persistence.EntityExistsException;

import org.androidpn.server.dao.FriendDao;
import org.androidpn.server.dao.UserDao;
import org.androidpn.server.dao.SubscribeDao;
import org.androidpn.server.model.User;
import org.androidpn.server.model.App;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

/** 
 * This class is the implementation of UserService.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class UserServiceImpl implements UserService {

    protected final Log log = LogFactory.getLog(getClass());

    private UserDao userDao;
    private FriendDao friendDao;
    private SubscribeDao subscribeDao;
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setFriendDao(FriendDao friendDao) {
        this.friendDao = friendDao;
    }
    public void setSubscribeDao(SubscribeDao subscribeDao) {
        this.subscribeDao = subscribeDao;
    }
    public User getUser(String userId) {
        return userDao.getUser(new Long(userId));
    }

    public List<User> listUsers() {
        return userDao.getUsers();
    }
    
    /**
     * @author:xu
     */
    public List<User> getFriends(long id){
    	return userDao.getFriends(id);
    }
    public List<User> getSubscribeUsers(String subscription) throws UserNotFoundException {
    	return subscribeDao.getSubscribeUsers(subscription);// (List<User>) userDao.getUsersBySubscriptions(subscription);
    }
 
    public List<App> getUserSubscribes(Long userId){
        log.info("getUserSubscribes("+userId+")");
    	return subscribeDao.getUserSubscribes(userId);
    }
    public void addSubscribe(Long userId,Long appId){
    	subscribeDao.addSubscribe(userId , appId);
    }
    public void delSubscribe(Long userId,Long appId){
    	subscribeDao.delSubscribe(userId,appId);
    }
    
    public User saveUser(User user) throws UserExistsException {
        try {
            return userDao.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        }
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return (User) userDao.getUserByUsername(username);
    }

    public void removeUser(Long userId) {
        log.debug("removing user: " + userId);
        userDao.removeUser(userId);
    }

	public boolean addFriend(int id1,int id2) {
		// TODO Auto-generated method stub
		return friendDao.addFriend(id1,id2);
	}
	public List<User> getFriends(String name) {
		// TODO Auto-generated method stub
		try {
			User u=getUserByUsername(name);
			return getFriends(u.getId());
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
