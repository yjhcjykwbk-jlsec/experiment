package org.androidpn.server.console.vo;

public class SubscriptionsVO implements Comparable{
	private String subscriptionName;
	private int count;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getSubscriptionName() {
		return subscriptionName;
	}
	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return subscriptionName.compareTo(((SubscriptionsVO)arg0).getSubscriptionName());
	}
	
}
