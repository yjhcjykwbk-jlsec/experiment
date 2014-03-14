/*
 * Created on Sep 10, 2003
 *  
 */
package nu.fw.jeti.plugins.idle;

/**
 * @author esdeboer
 */

public class IdleTimer {

	static {
	  System.loadLibrary("Idle");
	}

  public static native long IdleGetLastInputTime();

  public static native boolean IdleInit();
  
  public static native void IdleTerm();

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
