package nu.fw.jeti.plugins;

import javax.swing.JPanel;

/**
 * <p>Title: J²M</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public abstract class PreferencesPanel extends JPanel
{

	public abstract void savePreferences();

    public boolean inhibited() {
        return false;
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
