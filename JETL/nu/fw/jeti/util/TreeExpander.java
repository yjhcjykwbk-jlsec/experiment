package nu.fw.jeti.util;

import java.util.Hashtable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Used to filter some nodes from a tree. That is nodes which the
 * selector decides not to show does not seem to exist in the tree.
 * The selector is assumed to not change behavior over time.
 *
 * @author Martin Forssen
 */

public class TreeExpander implements TreeExpansionListener, TreeModelListener {
    private JTree tree;
    private Hashtable known = new Hashtable();

    public TreeExpander(JTree tree, TreeModel model) {
        this.tree = tree;

        tree.addTreeExpansionListener(this);
        model.addTreeModelListener(this);
    }

    public void expand(TreePath path) {
        //System.out.println("Expand: " + path + " " + tree.isCollapsed(path) + " " + tree.isExpanded(path));
        known.put(path, new Boolean(true));
        tree.expandPath(path);
    }

    /*
     * TreeModelListener interface
     */
    public void treeNodesChanged(TreeModelEvent e) {
        //System.out.println("changed: " + e);
    }

    public void treeNodesInserted(TreeModelEvent e) {
        //System.out.println("inserted: " + e);
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        //System.out.println("removed: " + e);
    }

    public void treeStructureChanged(TreeModelEvent e) {
        //System.out.println("structChanged: " + e.getTreePath());
        Boolean b = (Boolean)known.get(e.getTreePath());
        if (b != null && b.booleanValue()) {
            // System.out.println("do expand");
            //if (!tree.isExpanded(e.getTreePath())) {
                tree.expandPath(e.getTreePath());
                //}
                //} else {
                //System.out.println("Do nothing: " + b);
        }
    }

    /*
     * TreeExpansionListener
     */
    public void treeExpanded(TreeExpansionEvent event) {
        //System.out.println("expanded: " + event.getPath());
        Boolean b = (Boolean)known.get(event.getPath());
        if (b == null || !b.booleanValue()) {
            known.put(event.getPath(), new Boolean(true));
        }
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        //System.out.println("collapsed: " + event.getPath());
        Boolean b = (Boolean)known.get(event.getPath());
        if (b != null || b.booleanValue()) {
            known.put(event.getPath(), new Boolean(false));
        }
    }
}
