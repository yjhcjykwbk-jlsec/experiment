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
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/** 
 * This class represents the basic app object.
 *
 * @author xzg
 */
 
public class App implements Serializable,Contacter {

    private static final long serialVersionUID = 4733464888738356502L;
 
    private Long id;
    private String name;
    private Date createdDate = new Date();
    private String desp;
    private String url;
    public App() {
    }

    public App(final String  name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String  name) {
        this.name = name;
    }
 
    public String getUrl() {
        return url;
    }

    public void setUrl(String  url) {
        this.url = url;
    }
 
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
  
    public String getDesp() {
        return desp;
    }
    
    public void setDesp(String desp) {
        this.desp=desp;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof App)) {
            return false;
        }

        final App obj = (App) o;
        return this.id==obj.getId();
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 29 * result + (name != null ? name.hashCode() : 0);
        result = 29 * result
                + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }

}
