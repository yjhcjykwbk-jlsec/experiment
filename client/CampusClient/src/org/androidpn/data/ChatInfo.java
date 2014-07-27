package org.androidpn.data;

import java.util.Date;

/*
 * related to chat message list
 */
public class ChatInfo {
	private String username;//the man you're talking with
	private String chatXml;
	private Date time;
	private String packetID;
	public boolean hasPic=false;
	//发出
	private boolean isSelf;
	//自己发送的，是否发送成功
	private boolean sent;
	/*
	 * constructor of a message record
	 */
	public ChatInfo(String u, String chat,Date time,  String pid,boolean isSelf) {
		this.username = u;
		this.chatXml = chat;
		this.packetID=pid;
		this.time=time;
		if(this.chatXml.startsWith("<img>")) this.hasPic=true;
		if(isSelf){
			this.isSelf=true;
			this.sent=false;
		}else{
			this.isSelf=false;
		}
	}
	public ChatInfo(String u, String chat , String pid,boolean isSelf) {
		this.username = u;
		this.chatXml = chat;
		this.packetID=pid;
		if(this.chatXml.startsWith("<img>")) this.hasPic=true;
		if(isSelf){
			this.isSelf=true;
			this.sent=false;
		}else{
			this.isSelf=false;
		}
		this.time=new Date(System.currentTimeMillis());
	}
	public boolean isComplete(){
		return this.packetID!=null&&this.chatXml!=null&&this.time!=null&&this.username!=null;
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
	public String getRecipient() {
		// TODO Auto-generated method stub
		return username;
	}
}