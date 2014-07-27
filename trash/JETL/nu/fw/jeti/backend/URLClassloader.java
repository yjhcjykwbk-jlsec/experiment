package nu.fw.jeti.backend;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class URLClassloader extends URLClassLoader
{
	public URLClassloader(URL[] urls,ClassLoader parent)
	{
		super(urls,parent); 
	}

	public void addURL(URL url)
	{
		super.addURL(url);
	}
	
//	public Class loadClass(String name) throws ClassNotFoundException 
//	{
//		System.out.println(name);
//		return super.loadClass(name);
//	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
