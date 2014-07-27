/*
 * Created on Jan 6, 2004
 */
package nu.fw.jeti.plugins.windowsutils;


import java.awt.Canvas;
import java.awt.Component;

public class WindowUtil extends Canvas{
	static { System.loadLibrary("WindowUtil"); }
	public native void flash(Component c, boolean flash);
	public native void windowAlwaysOnTop(Component c, boolean flag);
	public native void setWindowAlpha(Component c, int alpha);
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
