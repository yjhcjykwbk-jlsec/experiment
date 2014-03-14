/**
 * 
 */
package org.androidpn.server.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author chengqiang.liu
 *	閫氱煡鍐呭瀹炰綋
 */

public class NotificationMO implements Serializable {
	
	private static final long serialVersionUID = 1845362556725768545L;
	
	public static final String STATUS_NOT_SEND = "0"; //鏈彂閫�
	public static final String STATUS_SEND = "1"; // 鍙戦�浣嗘湭鎺ユ敹
	public static final String STATUS_RECEIVE = "2"; // 鍙戦�鍑轰笖琚帴鍙�
	public static final String STATUS_READ = "3"; // 鍙戦�鍑轰笖宸茶
	
	public NotificationMO(){
	}
	
	public NotificationMO(String apiKey,String title,String message,String uri){
		this.apiKey = apiKey;
		this.title = title;
		this.message = message;
		this.uri = uri;
		this.createTime = new Timestamp(System.currentTimeMillis()); // ?
		this.updateTime = new Timestamp(System.currentTimeMillis()); // ?
	}
	
    private Long id;

    private String username;
    
    private String clientIp;
    
    private String resource;

    private String messageId;
    
    private String apiKey;

    private String title;

    private String message;
    
    private String uri;
    
    private String status;
    
    private Timestamp createTime ;
    
    private Timestamp updateTime ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	
}
