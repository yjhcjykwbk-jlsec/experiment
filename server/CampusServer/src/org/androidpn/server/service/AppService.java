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
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public interface AppService {

    public App getApp(Long AppId);
    
  //  public App saveApp(App App) throws AppExistsException;

    public App getAppByAppname(String Appname) throws AppNotFoundException;

  //  public void removeApp(Long AppId); 
    
    public List<App> listApps();
}
