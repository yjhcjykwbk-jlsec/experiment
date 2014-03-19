package org.androidpn.data;

import java.util.Date;

/*
 * related to chat message list
 */
public class ChatInfo {
	String username;
	String chatXml;
	Date time;
	String packetID;
	boolean isSelf;
	//�Լ����͵ģ��Ƿ��ͳɹ�
	boolean sent;
	/*
	 * constructor of a message record
	 */
	public ChatInfo(String u, String chat,Date time,  String pid,boolean isSelf) {
		this.username = u;
		this.chatXml = chat;
		this.packetID=pid;
		this.time=time;
		if(isSelf){
			this.isSelf=true;
			this.sent=false;
		}else{
			this.isSelf=false;
		}
	}
	public boolean isSent(){
		return sent;
	}
	public void setSent(){
		this.sent=true;
	}
	public Date getTime() {
		return time;
	}
	public String getPacketID(){
		return packetID;
	}
	public String getContent() {
		return chatXml;
	}

	public String getName() {
		return username;
	}
	public boolean isSelf(){
		return isSelf;
	}
}