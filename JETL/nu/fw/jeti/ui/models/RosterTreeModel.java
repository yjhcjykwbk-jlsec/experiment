package nu.fw.jeti.ui.models;

import java.util.LinkedList;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nu.fw.jeti.backend.roster.JIDStatusGroup;
import nu.fw.jeti.backend.roster.JIDStatusTree;
import nu.fw.jeti.backend.roster.PrimaryJIDStatus;

import nu.fw.jeti.events.RosterListener;
import nu.fw.jeti.jabber.JIDStatus;
import nu.fw.jeti.util.I18N;

/**
 * A TreeModel implementation of the roster. If used as the
 * TreeModel of a JTree the JTree will display the roster.
 * @author E.S. de Boer
 * @version 1.0
 */

public class RosterTreeModel implements TreeModel, RosterListener 
{//use invokeandwait because removing from roster will go wrong otherwise
	private LinkedList treeModelListeners = new LinkedList();
	private JIDStatusTree tree;
	
	//------------------ events------------------\\
	public void rosterReplaced(final JIDStatusTree jidTree)
	{
		tree = jidTree;
		Runnable updateAComponent = new Runnable() {
			public void run() {
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree});
				for (int i = 0; i < treeModelListeners.size(); i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeStructureChanged(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("rosterreplaced");
	}

	public void groupAdded(final JIDStatusGroup group, final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
		        int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree},new int[] {index}, new Object[]{group});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesInserted(e);
				}
			}
		};
		try{
			if(SwingUtilities.isEventDispatchThread()) SwingUtilities.invokeLater(updateAComponent);
			else SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("group added");
	}


	public void groupDeleted(final JIDStatusGroup group,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree},new int[] {index}, new Object[]{group});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesRemoved(e);
				}
			}
		};
		try{
			if(SwingUtilities.isEventDispatchThread()) SwingUtilities.invokeLater(updateAComponent);
			else SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("group deleted");
	}
	
	//TODO synchronization?
	public void groupUpdated(final JIDStatusGroup group,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree},new int[] {index}, new Object[]{group});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesChanged(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
	}

	public void primaryAdded(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
	            int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree, jidGroup},new int[] {index}, new Object[]{primary});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesInserted(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("primary added");
	}

	public void primaryDeleted(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary,final int index)
	{
		//System.out.println("primary not deleted");
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree, jidGroup},new int[] {index}, new Object[]{primary});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesRemoved(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("primary deleted");
	}

	public void primaryUpdated(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree, jidGroup},new int[] {index}, new Object[]{primary});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesChanged(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("primary updated");
	}

	public void jidStatusAdded(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary,final JIDStatus jidStatus,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree,jidGroup, primary},new int[] {index}, new Object[]{jidStatus});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesInserted(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("js added");
	}

	public void jidStatusDeleted(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary,final JIDStatus jidStatus,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree, jidGroup ,primary},new int[] {index}, new Object[]{jidStatus});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesRemoved(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("js deleted");
	}
/*
	public void jidStatusUpdated(final JIDGroup2 jidGroup,final JIDPrimaryStatus primary,final JIDStatus2 jidStatus,final int index)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree,jidGroup, primary} ,new int[] {index}, new Object[]{jidStatus});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeNodesChanged(e);
				}
			}
		};
		SwingUtilities.invokeLater(updateAComponent);
		System.out.println("js updated");
	}
	*/
	public void jidStatussenUpdated(final JIDStatusGroup jidGroup,final PrimaryJIDStatus primary)
	{
		Runnable updateAComponent = new Runnable() {
			public void run()
			{
				int len = treeModelListeners.size();
				TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree,jidGroup, primary});
				for (int i = 0; i < len; i++) {
					((TreeModelListener)treeModelListeners.get(i)).treeStructureChanged(e);
				}
			}
		};
		try{
			SwingUtilities.invokeAndWait(updateAComponent);
		}catch (Exception e)
		{
			e.printStackTrace(); 
		}
		//System.out.println("js updated");
	}
	
	public void add()
	{//error message
		if(tree!=null && !tree.existGroup(I18N.gettext("main.error.Error")))
		{//cannont use groupAdded because that needs invokeAndWait for the roster to work
		//this doesn't work with invokeAndWait because an error fired from the eventdispatch
		//thread causes an deadlock   
			final JIDStatusGroup group = tree.getGroup(I18N.gettext("main.error.Error"));
			Runnable updateAComponent = new Runnable() {
			public void run()
				{
					int len = treeModelListeners.size();
					TreeModelEvent e = new TreeModelEvent(this,new Object[] {tree},new int[] {tree.indexOfGroup(group)}, new Object[]{group});
					for (int i = 0; i < len; i++) {
						((TreeModelListener)treeModelListeners.get(i)).treeNodesInserted(e);
					}
				}
			};
			try{
				SwingUtilities.invokeLater(updateAComponent);
			}catch (Exception e)
			{//not on error thread to prevent endless recursion 
				System.out.println(e.getMessage()); 
			}
		}
	}
	
	public void remove()
	{
		if(tree!=null && tree.existGroup(I18N.gettext("main.error.Error")))
		{
			JIDStatusGroup group = tree.getGroup(I18N.gettext("main.error.Error"));
			int index = tree.indexOfGroup(group);
			tree.removeGroup(group);
			groupDeleted(group,index);
		}
	}

	//-------------------------------Treemodel implementation---------------------------

	public Object getRoot()
	{
		return tree;
	}

	public Object getChild(Object parent, int index)
	{
		if(parent instanceof JIDStatusTree) return tree.get(index);  //root node
		else if (parent instanceof JIDStatusGroup) return ((JIDStatusGroup)parent).getPrimaryJIDStatus(index);
		else return ((PrimaryJIDStatus)parent).getJIDStatus(index);

	}

	public int getChildCount(Object parent)
	{
		if(parent instanceof JIDStatusTree) return tree.getSize();  //root node
		if(parent instanceof JIDStatusGroup) return ((JIDStatusGroup)parent).size();
		if(parent instanceof PrimaryJIDStatus) return ((PrimaryJIDStatus)parent).size();
		System.out.println("no parent");
		return 0; //hoort niet voor te komen
	}

	public boolean isLeaf(Object node)
	{
		if(node instanceof JIDStatusTree) return false;
		if(node instanceof JIDStatusGroup) return false;
		if(node instanceof PrimaryJIDStatus) return false;
		return true;
	}

	public void valueForPathChanged(TreePath path, Object newValue)
	{
		/**@todo: Implement this javax.swing.tree.TreeModel method*/
		throw new java.lang.UnsupportedOperationException("Method valueForPathChanged() not yet implemented");
	}

	public int getIndexOfChild(Object parent, Object child)
	{//kijk na
		if(parent instanceof JIDStatusTree) return ((JIDStatusTree)parent).indexOfGroup(child);
		if(parent instanceof JIDStatusGroup) return ((JIDStatusGroup)parent).indexOfPrimaryJIDStatus(child);
		else return ((PrimaryJIDStatus)parent).indexOfJIDStatus(child);
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		treeModelListeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l)
	{
		treeModelListeners.remove(l);
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
