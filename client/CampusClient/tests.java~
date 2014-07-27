  private XmlPullParser resetParser(String s) {
        try {
        	XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(new StringReader(s));
            return parser;
        }
        catch (XmlPullParserException xppe) {
            xppe.printStackTrace();
            return null;
        }
    }




/*try {
				Document doc=DocumentHelper.parseText(resp);
				List<?> list=doc.selectNodes("/xml");
				Iterator<?> level0_it=list.iterator();
				Element level0_ele=(Element)(level0_it.next());
				Iterator<?> level1_it=level0_ele.elementIterator("result");
				Element level1_ele=(Element)(level1_it.next());
				if(level1_ele.getText().equals("failed")){
					Log.i(LOGTAG,"user not found");return ;
				}
				level1_it=level0_ele.elementIterator("user");
				level1_ele=(Element)(level1_it.next());
					Log.i(LOGTAG,"user found"+level1_ele.getText());return ;
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
//			XmlPullParser parser=resetParser(resp);
//			Log.i(LOGTAG, resp);
//			try {
//				parser.next();
//				Log.i(LOGTAG,parser.getName());
//				parser.require(XmlPullParser.START_TAG,null,"xml");
//				while(parser.nextTag()!=XmlPullParser.END_TAG){
//					String tagName=parser.getName();
//					Log.i(LOGTAG,tagName);
//					if(tagName.equals("result")){
//						String tagValue=parser.nextText();
//						if(tagValue.equals("failed")) {
//							Log.i(LOGTAG,"user not found");return ;
//						}
//						else Log.i(LOGTAG,"user found"); 
//					}
//					else if(tagName.equals("org.androidpn.server.model.User")){
//						User u=parseUser(parser);
////						String tagValue=(String) parser.getProperty("user");
//						Log.i(LOGTAG,"user found:"+u.getUsername());
//						return;
//					}
//					else return;
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
