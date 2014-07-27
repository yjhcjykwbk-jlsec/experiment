package org.androidpn.server.console.vo;

import java.util.Date;

public class PushStaticsVO {
	private int staticsId;
	private String messageId;
	private String messageTitle;
	private Date messageCreateDate;
	private int count_target;
	private int count_notsend;
	private int count_send;
	private int count_receive;
	private int count_view;
	
	public int getStaticsId() {
		return staticsId;
	}
	public void setStaticsId(int staticsId) {
		this.staticsId = staticsId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Date getMessageCreateDate(){
		return messageCreateDate;
	}
	public void setMessageCreateDate(Date messageCreateDate){
		this.messageCreateDate = messageCreateDate;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public int getCount_target() {
		return count_target;
	}
	public void setCount_target(int count_target) {
		this.count_target = count_target;
	}
	public int getCount_notsend() {
		return count_notsend;
	}
	public void setCount_notsend(int count_notsend) {
		this.count_notsend = count_notsend;
	}
	public int getCount_send() {
		return count_send;
	}
	public void setCount_send(int count_send) {
		this.count_send = count_send;
	}
	public int getCount_receive() {
		return count_receive;
	}
	public void setCount_receive(int count_receive) {
		this.count_receive = count_receive;
	}
	public int getCount_view() {
		return count_view;
	}
	public void setCount_view(int count_view) {
		this.count_view = count_view;
	}
	
	
}
