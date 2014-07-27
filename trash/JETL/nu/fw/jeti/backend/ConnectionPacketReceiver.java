package nu.fw.jeti.backend;


/**
 * @author E.S. de Boer
 * @version 1.0
 */

public interface ConnectionPacketReceiver extends PacketReceiver
{
	void setJabberHandler(JabberHandler jh);

	Handlers getHandlers();

	void connected(String connectionId);

	public void outputDeath();

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
