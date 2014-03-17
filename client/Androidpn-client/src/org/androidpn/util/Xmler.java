package org.androidpn.util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
public class Xmler{
	public static XStream instance=null;
	public static XStream getInstance(){
		if(instance==null){
			synchronized(Xmler.class){
				instance=new XStream(new DomDriver());
			}
		}
		return instance;
	}
}