package nu.fw.jeti.util;

import java.awt.Container;
import java.awt.HeadlessException;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * <p>Title: im</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author E.S. de Boer
 * @version 1.0
 */

public class Popups
{
	static private Container main;

    public Popups(Container window)
    {
		main = window;
	}

	public static void errorPopup(String error,String title)
	{
		popup(error,title,JOptionPane.ERROR_MESSAGE);
		//JOptionPane.showMessageDialog(main,error,title,JOptionPane.ERROR_MESSAGE);
	}

	public static void criticalErrorPopup(String error,String title)
	{
		//popup(error,title,javax.swing.JOptionPane.ERROR_MESSAGE);
		JOptionPane.showMessageDialog(main,error,title,JOptionPane.ERROR_MESSAGE);
	}

	public static void messagePopup(String message,String title)
	{
		popup(message,title,JOptionPane.INFORMATION_MESSAGE);
		//JOptionPane.showMessageDialog(main,error,title,JOptionPane.ERROR_MESSAGE);
	}

	public static void popup(String text,String title,int message)
	{
		if(main!=null)main.show();// toFront();
		showMessageDialog(text,title,message);
	}
	
	public static void showOptionDialog(Object message, String title, int optionType, int messageType,
				Icon icon,final Object[] options, Object initialValue,OptionChoosed choosed)
	{
		if(main!=null)main.show();
		JOptionPaneNonModal.showOptionDialog( main,message,title,optionType,messageType,icon,options,initialValue ,choosed); 		
	}
				

	/*
	static class TempThread extends Thread
	{
		private String title;
		private String text;
		private int message;

		public TempThread(String text, String title,int message)
		{
		    this.text =text;
			this.title =title;
			this.message =message;
		}

		public void run()
		{
			popup(text,title,message);
		}
	}
	*/
	
	/**
	 * Brings up a dialog that displays a message using a default
	 * icon determined by the <code>messageType</code> parameter.
	 *
	 * @param parentComponent determines the <code>Frame</code>
	 *		in which the dialog is displayed; if <code>null</code>,
	 *		or if the <code>parentComponent</code> has no
	 *		<code>Frame</code>, a default <code>Frame</code> is used
	 * @param message   the <code>Object</code> to display
	 * @param title     the title string for the dialog
	 * @param messageType the type of message to be displayed:
	 *                  <code>ERROR_MESSAGE</code>,
	 *			<code>INFORMATION_MESSAGE</code>,
	 *			<code>WARNING_MESSAGE</code>,
	 *                  <code>QUESTION_MESSAGE</code>,
	 *			or <code>PLAIN_MESSAGE</code>
	 * @exception HeadlessException if
	 *   <code>GraphicsEnvironment.isHeadless</code> returns
	 *   <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static void showMessageDialog(Object message, String title, int messageType)
	{
		showMessageDialog(message, title, messageType, null);
	}

	/**
	 * Brings up a dialog displaying a message, specifying all parameters.
	 *
	 * @param parentComponent determines the <code>Frame</code> in which the
	 *			dialog is displayed; if <code>null</code>,
	 *			or if the <code>parentComponent</code> has no
	 *			<code>Frame</code>, a 
	 *                  default <code>Frame</code> is used
	 * @param message   the <code>Object</code> to display
	 * @param title     the title string for the dialog
	 * @param messageType the type of message to be displayed:
	 *                  <code>ERROR_MESSAGE</code>,
	 *			<code>INFORMATION_MESSAGE</code>,
	 *			<code>WARNING_MESSAGE</code>,
	 *                  <code>QUESTION_MESSAGE</code>,
	 *			or <code>PLAIN_MESSAGE</code>
	 * @param icon      an icon to display in the dialog that helps the user
	 *                  identify the kind of message that is being displayed
	 * @exception HeadlessException if
	 *   <code>GraphicsEnvironment.isHeadless</code> returns
	 *   <code>true</code>
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public static void showMessageDialog(	Object message, String title, int messageType, Icon icon)
	{
		showOptionDialog(message, title, JOptionPane.DEFAULT_OPTION, 
						 messageType, icon, null, null,null);
	}
	
	public interface OptionChoosed
	{
		void optionChoosed(int option);
	}

}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
