package org.androidpn.server.model;
import java.io.Serializable;

import javax.persistence.Column;  
import javax.persistence.Embeddable;  
 
/**
 * @author: xzg
 * subscribe 表格的复合主键映射
 */
@Embeddable  
public class SubscribePK implements Serializable {  
	private static final long serialVersionUID = 1L;
 
    @Column(name = "userid", nullable = false )
    private long userid;
    @Column(name = "appid", nullable = false )
    private long appid;

    
    public long getUserid() {
        return userid;
    }

    public void setUserid(long id) {
        this.userid = id;
    }
    
    public long getAppid() {
        return appid;
    }

    public void setAppid(long id) {
        this.appid = id;
    }
   
    public SubscribePK(){
    	
    }
    public SubscribePK(long id1,long id2) {
    	this.userid=id1;
    	this.appid=id2; 
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SubscribePK)) {
            return false;
        }
        
        final SubscribePK obj = (SubscribePK) o;
        if(userid==obj.userid&&appid==obj.appid) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 29 * result + (int)userid;
        result = 29 * result
                + (int)appid;
        return result;
    }

}
