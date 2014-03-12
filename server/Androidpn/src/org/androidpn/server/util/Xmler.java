package org.androidpn.server.util;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
public class Xmler{
	private static Xmler instance;
	private XStream xstream;
	public static Xmler getInstance(){
		if(instance==null){
			synchronized(Xmler.class){
				instance=new Xmler();
			}
		}
		return instance;
	}
	private Xmler(){
		xstream=new XStream(new DomDriver());
	}
	public  String getXml(Object obj){
//		XStream xstream=new XStream();
		 String xml = xstream.toXML(obj); 
		 return xml;
	}
//	public String getXml(List<Object> list){
//		return "";
//	}
}