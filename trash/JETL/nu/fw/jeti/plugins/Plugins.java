package nu.fw.jeti.plugins;

/**
 * @author E.S. de Boer
 * @version 1.0
 * An Jeti plugin should be called Plugin and defined in a subpackage of nu.fw.jeti.plugins 
 * and have a constructor Plugin(nu.fw.jeti.backend Backend backend) that is called to initialize the plugin
 * 
 * 
 * 
 */
public interface Plugins
{

//back to interface??
	//String getName(){return NAME;}

	//abstract String getDescription();

	//abstract String getVersion();

	
	// public void unload(Backend backend);
	
	//remove??
	//public String[] getInfo(){return null;}
//TODO change to unload
	
	/**
	 * Perform unload cleaning of loaded plugins.
	 * Please make sure that your plugin instances 
	 * are not referenced anymore after this method 
 	 */
	 public void unload();

	 
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
