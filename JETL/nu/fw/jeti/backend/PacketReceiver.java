package nu.fw.jeti.backend;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public interface PacketReceiver
{
	public void receivePackets(nu.fw.jeti.jabber.elements.Packet packet);

	public void inputDeath();

	public void streamError();
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
