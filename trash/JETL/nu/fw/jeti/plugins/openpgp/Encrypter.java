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
 *	Created on 10-okt-2004
 */
 
package nu.fw.jeti.plugins.openpgp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Collection;
import java.util.Map;

import cryptix.message.*;
import cryptix.openpgp.PGPArmouredMessage;
import cryptix.openpgp.util.PGPArmoury;
import cryptix.pki.KeyBundle;

import nu.fw.jeti.jabber.JID;
import nu.fw.jeti.jabber.elements.Extension;
import nu.fw.jeti.jabber.elements.Presence;
import nu.fw.jeti.jabber.elements.XExtension;
import nu.fw.jeti.plugins.OpenPGP;
import nu.fw.jeti.ui.StdPreferencesPanel;

/**
 * @author E.S. de Boer
 *
 */
public class Encrypter implements OpenPGP
{
	private static Encrypter encrypter;
	private Map keys;
	
	public Encrypter(Map keys)
	{
		encrypter = this;
		this.keys = keys;
	}
	
	public static Encrypter getInstance()
	{
		return encrypter;
	}
	
	public Presence signPresence(int show, String status,int priority)
	{
		return Plugin.signPresence(show,status,priority);
	}
	
	public boolean canEncrypt(JID jid)
	{
		return keys.containsKey(jid);
	}
	
	public XExtension  encrypt(String text)
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

            FileInputStream in = new FileInputStream("bob-public.pgp.asc");

            MessageFactory mf = MessageFactory.getInstance("OpenPGP");
            Collection msgs = mf.generateMessages(in);
            
            KeyBundleMessage kbm = (KeyBundleMessage)msgs.iterator().next();
            
            bundle = kbm.getKeyBundle();

            in.close();
        
        } catch (IOException ioe) {
            System.err.println("IOException... You did remember to run the "+
                "GenerateAndWriteKey example first, right?");
            ioe.printStackTrace();
            
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP MessageFactory. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
            
        } catch (MessageException me) {
            System.err.println("Reading keybundle failed.");
            me.printStackTrace();
            
        }


        //**********************************************************************
        //  Build the literal message.
        //**********************************************************************
        LiteralMessage litmsg = null;
        try {
            
            LiteralMessageBuilder lmb = 
                LiteralMessageBuilder.getInstance("OpenPGP");
            lmb.init(text.getBytes());
            litmsg = (LiteralMessage)lmb.build();
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP LiteralMessageBuilder."+
                " This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
            
        } catch (MessageException me) {
            System.err.println("Creating the literal message failed.");
            me.printStackTrace();
            
        }
        

        //**********************************************************************
        // Encrypt the message.
        //**********************************************************************
        Message msg = null;
        try {
            
            EncryptedMessageBuilder emb = 
                EncryptedMessageBuilder.getInstance("OpenPGP");
            emb.init(litmsg);
            emb.addRecipient(bundle);
            emb.setAttribute("compressed", "true");
            msg = emb.build();
        
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP "+
                "EncryptedMessageBuilder. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
         
        } catch (MessageException me) {
            System.err.println("Creating the encrypted message failed.");
            me.printStackTrace();
         
        }
        

        //**********************************************************************
        // Armour the message and write it to disk
        //**********************************************************************
        try {
            
            //PGPArmouredMessage armoured;
            //PGPArmoury.armour(msg.getEncoded());
                        
            return new XEncrypted(PGPArmoury.armour(msg.getEncoded()));
        
        } catch (MessageException me) {
            System.err.println("Writing the encrypted message failed.");
            me.printStackTrace();
        }
		return null;
	}
	
	public String decrypt(Extension extension)
	{
		if(!(extension instanceof XEncrypted)) return null;
		System.out.println("decrypting....");
		String ciphertext = ((XEncrypted)extension).getCrypted();
		ciphertext = ciphertext.replaceAll("\n","\r\n");
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
        MessageFactory mf = null;
        
        try {

            FileInputStream in = new FileInputStream("bob-secret.pgp.asc");

            mf = MessageFactory.getInstance("OpenPGP");
            Collection msgs = mf.generateMessages(in);
            
            KeyBundleMessage kbm = (KeyBundleMessage)msgs.iterator().next();
            
            bundle = kbm.getKeyBundle();

            in.close();
        
        } catch (IOException ioe) {
            System.err.println("IOException... You did remember to run the "+
                "GenerateAndWriteKey example first, right?");
            ioe.printStackTrace();
            System.exit(-1);
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("Cannot find the OpenPGP MessageFactory. "+
                "This usually means that the Cryptix OpenPGP provider is not "+
                "installed correctly.");
            nsae.printStackTrace();
            System.exit(-1);
        } catch (MessageException me) {
            System.err.println("Reading keybundle failed.");
            me.printStackTrace();
            System.exit(-1);
        }


        //**********************************************************************
        //  Read the message.
        //**********************************************************************
        EncryptedMessage em = null;
        
        try {

           
		   

            Collection msgs = mf.generateMessages(new ByteArrayInputStream(PGPArmoury.unarmour(ciphertext)));
            em = (EncryptedMessage)msgs.iterator().next();

            
        
        } catch (IOException ioe) {
            System.err.println("IOException... You did remember to run the "+
                "Encrypt example first, right?");
            ioe.printStackTrace();
            System.exit(-1);
        } catch (MessageException me) {
            System.err.println("Reading message failed.");
            me.printStackTrace();
            System.exit(-1);
        }
        

        //**********************************************************************
        // Decrypt the message.
        //**********************************************************************
        try {

            Message msg = em.decrypt(bundle, "TestingPassphrase".toCharArray());
            System.out.println(((LiteralMessage)msg).getTextData());
            return ((LiteralMessage)msg).getTextData();
        
        } catch (NotEncryptedToParameterException netpe) {
            System.err.println("Not encrypted to this key.");
            netpe.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException uke) {
            System.err.println("Invalid passphrase.");
            uke.printStackTrace();
            System.exit(-1);
        } catch (MessageException me) {
            System.err.println("Decrypting message failed.");
            me.printStackTrace();
            System.exit(-1);
        }
        return null;
    
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
