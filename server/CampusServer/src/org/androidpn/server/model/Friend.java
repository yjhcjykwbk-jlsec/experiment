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
package org.androidpn.server.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * @author xzg
 */
@Entity
@Table(name = "apn_friend")
public class Friend implements Serializable {

	@Id
	private FriendPK pk; //this must match the friend.hbm.xml's composite-id
	@Column(name = "flag", nullable = false)
	private boolean flag;

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean f) {
		this.flag = f;
	}

	@Id
	public FriendPK getPk() {
		return pk;
	}

	public void setPk(FriendPK id) {
		this.pk = id;
	}

	public Friend(FriendPK pk,boolean flag) {
		this.pk = pk;
		this.flag=flag;
	}

	public Friend() {

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
