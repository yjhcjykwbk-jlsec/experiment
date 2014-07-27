package nu.fw.jeti.jabber;

import nu.fw.jeti.util.StringArray;

/**
 * Keeps a JID and the information about that JID together.
 * The JIDStatus is implemented in three classes
 * <ul>
 * <li>{@link nu.fw.jeti.backend.roster.PrimaryJIDStatus}</li>
 * <li>{@link nu.fw.jeti.backend.roster.NormalJIDStatus}</li>
 * <li>{@link nu.fw.jeti.backend.roster.ResourceJIDStatus}</li>
 * </ul>
 * These classes are part of the roster and are used to store and sort
 * information about the nickname and resources. This interface makes the most
 * used methods of those classes available, so the implementing classes are rarely needed.
 * The JIDStatussen available in the roster can be obtained by {@link Backend#getJIDStatus(JID)}
 * @author E.S. de Boer
 * @version 1.0
 */

public interface JIDStatus extends Comparable
{

	/**
	 * Gets the JID associated with this JIDStatus.
	 * @return the JID whitout resource associated with this JIDStatus.
	 */
	public JID getJID();

	/**
	 * Gets the JID with resource associated with this JIDStatus.
	 * @return If there is a known resource the JID with resource,
	 *         else the JID without the resource.
	 */
	public JID getCompleteJID();

	/**
	 * Returns true when the user is online.
	 * @return true when the user is online.
	 */
	public boolean isOnline();

	/**
	 * Gets the Show status of this JIDStatus.
	 * one of "chat", "available", "away", "dnd", "xa" or "unavailable".
	 * @return The show status of this JIDStatus.
	 */
	public int getShow();

	/**
	 * Gets the status of this JIDStatus, status is the user defined
	 * message (status can be null).
	 * @return The status of this JIDStatus.
	 */
	public String getStatus();

	/**
	 * Gets the nickname of this JIDStatus.
	 * If the nickname is unknown this method returns the
	 * JID assiocated with this JIDStatus.
	 * @return The nickname
	 */
	public String getNick();

	/**
	 * Gets the type of the transport this JIDStatus is registerd with.
	 * initialy this is "jabber". The type is browsed for the registerd transports,
	 * if the browse succeeded the type is changed in the type of the transport, when
	 * the browse fails the type is changed to "unknown".
	 * @return The type of this JIDStatus.
	 */
	public String getType();

	//public StringArray getNamespaces();

	/**
	 * Gets the subscription type of this JIDStatus
	 * can be null, "none" , "to", "from", or "both".
	 * @return The subscription type of this JIDStatus.
	 */
	public String getSubscription();

	/**
	 * Gets the waiting status of this JIDStatus
	 * can be null, "subscribe" or "unsubscribe".
	 * @return The waiting status of this JIDStatus.
	 */
	public String getWaiting();

	/**
	 *
	 * @return copy of the groups.
	 */
	StringArray getGroupsCopy();

	/**
	 * Counts the groups this JIDStatus is in.
	 * @return The number of groups this JIDStatus is in.
	 */
	public int groupCount();

	/**
	 * Returns true when this JIDStatus is in group.
	 * @param group The group that is tested.
	 * @return true when this JIDStatus is in group.
	 */
	public boolean isGroupPresent(String group);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
