package org.androidpn.server.dao;

import java.util.List;

import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.model.ReportVO;

/**
 * @author chengqiang.liu
 *
 */
public interface NotificationDao {
	
	/**
	 */
	public List<NotificationMO> getNotifications();	
	
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
	 * @param id
	 * @return NotificationMO
	 */
	public NotificationMO queryNotificationById(Long id);
	
	/**
	 * @param userName 
	 * @param messageId	֪ͨID
	 * @return List<NotificationMO
	 */
	public List<NotificationMO> queryNotificationByUserName(String userName,String messageId);

	/**
	 * @param status	״̬
	 * @param messageId ID
	 * @return
	 */
	public int queryCountByStatus(String status,String messageId);
	
	/**
	 * @param mo 
	 * @return List<ReportVO>
	 */
	public List<ReportVO> queryReportVO(NotificationMO mo);
	
	/**
	 * @param mo 
	 * @return List<NotificationMO>
	 */
	public List<NotificationMO> queryNotification(NotificationMO mo);

}
