/* 
 *	Jeti, a Java Jabber client, Copyright (C) 2004 E.S. de Boer  
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
 *  or mail me at eric@jeti.tk or Jabber at jeti@jabber.org
 *
 *	Created on 9-okt-2004
 */
 
package nu.fw.jeti.plugins.openpgp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nu.fw.jeti.events.PresenceListener;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.elements.XExtension;
import nu.fw.jeti.plugins.OpenPGP;
import nu.fw.jeti.plugins.Plugins;
import cryptix.message.*;
import cryptix.openpgp.PGPDetachedSignatureMessage;
import cryptix.openpgp.PGPSignedMessage;
import cryptix.openpgp.util.PGPArmoury;
import cryptix.pki.KeyBundle;
import cryptix.pki.KeyBundleException;
import cryptix.pki.KeyBundleFactory;

//TODO add security questions

/**
 * @author E.S. de Boer
 *
 */
public class Plugin implements Plugins, PresenceListener
{
	public final static String VERSION = "0.1";
	public final static String DESCRIPTION = "ants.End_to_end_security";
	public final static String MIN_JETI_VERSION = "0.6";
	public final static String NAME = "openpgp";
	public final static String ABOUT = "by E.S. de Boer";
	private static Plugin plugin;
	//private Backend backend;
	private Map keys= new HashMap();
			
	public Plugin(Backend backend)
	{
		//this.backend=backend;
		new Encrypter(keys);
		backend.addListener(PresenceListener.class,this);
		backend.addExtensionHandler("jabber:x:signed",new XSignedHandler());
		backend.addExtensionHandler("jabber:x:encrypted",new XEncryptedHandler());
	}
	
	public static void init(Backend backend)
    {
    	plugin = new Plugin(backend);
    	
    }

	public void unload()
	{

	}
	
	public static Object getInstance()
	{
		return Encrypter.getInstance();
	}

	public static void unload(Backend backend)
	{
		backend.removeListener(PresenceListener.class, plugin);
		backend.removeExtensionHandler("jabber:x:signed");
		backend.removeExtensionHandler("jabber:x:encrypted");
		plugin.unload();
		plugin = null;
	}
	
//	public boolean verify(KeyBundle pubkey) throws MessageException
//    {
//        return verify((PublicKey)pubkey.getPublicKeys().next());
//    }
	
	public static Presence signPresence(int show, String status,int priority)
	{
		XExtension signed =null;
		if(status!=null)
		{
			signed = new XSigned(sign(status));
		}
		return new Presence(show,status,priority,signed);
	}
	
	public static String sign(String plainText)
	{
		    //**********************************************************************
	        // Dynamically register both the Cryptix JCE and Cryptix OpenPGP 
	        // providers. 
	        //**********************************************************************
	        java.security.Security.addProvider(
	            new cryptix.jce.provider.CryptixCrypto() );
	        java.security.Security.addProvider(
	            new cryptix.openpgp.provider.CryptixOpenPGP() );


	        //**********************************************************************
	        // First read the key. 
	        //**********************************************************************
	        KeyBundle bundle = null;
	        
	        try {

	            FileInputStream in = new FileInputStream("alice-secret.pgp");

	            KeyBundleFactory kbf = 
	                KeyBundleFactory.getInstance("OpenPGP");
	            bundle = kbf.generateKeyBundle(in);

	            in.close();
	        
	        } catch (IOException ioe) {
	            System.err.println("IOException... You did remember to run the "+
	                "GenerateAndWriteKey example first, right?");
	            ioe.printStackTrace();
	            
	        } catch (NoSuchAlgorithmException nsae) {
	            System.err.println("Cannot find the OpenPGP KeyBundleFactory. "+
	                "This usually means that the Cryptix OpenPGP provider is not "+
	                "installed correctly.");
	            nsae.printStackTrace();
	            
	        } catch (KeyBundleException kbe) {
	            System.err.println("Reading keybundle failed.");
	            kbe.printStackTrace();
	            
	        }


	        //**********************************************************************
	        // Create the message.
	        //**********************************************************************
	        Message msg = null;
	        
	        try {
	        
	            LiteralMessageBuilder lmb = 
	                LiteralMessageBuilder.getInstance("OpenPGP");

	            lmb.init(plainText.getBytes());

	            msg = lmb.build();
	            
	        } catch (NoSuchAlgorithmException nsae) {
	            System.err.println("Cannot find the OpenPGP LiteralMessageBuilder. "+
	                "This usually means that the Cryptix OpenPGP provider is not "+
	                "installed correctly.");
	            nsae.printStackTrace();
	            
	        } catch (MessageException me) {
	            System.err.println("Generating the message failed.");
	            me.printStackTrace();
	            
	        }
	        
	        PGPSignedMessage message=null;
	        //**********************************************************************
	        // Sign the message.
	        //**********************************************************************
	        try {
	        
	            SignedMessageBuilder smb = 
	                SignedMessageBuilder.getInstance("OpenPGP");

	            smb.init(msg);
	            smb.addSigner(bundle, "TestingPassphrase".toCharArray());

	            message = (PGPSignedMessage)smb.build();
	        
	        } catch (NoSuchAlgorithmException nsae) {
	            System.err.println("Cannot find the OpenPGP SignedMessageBuilder. "+
	                "This usually means that the Cryptix OpenPGP provider is not "+
	                "installed correctly.");
	            nsae.printStackTrace();
	            
	        } catch (UnrecoverableKeyException uke) {
	            System.err.println("Incorrect passphrase.");
	            uke.printStackTrace();
	            
	        } catch (MessageException me) {
	            System.err.println("Generating the message failed.");
	            me.printStackTrace();

	        }


	        //**********************************************************************
	        // Armour the message.
	        //**********************************************************************
	        try{
	        	return  PGPArmoury.armour(message.getDetachedSignature().getEncoded());
	        } catch (MessageException me) {
	        	System.err.println("Writing the message failed.");
        		me.printStackTrace();
	        }
	        return null;
	        
//	        //**********************************************************************
//	        // Write the message.
//	        //**********************************************************************
//	        try {
//	        
//	            FileOutputStream out = new FileOutputStream("cleartext.asc");
//	            out.write(msg.getEncoded());
//	            out.close();
//
//	        } catch (IOException ioe) {
//	            System.err.println("Writing the message failed.");
//	            ioe.printStackTrace();
//	            System.exit(-1);
//	        } catch (MessageException me) {
//	            System.err.println("Writing the message failed.");
//	            me.printStackTrace();
//	            System.exit(-1);
//	        }

	    }
	
	public void presenceChanged(Presence presence)
	{
		if(presence.hasExtensions())
		{
			for(Iterator i = presence.getExtensions();i.hasNext();)
			{
				Object o = i.next();
				if(o instanceof XSigned)
				{
					verify(presence.getFrom(),((XSigned)o).getSigned(),presence.getStatus());
					break;
				}
			}
		}
	}
	
	private void verify(JID jid,String signed,String plain)
	{
		signed = signed.replaceAll("\n","\r\n");
		
		
		//**********************************************************************
        // First read the key. 
        //**********************************************************************
        KeyBundle bundle = null;
        
        try {

            FileInputStream in = new FileInputStream("alice-public.pgp");

            KeyBundleFactory kbf = 
                KeyBundleFactory.getInstance("OpenPGP");
            bundle = kbf.generateKeyBundle(in);

            in.close();
        
        } catch (IOException ioe) {
            System.err.println("IOException... You did remember to run the "+
                "GenerateAndWriteKey example first, right?");
            ioe.printStackTrace();
      
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP KeyBundleFactory. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
      
        } catch (KeyBundleException kbe) {
            System.err.println("Reading keybundle failed.");
            kbe.printStackTrace();
      
        }
        //**********************************************************************
        // Read the signed file
        //**********************************************************************
        Message originalMsg = null;
        
        try {
        	
          
            LiteralMessageBuilder lmb = 
                LiteralMessageBuilder.getInstance("OpenPGP");
            lmb.init(plain.getBytes());
            originalMsg = lmb.build();

        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP LiteralMessageBuilder."+
                " This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
         
        } catch (MessageException me) {
            System.err.println("Reading the message failed.");
            me.printStackTrace();
        }
        

        
        
        
        //**********************************************************************
        // Read the detached signature
        //**********************************************************************
        
             
        
        PGPDetachedSignatureMessage detachedSig = null;
                
        
        
        try {
        
            MessageFactory mf = MessageFactory.getInstance("OpenPGP");
            
            System.out.println(signed);
                      
            byte[] bm = PGPArmoury.unarmour(signed);
						
            detachedSig = (PGPDetachedSignatureMessage)
                           mf.generateMessages(new ByteArrayInputStream(bm)).iterator().next();
            
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP MessageFactory. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
            
        } catch (MessageException me) {
            System.err.println("Reading the message failed.");
            me.printStackTrace();
            
        } catch (IOException ioe) {
            System.err.println("Reading the message failed.");
            ioe.printStackTrace();
            
        }
        

        //**********************************************************************
        // Verify the message.
        //**********************************************************************
        try {
        
            if (detachedSig.verify(originalMsg, bundle)) {
                System.out.println("Signature OK");
                keys.put(jid,bundle);
            } else {
                System.out.println("Signature BAD");
            }
        
        } catch (MessageException me) {
            System.err.println("Verifying the signature failed.");
            me.printStackTrace();
     
        }
	}
		
	public static void main(String[] args)
	{
		 //**********************************************************************
        // Dynamically register both the Cryptix JCE and Cryptix OpenPGP 
        // providers. 
        //**********************************************************************
        java.security.Security.addProvider(
            new cryptix.jce.provider.CryptixCrypto() );
        java.security.Security.addProvider(
            new cryptix.openpgp.provider.CryptixOpenPGP() );
		
//		verify(null,"iEYEABECAAYFAkFpO2gACgkQYQ1jZZhwZIMjmACglIzk17S9xHn1H3dUpz/FcNEi\r\n" +
//		"UIsAn1659iftrfWP1ttjeEcgyh33WF4G\r\n"+
//		"=NWob\r\n","available");

	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
