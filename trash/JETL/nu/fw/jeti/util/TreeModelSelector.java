package nu.fw.jeti.util;

/**
 * Determines if a node in a tree shoudl be shown or not.
 * Must only look at the node when determining status. Must NOT change
 * depending on some external circumstance.
 *
 * @author Martin Forssen
 */

public interface TreeModelSelector {
    boolean isVisible(Object o);
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
