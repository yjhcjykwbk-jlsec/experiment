import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Window;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;



/**
 * remote Load jeti
 * use it to load jeti from network 
 * or on a multi user system
 * (only config files and logs are stored localy)
 */
public class IM extends Window
{
	private Label from;
	
	public IM(Frame frame)
	{
		super(frame);
		Label label = new Label("Jeti is Loading...");
		label.setFont(new java.awt.Font("Serif", 1, 40)); 
		label.setBackground(new Color(150,150,255));
		add(label,BorderLayout.CENTER);
		from = new Label("Loading");
		from.setBackground(new Color(255,255,150));
		add(from,BorderLayout.SOUTH); 
		pack();
		setLocationRelativeTo(null);
	}
	
	
    public static void main(String[] args) 
    {
		String path = System.getProperty("user.dir");
		path = path.replace('\\','/');
		path = "file:/" + path + "/";
		System.out.println(path);
//    	try
//		{
//			//System.out.println(args[0]);
//			//
//			System.out.println(new File(".").toURL());// getCanonicalPath());
//		}
//		catch (IOException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		//if(true) return;
    	Frame frame = new Frame();
    	IM im = new IM(frame);
    	im.show();
		URL url = null;
		URL url2[] = new URL[1];
		try {
			//url = new URL ("file:/e:/data/java/jeti/");
			//url = new URL ("file:/z:/data/java/2m/classes/");
			//url = new URL ("http://192.168.100.2/~eric/");
		    url = new URL(args[0]);

		   url2[0] = new URL (url + "Jeti.jar");
		   im.from.setText("from: " + url2[0].toString());
		   //url2[0] = url;
		   System.out.println(url2[0]);
		} catch (MalformedURLException e) 
		{
			im.from.setText(e.getMessage());
			e.printStackTrace();
		}
		URLClassLoader loader = new URLClassLoader(url2);
		Class start = null;
		
//		String path = null;
//		URL urlPath =null;
//		urlPath = IM.class.getResource("IM.class");
//		try{//remove %20 from program files etc
//			path = URLDecoder.decode(urlPath.toString(),"UTF8");//encode if to url? probaly not
//		}catch (Exception e){e.printStackTrace();}//1.2 error + 1.4
//		path = path.substring(0,path.lastIndexOf("/")+1);

		try{
			start = loader.loadClass("nu.fw.jeti.backend.Start");
			Method m = start.getMethod("remoteLoad",new Class[]{java.net.URL.class,String.class});
			m.invoke(null,new Object[]{url,path});
		}catch(Exception e){e.printStackTrace();}
		im.dispose();
		frame.dispose();  
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
