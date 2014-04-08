/**
 * 
 */
package org.androidpn.server.service;

import java.util.List;

import org.androidpn.server.model.NotificationMO;

/**
 * @author chengqiang.liu
 *
 */
public interface NotificationService {
	
	/**
	 * @param notificationMO
	 */
	public void saveNotification(NotificationMO notificationMO);
	
	/**
	 * @param notificationMO
	 */
	public void updateNotification(NotificationMO notificationMO);
	
	/**
	 * @param id
	 */
	public void deleteNotification(Long id);
	
	/**
	 * @param null
	 * @return List<NotificationMO>
	 */
	public List<NotificationMO> getNotifications();
	
	
	/**
	 * @param 
	 * @author xuzhigang
	 */
	public List<NotificationMO> getUnsentNotifications(String userName);
	/**
	 * @param id
	 * @return NotificationMO
	 */
	public NotificationMO queryNotificationById(Long id);
	
	/**
	 * @param notificationMOs
	 */
	public void createNotifications(List<NotificationMO> notificationMOs);
	
	/**
	 * @param userName	 
	 * @param messageId	֪ͨID
	 * @return NotificationMO
	 */
	public NotificationMO queryNotificationByUserName(String userName,String messageId);
	
	/**
	 * @param mo  
	 * @return List<NotificationMO>
	 */
	public List<NotificationMO> queryNotification(NotificationMO mo);
	
	/**
	 * @param status	״̬
	 * @param messageId ID
	 * @return
	 */
	public int queryCountByStatus(String status,String messageId);
}
