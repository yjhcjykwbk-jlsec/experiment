package org.androidpn.server.console.controller;

import org.springframework.web.servlet.ModelAndView;

public class Utils {
	public static ModelAndView strView(String name,String value){
   	 ModelAndView mav = new ModelAndView();
        mav.addObject(name, "<xml>"+value+"</xml>");
        mav.setViewName("xml");
        return mav;
   }
}
