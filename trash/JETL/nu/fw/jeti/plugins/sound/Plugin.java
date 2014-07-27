/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2003 E.S. de Boer  
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *	For questions, comments etc, 
 *	use the website at http://jeti.jabberstudio.org
 *  or mail me at eric@jeti.tk
 *
 *	Created on 6-nov-2003
 */
 
package nu.fw.jeti.plugins.sound;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.sound.sampled.*;

import nu.fw.jeti.events.MessageListener;
import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.events.StatusChangeListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.elements.Message;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.plugins.Plugins;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, MessageListener,PresenceListener,StatusChangeListener
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "sound.Plays_a_sound_on_new_message_or_person_available";
	public final static String MIN_JETI_VERSION = "0.5.1";
	public final static String NAME = "sound";
	public final static String ABOUT = "by E.S. de Boer";
	public final static String PARENT = "Notifiers";

    private final static int MESSAGE = 0;
    private final static int ONLINE  = 1;
    private final static int OFFLINE = 2;
    private long lastTime[] = {0, 0, 0};

	private static Plugin plugin;
	private Map sounds;
	private PlayList playList;
	private boolean singleLine=false;
		
	public Plugin(final Backend backend)
	{
        initSounds();

		if(sounds.get("message") != null) {
            backend.addListener(MessageListener.class,this);
        }
		if(sounds.get("online")!=null || sounds.get("offline")!=null) {
            backend.addListener(PresenceListener.class,this);
        }
		if(sounds.get("own_online")!=null || sounds.get("own_offline")!=null) {
            backend.addListener(StatusChangeListener.class,this);
        }
 	}
	
	public static void init(Backend backend)
	{
		plugin = new Plugin(backend);
	}
			
	public void unload() 
	{
		playList.stopPlaying();
		sounds.clear();
	}
	
	public static void unload(Backend backend)
	{
		plugin.unload();
		backend.removeListener(MessageListener.class,plugin);
		backend.removeListener(PresenceListener.class,plugin);
		plugin = null;
	}

	public static void reloadSounds()
    {
        plugin.initSounds();
    }

    private void initSounds()
    {
		sounds = new HashMap(10);

		if (System.getProperty("os.name").equals("OS/2")) singleLine=true;
		loadSound("message",Preferences.getString("sound","message","STEELYEC.WAV"));
		loadSound("online",Preferences.getString("sound","online","BEEPPURE.WAV"));
		loadSound("offline",Preferences.getString("sound","offline","WHOOSH.WAV"));
		loadSound("own_online",Preferences.getString("sound","own_online","BEEPBASS.WAV"));
		loadSound("own_offline",Preferences.getString("sound","own_offline","BADUMM.WAV"));

        if (playList != null) {
            playList.stopPlaying();
        }
		playList = new PlayList(sounds,singleLine);
		playList.start();
    }

	private void loadSound(String sound,String path)
	{
		if(!Preferences.getBoolean("sound",sound +".enabled",true)) return;
		if(singleLine)
		{
			loadSoundSingleLine(sound,path);
			return;
		}
		try{
	 		AudioInputStream stream;
	 		URL loadedSound = getClass().getResource(path);
	 		//System.out.println(loadedSound);
	 		if(loadedSound!=null)stream=AudioSystem.getAudioInputStream(new BufferedInputStream(loadedSound.openStream()));
	    	else stream=AudioSystem.getAudioInputStream(new File(path));
	        Line.Info linfo = new Line.Info (Clip.class);
	        Clip clip=(Clip)AudioSystem.getLine(linfo);
	        //Clip clip=(Clip)mixer.getLine(linfo);
	        clip.open(stream);
	        sounds.put(sound,clip);
     
		}catch (IOException e)
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("sound.{0}_not_found"),new Object[]{sound}),I18N.gettext("sound.Sound_not_found"));
		}
		catch (UnsupportedAudioFileException e)
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("sound.{0}_is_an_unsupported_format"),new Object[]{sound}),I18N.gettext("sound.Format_not_supported"));
		}
		catch(LineUnavailableException e)
		{
			System.err.println(I18N.gettext("sound.Problem_playing_sound_check_your_sound_system"));	
		}
	}
	
	private void loadSoundSingleLine(String sound,String path)
	{//preload sound, no clip because that doesn't work on OS/2
		
		try{
			AudioInputStream stream;
			URL loadedSound = getClass().getResource(path);
	 		if(loadedSound!=null)stream=AudioSystem.getAudioInputStream(new BufferedInputStream(loadedSound.openStream()));
	    	else stream=AudioSystem.getAudioInputStream(new File(path));
					
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nBufferSize = 1024 * stream.getFormat().getFrameSize();
			byte[]	abBuffer = new byte[nBufferSize];
			int nBytesRead;
			while ((nBytesRead = stream.read(abBuffer))>0 )
			{
				baos.write(abBuffer, 0, nBytesRead);
			}
			sounds.put(sound,new Object[]{stream.getFormat(),baos.toByteArray()});
		}catch (IOException e)
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("sound.{0}_not_found"),new Object[]{sound}),I18N.gettext("sound.Sound_not_found"));
		}
		catch (UnsupportedAudioFileException e)
		{
			Popups.errorPopup(MessageFormat.format(I18N.gettext("sound.{0}_is_an_unsupported_format"),new Object[]{sound}),I18N.gettext("sound.Format_not_supported"));
		}
	}
	
	public static void test()
	{
		plugin.playList.addSound("message");
	}
	
	
	public void message(Message message)
	{
        if (!timeBlocked(MESSAGE)) {
        	if(message.getType().equals("groupchat"))
        	{//no sound on groupchat server messages
        		if(message.getFrom().getResource()==null) return;
        	}
        	playList.addSound("message");
        }
	}
	
	public void presenceChanged(Presence presence)
	{
		if(presence.getShow() == Presence.UNAVAILABLE)
		{
            if (!timeBlocked(OFFLINE)) {
                playList.addSound("offline");
            }
		}
		else if (presence.getShow() == Presence.AVAILABLE)
		{	
            if (!timeBlocked(ONLINE)) {
                playList.addSound("online");
            }
		}
	}
	
	public void exit(){}
	
	public void connectionChanged(boolean online)
	{
		if(online)playList.addSound("own_online");
		else playList.addSound("own_offline");
	}
	
	public void ownPresenceChanged(int show,String status){}

    private boolean timeBlocked(int index)
    {
        long now = System.currentTimeMillis();
        if (now - Preferences.getInteger("sound","block",2)*1000 < lastTime[index]){
            return true;
        } else {
            lastTime[index] = now;
            return false;
        }
    }

    class PlayList extends Thread
    {
        private LinkedList queue = new LinkedList();
        private volatile boolean isRunning=true;
        private Map sounds;
        private boolean singleLine=false;
		
        public PlayList(Map sounds,boolean singleLine)
        {
            this.sounds = sounds;
            this.singleLine = singleLine;
        }

        public void addSound(String sound)
        {
            if(isRunning)
            {
                if(!Preferences.getBoolean("sound",sound+".enabled",true)) return;
                Object clip = sounds.get(sound);
                if(!queue.contains(clip))
                {
                    synchronized(queue)
                    {
                        queue.addLast(clip);
                        queue.notifyAll();
                    }
                }
            }
        }

        public void stopPlaying()
        {
            isRunning = false;
            synchronized(queue){queue.notifyAll();}
        }
	
        public final void run()
        {
            Clip clip=null;
            byte[] data=null;
            AudioFormat format=null;
            while (isRunning)
            {
                synchronized(queue)
                {
                    if (queue.isEmpty())
                    {
                        if(isRunning)
                        {
                            try
                            {
                                queue.wait();
                            }
                            catch(InterruptedException e)
                            {//bug when thrown? called when interrupted
                                e.printStackTrace();
                                return;
                            }
                        }
                        continue;
                    }
                    else 
                    {
                        if(singleLine)
                        {
                            Object[] temp =(Object[]) queue.removeFirst();
                            format=(AudioFormat)temp[0];
                            data = (byte[])temp[1];
                        }
                        else clip = (Clip)queue.removeFirst();
                    }
                }
                if(singleLine)
                {
                    playSingleLine(data, format);
                }
                else
                {
                    clip.setFramePosition(0);
                    if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
                    {
                        FloatControl flc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float volume = Preferences.getInteger("sound","volume",0);
                        if (volume >flc.getMaximum())
                        {
                            volume=flc.getMaximum();
                        }
                        flc.setValue(volume);
                    }
                    clip.start();
                }
            }
            //cleanup
            if(!singleLine)
            {
                for(Iterator i= queue.iterator();i.hasNext();)
                {
                    ((Clip)i.next()).close();
                }
            }
        }
	
        private void playSingleLine(byte[] data, AudioFormat format)
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            AudioInputStream stream = new AudioInputStream(	bais, format,data.length / format.getFrameSize());

            Line.Info linfo = new Line.Info (Clip.class);
            Clip clip=null;
            try
            {
                clip = (Clip)AudioSystem.getLine(linfo);
                clip.open(stream);
            } catch (LineUnavailableException e3)
            {
                System.err.println(I18N.gettext("sound.Problem_playing_sound_check_your_sound_system"));
            } catch (IOException e3)
            {
                // TODO Auto-generated catch block
                e3.printStackTrace();
            }
				
            if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                FloatControl flc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = Preferences.getInteger("sound","volume",0);
                if (volume >flc.getMaximum())
                {
                    volume=flc.getMaximum();
                }
        	
                flc.setValue(volume);
            }
					
            //clip.setFramePosition(0);
            clip.start();	
            try
            {//wait until clip finishes and then close it so it works on OS/2
                sleep(clip.getMicrosecondLength()/1000);
                clip.stop();
                clip.close();
                clip=null;
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
