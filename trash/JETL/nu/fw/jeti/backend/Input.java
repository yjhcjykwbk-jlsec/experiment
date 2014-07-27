package nu.fw.jeti.backend;


import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class Input extends Thread
{
	private InputSource in;
	private PacketReceiver backend;

    public Input(InputStream stream,PacketReceiver backend)
    {
		try{
		    //in = new InputSource(new LogInputStream(new InputStreamReader(stream,"UTF8")));
			in = new InputSource(new InputStreamReader(stream,"UTF8"));
		}catch(UnsupportedEncodingException e){e.printStackTrace();}
		this.backend = backend;
		start();
    }

	public void run()
	{
		try{
			
//			BufferedReader b=   new BufferedReader(in.getCharacterStream());
//
//			while(b.markSupported() )
//			{
//			    System.out.println(b.readLine());
//				sleep(100);
//
//			}
//		    

			/** @todo look at parsers */

//direct, higer mem?
//SAXParser parser =new org.apache.crimson.jaxp.SAXParserFactoryImpl().newSAXParser();


			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		     //org.apache.crimson.parser.Parser2 parser = new org.apache.crimson.parser.Parser2();
			 //parser.

			//parser.getParser().setDocumentHandler()
			//System.out.println(parser.isValidating());
			//System.out.println(parser.isNamespaceAware());

			JabberHandler jH = new JabberHandler(backend);
			((ConnectionPacketReceiver)backend).setJabberHandler(jH);
			
			//BufferedReader b=   new BufferedReader(in.getCharacterStream());

					/*
 						test read from stream
 					 	StringBuffer buffer = new StringBuffer(); 
						while(true )
						{
							char c = ((char)in.getCharacterStream().read() );
							System.out.println(c);
							buffer.append(c); 
							//sleep(100);
							if(buffer.indexOf("client" ) !=-1)
							{ 
							 	((ConnectionPacketReceiver)backend).connected("0");
							 	buffer = new StringBuffer();  
							}
						}
						*/
		    
			
			
			parser.parse(in,jH);

			//SAXParserFactory.newInstance().newSAXParser().parse(new File("test.xml"), new JabberHandler());
			//Parser parser = (Parser) new org.xml.sax.helpers.XMLReaderAdapter(new org.apache.crimson.parser.XMLReaderImpl());
			//Parser parser = new com.microstar.xml.SAXDriver();

		    //only crimson. saxparserfactory maakt hoop rommel aan
		//	XMLReader xmlReader = new org.apache.crimson.parser.XMLReaderImpl();
	//	    xmlReader.setContentHandler(new JabberHandler(handlers,connect));

//			xmlReader.parse(in);
			//parser.setDocumentHandler(new Test());

			//parser.parse(in);

		}
		catch(ParserConfigurationException pce){pce.printStackTrace();}
		catch (SAXParseException spe)
		{
			Exception e = spe.getException();
			if(e != null)
			{
				e.printStackTrace();
				return;
			}
			else
			{
				System.out.println(spe.getMessage());
			}
		}
		catch(SAXException se)
		{
			Exception e = se.getException();
			if(e != null)
			{
				e.printStackTrace();
				return;
			}
			else System.out.println(se.getMessage());
		}
		catch(IOException e)
		{
            // No need to do anything here.
			System.out.println(e.getMessage());
		}
        backend.inputDeath();
	}
	

//	//prints inputstream
//	class LogInputStream extends FilterReader
//	{
//		LogInputStream(Reader reader)
//		{
//			 super(reader);
//		}
//
//		public int read(char[] cbuf, int off, int len) throws IOException
//		{
//			int readed = super.read(cbuf, off, len);
//			if(readed == -1) System.out.println("end of stream detected readed");
//			else System.out.println(new String(cbuf, off,readed).trim());
//			return readed;
//		}
//
//		
//		public int read(char[] cbuf) throws IOException
//		{
//			int readed = super.read(cbuf);
//			System.out.println(new String(cbuf).trim());
//			return readed;
//		}
//	}
//	
	
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
