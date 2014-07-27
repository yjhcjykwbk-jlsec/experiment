package nu.fw.jeti.events;

import nu.fw.jeti.backend.roster.*;
import nu.fw.jeti.jabber.*;
import nu.fw.jeti.ui.models.RosterTreeModel;

/**
 * The listener interface for receiving roster events.
 * This listener is used to inform the frontside roster of changes in the backside
 * see {@link RosterTreeModel} for a TreeModel implementation.
 * @author E.S. de Boer
 * @version 1.0
 */

public interface RosterListener extends JETIListener
{
	/**
	 * Called on a complete roster refresh.
	 * @param tree Tree containing the new roster.
	 */
	public void rosterReplaced(JIDStatusTree tree);

	/**
	 * Called when a group has been added to the roster.
	 * @param group The new group.
	 * @param index the index of the new group in the tree.
	 */
	void groupAdded(JIDStatusGroup group,int index);

	/**
	 * Called when a group has been deleted.
	 * @param group The deleted group.
	 * @param index The index of the group in the tree before deletion.
	 */
	void groupDeleted(JIDStatusGroup group,int index);
	
	
	/**
	 * Called when a group has changed textual info.
	 * @param group The deleted group.
	 * @param index The index of the group in the tree.
	 */
	public void groupUpdated(final JIDStatusGroup group,int index);
	

	/**
	 * Called when a primaryJidStatus has been added
	 * @param jidGroup The group where the PrimaryJIDStatus is in.
	 * @param primary The added primaryJIDStatus
	 * @param index The index of the primaryJIDStatus in the group.
	 */
	void primaryAdded(JIDStatusGroup jidGroup,PrimaryJIDStatus primary,int index);

	/**
	 * Called when a primaryJidStatus has been deleted.
	 * @param jidGroup The group the primaryJIDStatus was in.
	 * @param primary The deleted primaryJIDStatus.
	 * @param index The index of the primaryJIDStaus in the group before deletion.
	 */
	void primaryDeleted(JIDStatusGroup jidGroup,PrimaryJIDStatus primary,int index);

	/**
	 * Called when the primaryJidstatus has been changed
	 * @param jidGroup The group the primaryJIDStatus is in.
	 * @param primary The changed primaryJIDStatus
	 * @param index The index of the primaryJIDStatus in the group
	 */
	//weg? only server uses this
	void primaryUpdated(JIDStatusGroup jidGroup,PrimaryJIDStatus primary,int index);

	/**
	 * Called when a JIDStatus has been added.
	 * @param jidGroup The group where the primaryJIDStatus, where this jidstatus has been added to, is in.
	 * @param primary The primaryJIDStatus where this JIDStatus has been added to.
	 * @param jidStatus The new JIDStatus
	 * @param index The index of the new JIDStatus in the PrimaryJIDStatus.
	 */
	void jidStatusAdded(JIDStatusGroup jidGroup,PrimaryJIDStatus primary,JIDStatus jidStatus,int index);

	/**
	 * Called when a JIDStatus has been deleted.
	 * @param jidGroup The group where the primaryJIDStatus, where this jidstatus has been deleted from, is in.
	 * @param primary The primaryJIDStatus where this JIDStatus has been deleted from.
	 * @param jidStatus The deleted JIDStatus
	 * @param index The index of jidStatus in the PrimaryJIDStatus before deletion.
	 */
	void jidStatusDeleted(JIDStatusGroup jidGroup,PrimaryJIDStatus primary,JIDStatus jidStatus,int index);

	//void jidStatusUpdated(JIDGroup2 jidGroup,JIDPrimaryStatus primary,JIDStatus2 jidStatus,int index);

	/**
	 * Called when the JIDStatussen of a primaryJIDStatus are updated.
	 * @param jidGroup The group where primary is in.
	 * @param primary The primaryJIDStatus that has been updated.
	 */
	void jidStatussenUpdated(JIDStatusGroup jidGroup,PrimaryJIDStatus primary);

}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
