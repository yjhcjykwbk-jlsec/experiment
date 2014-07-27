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
package org.androidpn.server.dao;
import java.util.List;
import org.androidpn.server.model.App;
import java.util.ArrayList;
import org.androidpn.server.model.*;
/** 
 * @author xzg
 * apn_subscribe and apn_app
 */
public interface SubscribeDao {
    
    public List<User> getSubscribeUsers(long id);
    public List<App> getUserSubscribes(long userId);
    
    public void  addSubscribe(long userid,long appid);
	public void delSubscribe(long userId,long appId);
	
    public List<App> listApps();
    public App getApp(long appId);
    public App getAppByName(String appName);

}
