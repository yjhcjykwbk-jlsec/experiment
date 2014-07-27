// Created on 18-okt-2003
package nu.fw.jeti.plugins.tts;

import java.io.File;
import java.io.FileWriter;
import java.util.Locale;

import javax.speech.*;
import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.plugins.Plugins;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins,MessageListener
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "text to speach, let Jeti speak messages";
	public final static String MIN_JETI_VERSION = "0.5.1";
	public final static String NAME = "tts";
	public final static String ABOUT = "uses FreeTTS (freetts.sourceforge.net)";
	private static Plugin plugin;
	private  Synthesizer synthesizer1;
	
	public static void init(Backend backend) throws Exception
	{
		plugin = new Plugin (backend);
	}
	
	public Plugin(final Backend backend) throws Exception
	{
		//locate speech.properties
//		File location = new File(System.getProperty("user.home")+ File.separator + "speech.properties");
//		if(!location.canRead())
//		{	
//			File location2  = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator +"speech.properties");
//			if(!location2.canRead())
//			{
//				try {
//				location.createNewFile();
//				FileWriter w= new FileWriter(location);
//				w.write("FreeTTSSynthEngineCentral=com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
//				w.flush();
//				w.close();
//				}
//				catch (Exception e)
//				{
//					throw new InstantiationException(noSynthesizerMessage("unlimited domain synthesizer"));
//				}
//			}
//		}
		
		
		System.out.println(" ** Loading tts **");
		// kevinHQ in a 16khz unlimited-domain diphone voice
		Voice kevinHQ = new Voice("kevin16",Voice.GENDER_DONT_CARE, Voice.AGE_DONT_CARE, null);
			
		// Create a new SynthesizerModeDesc that will match the 
		// Unlimited domain FreeTTS Synthesizer.
		
		SynthesizerModeDesc unlimitedDesc = 
		new SynthesizerModeDesc(
				"Unlimited domain FreeTTS Speech Synthesizer from Sun Labs",
				null,
				Locale.US,
				Boolean.FALSE,         // running?
				null);                 // voice
		
//		synthesizer1 = Central.createSynthesizer(unlimitedDesc);
//		if (synthesizer1 == null) {
//			throw new InstantiationException(noSynthesizerMessage("unlimited domain synthesizer"));
//		}
		
		//-----------
		 FreeTTSEngineCentral central = new FreeTTSEngineCentral();
		 
		 EngineList list = central.createEngineList(unlimitedDesc);

		 if (list.size() > 0) {
		 EngineCreate creator = (EngineCreate) list.get(0);
		 synthesizer1 = (Synthesizer) creator.createEngine();
		 }

		 if (synthesizer1 == null) {
		 	throw new InstantiationException(noSynthesizerMessage("unlimited domain synthesizer"));
		 }
		//-------
					
		System.out.print("  Allocating synthesizers...");
		synthesizer1.allocate();
				
		// get it ready to speak
		System.out.print("Loading voices...");
		synthesizer1.getSynthesizerProperties().setVoice(kevinHQ);
				
		System.out.println("And here we go!");
		backend.addListener(MessageListener.class,this);
	}
	
	public void unload() {}
	
	public static void unload(Backend backend)
	{
		backend.removeListener(MessageListener.class,plugin);
		try
		{
			plugin.synthesizer1.deallocate();
		}
		catch (EngineException e)
		{
			e.printStackTrace();
		}
		plugin = null;
	}
	
	public void message(final Message message)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				try
				{
					if(message.getBody() == null)return;
					String nick = message.getFrom().getUser();
					JIDStatus jidStatus = Backend.getJIDStatus(message.getFrom());
					if(jidStatus!=null) nick = jidStatus.getNick();
					if(nick == null)return;
					synthesizer1.resume();
					synthesizer1.speakPlainText(nick + " says  ", null);
					synthesizer1.speakPlainText(message.getBody(), null);
					synthesizer1.waitEngineState(Synthesizer.QUEUE_EMPTY);
				}
				catch (AudioException e)
				{
					System.out.println(e.getMessage());
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
					//e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	private String noSynthesizerMessage(String synthesizer) {
		String message = "Can't find " + synthesizer + ".\n" +
		"Make sure that there is a \"speech.properties\" file at either " +
		"of these locations: \n";
		message += "user.home    : " + System.getProperty("user.home") + "\n";
		message += "java.home/lib: " + System.getProperty("java.home")
		+ File.separator + "lib\n";
		return message;
	}
	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
