package nu.fw.jeti.images;

import java.util.*;
import javax.swing.*;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.io.*;
import java.net.*;

import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.backend.Start;

/**
 * @author E.S. de Boer
 */

public class StatusIcons
{
	private static Map statusIcons;
	private SAXParser parser;

	public StatusIcons(SAXParser parser)
    {
		if(statusIcons ==null)
		{
			try{
				new Icons(parser,"rostericons");
			}catch(IOException e)
			{
				e.printStackTrace();
			}
			statusIcons = new HashMap();
			loadDefaultPics();
			loadWebstartPics();
			List iconList = Preferences.getPlugable("rostericons");
			for (Iterator i = iconList.iterator();i.hasNext();)
			{
				Object[] temp = (Object[])i.next();
				if(((Boolean)temp[1]).booleanValue())
				{
					loadRosterIcon((String)temp[4],(String)temp[3]);
				}
			}
		}
	}
	
	public StatusIcons(){}
	
	private void loadParser()
	{
		try{parser = SAXParserFactory.newInstance().newSAXParser();}
		catch (FactoryConfigurationError ex){ex.printStackTrace();}
		catch (SAXException ex){ex.printStackTrace();}
		catch (ParserConfigurationException ex){ex.printStackTrace();}
	}
	
	protected void reloadRosterIcon(String name,String type)
	{
		if(!statusIcons.containsKey(type))loadRosterIcon(name, type);
	}
	
	protected void loadRosterIcon(String filename,String type)
	{
		String name2 = filename.substring(0, filename.lastIndexOf("."));
		InputStream stream = null;
		URL url = null;
		try
		{
			//if(Start.programURL != null)
			url =  new URL ("jar:" + Start.programURL + "plugins/rostericons/" + filename +"!/" + name2 + "/");
			//else url =  new URL ("jar:" + Start.localURL + "plugins/rostericons/" + name +"!/" + name2 + "/");
			stream =  new URL(url,"icondef.xml").openStream();
		}
		catch (IOException ex)
		{
			//webstart
			url=null;
			System.out.println("webstart loading");
			stream =  getClass().getClassLoader().getResourceAsStream("msn_messenger-6.0/icondef.xml");
			//ex.printStackTrace();
		}
		try
		{
			if(parser == null) loadParser();
			parser.parse(stream ,new RosterIconsHandler(url,statusIcons,type));
		}
		catch (SAXException ex)
		{
			ex.getException().printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void unloadRosterIcon(String name)
	{
		statusIcons.remove(name);
	}
	

	static public ImageIcon getOfflineIcon()
	{
		return getStatusIcon(Presence.UNAVAILABLE ,"standard");
	}

	static public ImageIcon getStatusIcon(int show){return getStatusIcon(show,"standard");}

	/** verander
	 * calls getImageIcon(show) whith the default imageset
	 * @param show
	 * @return ImageIcon
	 */
	static public ImageIcon getImageIcon(String show){return getStatusIcon(show,"images");}


	/**
	 * calls getStatusIcon(show,type) whith the default imageset
	 * @param show
	 * @return ImageIcon
	 */
	//static public ImageIcon getImageIcon(int image){return getStatusIcon(image,"images");}

	/**
	 * returns a status icon
	 * @param show one of chat,available,dnd,away,xa or unavailable
	 * @param type which image set
	 * @return ImageIcon
	 */
	static public ImageIcon getStatusIcon(int show,String type)
	{
		switch (show)
		{
			case Presence.FREE_FOR_CHAT: return  getStatusIcon("status/chat",type);
			case Presence.AWAY: return  getStatusIcon("status/away",type);
			case Presence.XA: return  getStatusIcon("status/xa",type);
			case Presence.DND: return  getStatusIcon("status/dnd",type);
			case Presence.UNAVAILABLE: return  getStatusIcon("status/offline",type);
			case Presence.INVISIBLE: return  getStatusIcon("status/invisible",type);
			case Presence.NONE: return  getStatusIcon("status/unknown",type);
			default: return getStatusIcon("status/online",type);
		}	
	}
	
	static private ImageIcon getStatusIcon(String show,String type)
	{
		if(!statusIcons.containsKey(type)) type = "standard";
		ImageIcon icon = (ImageIcon)((Map)statusIcons.get(type)).get(show);
		if (icon == null) icon = (ImageIcon)((Map)statusIcons.get("standard")).get(show);
		return icon;
	}

	public void loadDefaultPics()
	{
		//load default smilies
	//	readSmilies(new BufferedReader(new InputStreamReader(PlgEmoticons.class.getResourceAsStream("emoticons/msn/info.cfg"))),null);
		// Now, search for and get the URL for this class.
		String urlString = getClass().getResource("StatusIcons.class").toString();
		//String urlString = url.toString();
		//System.out.println(url);
		try{
			urlString = urlString.substring(0,urlString.lastIndexOf("StatusIcons.class"));
			readImages(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("status.cfg"))),new URL(urlString));
			readImages(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("images.cfg"))),new URL(urlString));
		}catch(MalformedURLException e){e.printStackTrace(); }
	}
	
	
//	public static ImageIcon getImage(String location)
//	{
//		//get url for images
//		URL url = null;
//		try
//		{
//			if (Start.remoteURL != null)
//			{
//
//				url = new URL("jar:" + Start.remoteURL + location);
//			}
//			else url = new URL("jar:" + Start.localURL + location);
//		}
//		catch (MalformedURLException e)
//		{
//			e.printStackTrace();
//		}
//		return new ImageIcon(url);
//	}
	

	public void readImages(BufferedReader data,URL file)
	{
	    try
		{
			Map map = new HashMap();
			statusIcons.put(data.readLine(),map);
			while(true)
			{
				readSmilie(data,file,map);
			}
		}
		catch(EOFException e)
		{
		    //System.out.println(smilies);
		}
		catch (IOException e2)
		{
		    e2.printStackTrace();
		}
	}

	private void readSmilie(BufferedReader data,URL file,Map current) throws IOException
	{
		StringBuffer buffer=new StringBuffer();
		while(true)
		{
			int in2 = data.read();
			if(in2 ==-1) throw new EOFException();
			char ca =(char)in2;
			if(ca ==' ')
			{
				current.put(new String(buffer),readImage(data,file));
				return;
			}
			buffer.append(ca);
		}
	}

	private Icon readImage(BufferedReader data,URL file) throws IOException
	{
		//Icon icon=null;
		java.net.URL picURL=null;
		String resource = data.readLine();
		picURL =new URL(file + resource);
		//System.out.println(picURL);
		//if(picURL ==null) System.err.println(resource +" not found");
		return new ImageIcon(picURL);
	}
	
	private void loadWebstartPics()
	{
		try
		{
			Enumeration m=getClass().getClassLoader().getResources("info.cfg");
			for(Enumeration s =m;s.hasMoreElements();)
			{
				try
				{
					//URL urlJar = new URL("jar:"+url+"!/");
					URL urlJar = (URL )s.nextElement();
					//JarFile jarFile = new JarFile(  ""  );
					//BufferedReader data =new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("info.cfg"))));

					BufferedReader data = null;
					data =new BufferedReader(new InputStreamReader(urlJar.openStream()));
					String url = urlJar.toString(); 
					url = url.substring(0,url.length()-8);
					//System.out.println(url);
					readImages(data,new URL(url));
				}
				catch(IOException e)
				{
					//System.out.println(currentFile.getName() + " is not a valid jar file");
					System.err.println(e.toString());
				}
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	
	
	
	
//		BufferedReader data = null;
//		try
//		{
//			//if(Start.programURL != null)
//			{
//				URL baseURL = new URL(Start.programURL + "plugins/statusicons/");
//				try
//				{
//					data =new BufferedReader(new InputStreamReader(new URL(baseURL + "list.txt").openStream()));
//				}
//				catch (IOException ex)
//				{
//					scanPics();
//					return;
//				}
//				while (true)
//				{
//					String file = data.readLine();
//					if (file ==null) break;//end of stream
//					readJAR(new URL(baseURL + file));
//				}
//			}
////			else
////			{
////				URL baseURL = new URL(Start.localURL + "plugins/statusicons/");
////				try
////				{
////					data =new BufferedReader(new InputStreamReader(new URL(baseURL + "list.txt").openStream()));
////				}
////				catch (IOException ex)
////				{
////					scanPics();
////					return;
////				}
////				while (true)
////				{
////					String file = data.readLine();
////					if (file ==null) break;//end of stream
////					readJAR(new URL(baseURL + file));
////				}
////			}
//		}
//		catch(IOException e){e.printStackTrace();}
//		finally 
//		{
//			if(data!=null)
//			{	
//				try
//				{
//					data.close();
//				}
//				catch (IOException e1)
//				{
//					e1.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private void scanPics()
//	{
//		String urlString = Start.path  + "plugins" + File.separator;
//		urlString += "statusicons" +  File.separator;
//		//System.out.println(urlString);
//		File path = new File(urlString);
//		File file[] = path.listFiles();
//		if(file == null)
//		{
//			System.err.println(I18N.gettext("main.error.statusicons_dir_not_found"));
//			return;
//		}
//		try
//        {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(urlString + "list.txt"));
//			for (int tel=0;tel<file.length;tel++)
//			{
//				File currentFile = file[tel];
//			   /*
//			   if (currentFile.isDirectory())
//			   {
//							//System.out.println(currentFile.getName());
//				try
//				{
//				 BufferedReader data =new BufferedReader(new FileReader(currentFile.getPath() +File.separatorChar + "info.cfg"));
//				 readImages(data,currentFile.toURL());
//				}
//				catch (FileNotFoundException e)
//				{//no config file skip dir
//				 System.out.println(currentFile.getName() +" Contains no 'info.cfg' file");
//				 continue;
//				}
//			   }
//			   */
//				if(currentFile.toString().endsWith(".jar"))
//				{
//					try{
//						readJAR(currentFile.toURL());
//						}catch(MalformedURLException e){}
//						writer.write(currentFile.getName() + "\r\n");
//				}
//			}
//			writer.close();
//		}
//		catch (IOException ex)
//		{
//			ex.printStackTrace();
//        }
//	}
//
//	private void readJAR(URL url)
//	{
//		try
//		{
//			URL urlJar = new URL("jar:"+url+"!/");
//			//JarFile jarFile = new JarFile(  ""  );
//			//BufferedReader data =new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry("info.cfg"))));
//
//			BufferedReader data = null;
//			data =new BufferedReader(new InputStreamReader(new URL(urlJar + "info.cfg").openStream()));
//			readImages(data,urlJar);
//		}
//		catch(IOException e)
//		{
//			//System.out.println(currentFile.getName() + " is not a valid jar file");
//			System.err.println(e.toString());
//		}
//	}

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
