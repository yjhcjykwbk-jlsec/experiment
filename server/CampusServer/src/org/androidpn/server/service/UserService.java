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
package org.androidpn.server.service;

import java.util.List;

import org.androidpn.server.model.User;
import org.androidpn.server.model.App;

/** 
 * Business service interface for the user management.
 *
 * @author xzg
 */
public interface UserService {
	
	//好友
    
    public List<User> getFriends(long id);
    
    public List<User> getFriends(String name);
    
    public boolean addFriend(int id1,int id2);

    public User getUser(String userId);
    
    public User getUserByUsername(String username) throws UserNotFoundException;

    public List<User> listUsers();

    public User saveUser(User user) throws UserExistsException;

    public void removeUser(Long userId);
    
    
    //订阅有关接口
    

    public List<User> getSubscribeUsers(String subName) throws UserNotFoundException;
    
    public List<App> getUserSubscribes(Long userId);

    public void addSubscribe(Long userId,Long appId);
    
    public void delSubscribe(Long userId,Long appId);
}
