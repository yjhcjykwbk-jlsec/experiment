package nu.fw.jeti.backend;

import nu.fw.jeti.jabber.elements.Packet;
import nu.fw.jeti.jabber.handlers.ExtensionHandler;
import nu.fw.jeti.jabber.handlers.PacketHandler;
import nu.fw.jeti.jabber.handlers.UnknownPacketHandler;
import nu.fw.jeti.util.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class JabberHandler extends DefaultHandler
{
	//private Writer out;
	private int depth;//diepte in xml doc
	private PacketHandler packetHandeler;
	private ExtensionHandler extensionHandler;
	private Handlers handlers;
	private PacketReceiver packetReceiver;
	private StringBuffer completeXML = new StringBuffer();
	//private boolean inExtension = false;

    public JabberHandler(PacketReceiver connect)
    {
		this.handlers =((ConnectionPacketReceiver)connect).getHandlers();
		this.packetReceiver = connect;
		/*
		try{
			out = new OutputStreamWriter(System.out, "UTF8");
		}catch(IOException e){e.printStackTrace();}
		*/
    }

	public void changePacketReceiver(PacketReceiver reciever)
	{
	    packetReceiver = reciever;
	}

	public void startDocument()
	{
	   //System.out.println("<?xml version='1.0' encoding='UTF-8'?>");
	}

	public void endDocument(){ System.out.println("end document");}



	public void startElement(String namespaceURI,
							String sName, // simple name
							String qName, // qualified name
							Attributes attrs)
							throws SAXException
	{
		//System.out.println(qName);
		//System.out.println(attrs.);
		try{
			switch(depth++)
			{
				case 0:
					if("stream:stream".equals(qName))
					{
						String sessionID = attrs.getValue("id");
						//connected only when connecting so cast to connect make interface for register?
						((ConnectionPacketReceiver)packetReceiver).connected(sessionID);
						if(sessionID == null) throw new SAXException("session id = null");
						return;
					}
					else if(qName.equals("stream:error")) break;
					//System.out.println("no stream");
					throw new SAXException("No stream:stream element");
				case 1://iq message and presence
					completeXML = new StringBuffer();//new packet so clear stringbuffer
					if(qName.equals("stream:error")) break;
					packetHandeler = handlers.getPacketHandler(qName);
					extensionHandler = null;
					if(packetHandeler == null)
					{//unknown handler
						Log.notParsedXML(qName + " is not a message, presence or iq packet");
						packetHandeler = new UnknownPacketHandler();
					}
					packetHandeler.startHandling(attrs);
					break;
				default:
					String xmlns = attrs.getValue("xmlns");
					if(xmlns !=null)
					{//if contains xmlns new extension //extension in extension?
						ExtensionHandler tempExtensionHandler = handlers.getExtensionHandler(xmlns);
						if(tempExtensionHandler == null)
						{// unknown extension handler
							tempExtensionHandler = handlers.getExtensionHandler("unknown");
						}
						if (extensionHandler != null) tempExtensionHandler.setParent(extensionHandler);//extension in extension
						extensionHandler = tempExtensionHandler;
						extensionHandler.setName(qName);
						extensionHandler.startHandling(attrs);
					}
					else if(extensionHandler !=null)
					{//no new extension
						extensionHandler.up();
						extensionHandler.startElement(qName,attrs);
					}
					else packetHandeler.startElement(qName, attrs);
			}
			completeXML.append("<");
			completeXML.append(qName);
			if (attrs != null) {
				String aName = null;
				for (int i = 0; i < attrs.getLength(); i++) {
					aName = attrs.getQName(i);
					completeXML.append(" ");
					completeXML.append(aName+"=\""+attrs.getValue(i)+"\"");
				}
			}
			completeXML.append(">");
	   } catch (Exception e){throw new SAXException(e);}
	}

	public void endElement(String namespaceURI,
						   String sName, // simple name
						   String qName  // qualified name
							)
							throws SAXException
	{
		try{
			completeXML.append("</");
			completeXML.append(qName);
		    completeXML.append(">");
			switch(--depth){
			case 0:   // We're back at the end of the root
			  break;
			case 1:// The Packet is finished
				Log.completeXML(completeXML);
				if(qName.equals("stream:error"))
				{
					//System.out.println(completeXML);
					packetReceiver.streamError();
					break;
				}
				try{
					Packet packet = packetHandeler.build();
					Log.xmlPacket(packet);
					packetReceiver.receivePackets(packet);
				}catch(InstantiationException e){Log.xmlParseException(e);}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
			default:  // Move back up the tree
				if(extensionHandler != null)
				{//in extension
					//if(extensionHandler.getName().equals(qName))//name matched start tag so close extension
					if(extensionHandler.isTop())
					{
						ExtensionHandler parent = extensionHandler.getParent();
						if(parent != null)
						{//extension has parent add this extension to parent
							try{
								parent.addExtension(extensionHandler.build());
							}catch(InstantiationException e){Log.xmlParseException(e);}
							extensionHandler = parent;
						}
						else
						{//add extension to packet
							try{
								packetHandeler.addExtension(extensionHandler.build());
							}catch(InstantiationException e){Log.xmlParseException(e);}
							extensionHandler = null;
						}
					}// element end inside a extension
					else
					{
						extensionHandler.down();
						extensionHandler.endElement(qName);
					}
				}// element end inside a packet
				else packetHandeler.endElement(qName);
			}
		} catch (Exception e){throw new SAXException(e);}
	}

	public void characters(char buf[], int offset, int len)	throws SAXException
	{
		try{
			String text = new String(buf, offset,len); //.trim();
			if(extensionHandler != null) extensionHandler.characters(text);
			else if (packetHandeler!=null)  packetHandeler.characters(text);
			completeXML.append(text.trim());
		} catch (Exception e){throw new SAXException(e);}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
