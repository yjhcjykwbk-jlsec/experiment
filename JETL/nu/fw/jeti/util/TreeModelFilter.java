package nu.fw.jeti.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

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

public class TreeModelFilter implements TreeModel, TreeModelListener {
    private TreeModelSelector selector;
    private TreeModel model;
    private LinkedList listeners = new LinkedList();
    private Hashtable nodes = new Hashtable(30);

    public TreeModelFilter(TreeModel model, TreeModelSelector selector) {
        this.model = model;
        this.selector = selector;

        model.addTreeModelListener(this);
    }

    /*
     * TreeModel interface
     */
    public Object getRoot() {
        return model.getRoot();
    }

    public Object getChild(Object parent, int index) {
        int max = model.getChildCount(parent);
        for (int i=0, v=0; i<max; i++) {
            Object child = model.getChild(parent, i);
            boolean isVisible = selector.isVisible(child);
            nodes.put(child, new Boolean(isVisible));
            if (isVisible && index == v++) {
                return child;
            }
        }
        return null;
    }

    public int getChildCount(Object parent) {
        int max = model.getChildCount(parent);
        int count = 0;

        for (int i=0; i<max; i++) {
            Object child = model.getChild(parent, i);
            boolean isVisible = selector.isVisible(child);
            nodes.put(child, new Boolean(isVisible));
            if (isVisible) {
                count++;
            }
        }
        return count;
    }

    public boolean isLeaf(Object node) {
        return model.isLeaf(node);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO
    }

    public int getIndexOfChild(Object parent, Object child) {
        int max = model.getChildCount(parent);
        for (int i=0, v=0; i<max; i++) {
            Object mChild = model.getChild(parent, i);
            if (mChild.equals(child)) {
                return v;
            }
            if (selector.isVisible(child)) {
                v++;
            }
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    /*
     * TreeModelListener interface
     */
    public void treeNodesChanged(TreeModelEvent e) {
        int indices[] = e.getChildIndices();
        Object children[] = e.getChildren();

        if (indices == null || indices.length == 0) {
            TreeModelEvent e2 = new TreeModelEvent(this, e.getTreePath());
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesChanged(e2);
            }
        } else {
            for (int i=0, v=0; i < indices.length; i++) {
                nodeChanged(e.getTreePath(), indices[i], children[i]);
            }
        }
    }

    public void treeNodesInserted(TreeModelEvent e) {
        ArrayList newIndices = new ArrayList();
        ArrayList newChildren = new ArrayList();
        Object parent = e.getTreePath().getLastPathComponent();
        int indices[] = e.getChildIndices();
        Object children[] = e.getChildren();
        
        for (int i=0, v=0; i < indices.length; i++) {
            boolean vis = selector.isVisible(children[i]);
            nodes.put(children[i], new Boolean(vis));
            if (vis) {
                newIndices.add(new Integer(upperIndex(parent, indices[i])));
                newChildren.add(children[i]);
            }
        }
        if (newIndices.size() > 0) {
            TreeModelEvent e2 = createEvent(e.getTreePath(), newIndices,
                                            newChildren);
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesInserted(e2);
            }
        }
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        ArrayList newIndices = new ArrayList();
        ArrayList newChildren = new ArrayList();
        Object parent = e.getTreePath().getLastPathComponent();
        int indices[] = e.getChildIndices();
        Object children[] = e.getChildren();
        
        for (int i=0, v=0; i < indices.length; i++) {
            Boolean visible = (Boolean)nodes.get(children[i]);
            if (visible != null && visible.booleanValue()) {
                newIndices.add(new Integer(upperIndex(parent, indices[i])));
                newChildren.add(children[i]);
            }
            nodes.remove(children[i]);
        }

        if (newIndices.size() > 0) {
            TreeModelEvent e2 = createEvent(e.getTreePath(), newIndices,
                                            newChildren);
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesRemoved(e2);
            }
        }
    }

    public void treeStructureChanged(TreeModelEvent e) {
        TreePath path = e.getTreePath();
        Object node = path.getLastPathComponent();
        nodes.put(node, new Boolean(selector.isVisible(node)));
        if (path.getPathCount() > 1) {
            path = path.getParentPath();
        }
        TreeModelEvent e2 = new TreeModelEvent(this, path);
        for (int i = 0; i < listeners.size(); i++) {
            ((TreeModelListener)listeners.get(i)).treeStructureChanged(e2);
        }
    }

    private TreeModelEvent createEvent(TreePath path,
                                       ArrayList indices,
                                       ArrayList children) {
        int intIndices[] = new int[indices.size()];
        for (int i=0; i<indices.size(); i++) {
            intIndices[i] = ((Integer)indices.get(i)).intValue();
        }
        return new TreeModelEvent(
            this, path, intIndices,
            children.toArray(new Object[children.size()]));
    }

    private int upperIndex(Object parent, int index) {
        int vIndex = 0;
        for (int i=0; i < index; i++) {
            if (selector.isVisible(model.getChild(parent, i))) {
                vIndex++;
            }
        }
        return vIndex;
    }

    private void nodeChanged(TreePath path, int index, Object node) {
        Object parent = path.getLastPathComponent();

        boolean newVis = selector.isVisible(node);
        Boolean oldVis = (Boolean)nodes.get(node);
        if (newVis && (oldVis == null || !oldVis.booleanValue())) {
            int indices[] = {upperIndex(parent, index)};
            Object children[] = {node};
            TreeModelEvent e = new TreeModelEvent(this, path,indices,children);
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesInserted(e);
            }
        } else if (!newVis && oldVis != null && oldVis.booleanValue()) {
            int indices[] = {upperIndex(parent, index)};
            Object children[] = {node};
            TreeModelEvent e = new TreeModelEvent(this, path,indices,children);
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesRemoved(e);
            }
        } else if (newVis && oldVis != null && oldVis.booleanValue()) {
            int vIndex = upperIndex(path.getLastPathComponent(), index);
            TreeModelEvent e = new TreeModelEvent(this, path,
                                                  new int[] {vIndex},
                                                  new Object[] {node});
            for (int i = 0; i < listeners.size(); i++) {
                ((TreeModelListener)listeners.get(i)).treeNodesChanged(e);
            }
        }
        nodes.put(node, new Boolean(newVis));
    }
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
