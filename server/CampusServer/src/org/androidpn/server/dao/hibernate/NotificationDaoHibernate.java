/**
 * 
 */
package org.androidpn.server.dao.hibernate;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.NotificationMO;
import org.androidpn.server.model.ReportVO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author chengqiang.liu
 * 
 */
public class NotificationDaoHibernate extends HibernateDaoSupport implements
		NotificationDao {

	public void deleteNotification(Long id) {
		getHibernateTemplate().delete(queryNotificationById(id));
	}

	
	public NotificationMO queryNotificationById(Long id) {
		NotificationMO notificationMO = (NotificationMO) getHibernateTemplate()
				.get(NotificationMO.class, id);
		return notificationMO;
	}

	public void saveNotification(NotificationMO notificationMO) {
		getHibernateTemplate().saveOrUpdate(notificationMO);
		getHibernateTemplate().flush();
	}

	public void updateNotification(NotificationMO notificationMO) {
		getHibernateTemplate().update(notificationMO);
		getHibernateTemplate().flush();
	}

	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotificationByUserName(String userName,
			String messageId) {
		Object[] params = new Object[] {userName, messageId};
		return getHibernateTemplate()
				.find(
						"from NotificationMO n where n.username=? and n.messageId=? order by n.createTime desc",
						params);
	}

	public List<NotificationMO> queryOldNotificationByUserName(String userName) {
		Object[] params = new Object[] {userName};
		
		return getHibernateTemplate()
				.find("from NotificationMO n where n.username=? and status='0' and unix_timestamp(n.createTime)>unix_timestamp(NOW())-3600*12 limit 0,15",params);
	}

	
	@SuppressWarnings("unchecked")
	public List<NotificationMO> getNotifications() {
		return getHibernateTemplate().find(
						"from NotificationMO n order by n.createTime desc");
	}
	
	@SuppressWarnings("unchecked")
	public int queryCountByStatus(String status, String messageId) {
		Object[] params = new Object[] {status, messageId};
		List<NotificationMO> findList = getHibernateTemplate().find("from NotificationMO n where n.status=? and n.messageId=? order by n.messageId desc",params);
		int findResult=findList.size();
		return findResult;  
	}

	@SuppressWarnings("unchecked")
	public List<NotificationMO> queryNotification(NotificationMO mo) {
		return getHibernateTemplate().findByExample(mo);
	}

	public List<ReportVO> queryReportVO(NotificationMO mo) {
		// TODO Auto-generated method stub
		return null;
	}

}
