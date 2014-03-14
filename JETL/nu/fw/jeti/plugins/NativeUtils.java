/*
 * Created on Feb 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package nu.fw.jeti.plugins;

import java.awt.Component;

public interface NativeUtils
{
	public boolean supportsAlpha();
		
	public void flash(Component c, boolean flash);
	public void windowAlwaysOnTop(Component c, boolean flag);
	public void setWindowAlpha(Component c, int alpha);
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
