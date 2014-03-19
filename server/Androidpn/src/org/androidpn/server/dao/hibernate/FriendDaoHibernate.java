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

import java.util.List;

import org.androidpn.server.dao.FriendDao;
import org.androidpn.server.model.Friend;
import org.androidpn.server.model.FriendPK;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** 
 * This class is the implementation of UserDAO using Spring's HibernateTemplate.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class FriendDaoHibernate extends HibernateDaoSupport implements FriendDao {
	 
//	private JdbcTemplate template;// Impl层需要操作数据库，依赖JdbcTemplate的支持  
    //id1 关注 id2
	public boolean addFriend(int id1, int id2) {
		
//		 String sql = "insert into apn_friend(id1,id2) values(?,?)";  
//	     template.update(sql, new Object[]{id1, id2});  
//	     template.update(sql, new Object[]{id2, id1});  
	    @SuppressWarnings("unchecked")
		List<Friend> f=getHibernateTemplate().find("from Friend f where f.pk.id1="+id2+" and f.pk.id2="+ id1);
		if(f.size()>0) {
			getHibernateTemplate().saveOrUpdate(new Friend(new FriendPK(id1,id2),true));
			getHibernateTemplate().saveOrUpdate(new Friend(new FriendPK(id2,id1),true));
			return true;
		}
		else getHibernateTemplate().saveOrUpdate(new Friend(new FriendPK(id1,id2),false));
		//getHibernateTemplate().saveOrUpdate(new Friend(new FriendPK(id2,id1)));
		return false;
	}
}
