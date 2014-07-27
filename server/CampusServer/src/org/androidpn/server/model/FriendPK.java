package org.androidpn.server.model;
import java.io.Serializable;

import javax.persistence.Column;  
import javax.persistence.Embeddable;  
 
/**
 * @author: xzg
 */
@Embeddable  
public class FriendPK implements Serializable {
  
	private static final long serialVersionUID = 1L;

	@Column(name = "id1", nullable = false)
    private int id1;

    @Column(name = "id2", nullable = false)
    private int id2;
    
    
    public int getId1() {
        return id1;
    }

    public void setId1(int id) {
        this.id1 = id;
    }
    
    public int getId2() {
        return id2;
    }

    public void setId2(int id) {
        this.id2 = id;
    }
   
    public FriendPK(){
    	
    }
    public FriendPK(int id1,int id2) {
    	this.id1=id1;this.id2=id2; 
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FriendPK)) {
            return false;
        }
        
        final FriendPK obj = (FriendPK) o;
        if(id1==obj.id1&&id2==obj.id2) return true;
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 29 * result + id1;
        result = 29 * result
                + id2;
        return result;
    }

}
