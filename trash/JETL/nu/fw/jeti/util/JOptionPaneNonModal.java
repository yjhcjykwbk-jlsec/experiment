package nu.fw.jeti.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import nu.fw.jeti.util.Popups;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener; 


/**
 * @author student
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JOptionPaneNonModal
{

//		
//
//		/**
//		 * Brings up a dialog with the options <i>Yes</i>,
//		 * <i>No</i> and <i>Cancel</i>; with the
//		 * title, <b>Select an Option</b>.
//		 *
//		 * @param parentComponent determines the <code>Frame</code> in which the
//		 *			dialog is displayed; if <code>null</code>,
//		 *			or if the <code>parentComponent</code> has no
//		 *			<code>Frame</code>, a 
//		 *                  default <code>Frame</code> is used
//		 * @param message   the <code>Object</code> to display
//		 * @return an integer indicating the option selected by the user
//		 * @exception HeadlessException if
//		 *   <code>GraphicsEnvironment.isHeadless</code> returns
//		 *   <code>true</code>
//		 * @see java.awt.GraphicsEnvironment#isHeadless
//		 */
//		public static int showConfirmDialog(Component parentComponent,
//			Object message) throws HeadlessException {
//			return showConfirmDialog(parentComponent, message,
//									 UIManager.getString("OptionPane.titleText"),
//				JOptionPane.YES_NO_CANCEL_OPTION);
//		}
//
//		/**
//		 * Brings up a dialog where the number of choices is determined
//		 * by the <code>optionType</code> parameter.
//		 * 
//		 * @param parentComponent determines the <code>Frame</code> in which the
//		 *			dialog is displayed; if <code>null</code>,
//		 *			or if the <code>parentComponent</code> has no
//		 *			<code>Frame</code>, a 
//		 *                  default <code>Frame</code> is used
//		 * @param message   the <code>Object</code> to display
//		 * @param title     the title string for the dialog
//		 * @param optionType an int designating the options available on the dialog:
//		 *                  <code>YES_NO_OPTION</code>, or
//		 *			<code>YES_NO_CANCEL_OPTION</code>
//		 * @return an int indicating the option selected by the user
//		 * @exception HeadlessException if
//		 *   <code>GraphicsEnvironment.isHeadless</code> returns
//		 *   <code>true</code>
//		 * @see java.awt.GraphicsEnvironment#isHeadless
//		 */
//		public static int showConfirmDialog(Component parentComponent,
//			Object message, String title, int optionType)
//			throws HeadlessException {
//			return showConfirmDialog(parentComponent, message, title, optionType,
//			JOptionPane.QUESTION_MESSAGE);
//		}
//
//		/**
//		 * Brings up a dialog where the number of choices is determined
//		 * by the <code>optionType</code> parameter, where the
//		 * <code>messageType</code>
//		 * parameter determines the icon to display.
//		 * The <code>messageType</code> parameter is primarily used to supply
//		 * a default icon from the Look and Feel.
//		 *
//		 * @param parentComponent determines the <code>Frame</code> in
//		 *			which the dialog is displayed; if <code>null</code>,
//		 *			or if the <code>parentComponent</code> has no
//		 *			<code>Frame</code>, a 
//		 *                  default <code>Frame</code> is used.
//		 * @param message   the <code>Object</code> to display
//		 * @param title     the title string for the dialog
//		 * @param optionType an integer designating the options available
//		 *			on the dialog: <code>YES_NO_OPTION</code>,
//		 *			or <code>YES_NO_CANCEL_OPTION</code>
//		 * @param messageType an integer designating the kind of message this is; 
//		 *                  primarily used to determine the icon from the pluggable
//		 *                  Look and Feel: <code>ERROR_MESSAGE</code>,
//		 *			<code>INFORMATION_MESSAGE</code>, 
//		 *                  <code>WARNING_MESSAGE</code>,
//		 *                  <code>QUESTION_MESSAGE</code>,
//		 *			or <code>PLAIN_MESSAGE</code>
//		 * @return an integer indicating the option selected by the user
//		 * @exception HeadlessException if
//		 *   <code>GraphicsEnvironment.isHeadless</code> returns
//		 *   <code>true</code>
//		 * @see java.awt.GraphicsEnvironment#isHeadless
//		 */
//		public static int showConfirmDialog(Component parentComponent,
//			Object message, String title, int optionType, int messageType)
//			throws HeadlessException {
//			return showConfirmDialog(parentComponent, message, title, optionType,
//									messageType, null);
//		}
//
//		/**
//		 * Brings up a dialog with a specified icon, where the number of 
//		 * choices is determined by the <code>optionType</code> parameter.
//		 * The <code>messageType</code> parameter is primarily used to supply
//		 * a default icon from the look and feel.
//		 *
//		 * @param parentComponent determines the <code>Frame</code> in which the
//		 *			dialog is displayed; if <code>null</code>,
//		 *			or if the <code>parentComponent</code> has no
//		 *			<code>Frame</code>, a 
//		 *			default <code>Frame</code> is used
//		 * @param message   the Object to display
//		 * @param title     the title string for the dialog
//		 * @param optionType an int designating the options available on the dialog:
//		 *                  <code>YES_NO_OPTION</code>,
//		 *			or <code>YES_NO_CANCEL_OPTION</code>
//		 * @param messageType an int designating the kind of message this is, 
//		 *                  primarily used to determine the icon from the pluggable
//		 *                  Look and Feel: <code>ERROR_MESSAGE</code>,
//		 *			<code>INFORMATION_MESSAGE</code>, 
//		 *                  <code>WARNING_MESSAGE</code>,
//		 *                  <code>QUESTION_MESSAGE</code>,
//		 *			or <code>PLAIN_MESSAGE</code>
//		 * @param icon      the icon to display in the dialog
//		 * @return an int indicating the option selected by the user
//		 * @exception HeadlessException if
//		 *   <code>GraphicsEnvironment.isHeadless</code> returns
//		 *   <code>true</code>
//		 * @see java.awt.GraphicsEnvironment#isHeadless
//		 */
//		public static int showConfirmDialog(Component parentComponent,
//			Object message, String title, int optionType,
//			int messageType, Icon icon) throws HeadlessException {
//			return showOptionDialog(parentComponent, message, title, optionType,
//									messageType, icon, null, null);
//		}




	public static void showOptionDialog(Component parentComponent,
			Object message, String title, int optionType, int messageType,
			Icon icon,final Object[] options, Object initialValue,final Popups.OptionChoosed   choosed)
			throws HeadlessException 
	{
		final JOptionPane   pane = new JOptionPane(message, messageType,
													   optionType, icon,
													   options, initialValue);

		pane.setInitialValue(initialValue);
		//	pane.setComponentOrientation(((parentComponent == null) ?
		//	getRootFrame() : parentComponent).getComponentOrientation());

		int style = styleFromMessageType(messageType);
		//JDialog dialog = pane.createDialog(parentComponent, title, style);


		final JDialog dialog;

		//Window window = JOptionPane.getWindowForComponent(parentComponent);
		if (parentComponent instanceof Dialog) {
			dialog = new JDialog((Dialog)parentComponent, title, true);
		}
		else if (parentComponent instanceof Frame) {
			dialog = new JDialog((Frame)parentComponent , title, false);	
		} else {
			dialog = new JDialog(new JFrame() , title, false);
		}
		Container             contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(pane, BorderLayout.CENTER);
		dialog.setResizable(false);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = 
			UIManager.getLookAndFeel().getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				dialog.setUndecorated(true);
				pane.getRootPane().setWindowDecorationStyle(style);
			}
		}
		dialog.pack();
		dialog.setLocationRelativeTo(parentComponent);
		dialog.addWindowListener(new WindowAdapter() {
			private boolean gotFocus = false;
			public void windowClosing(WindowEvent we) {
				pane.setValue(null);
				returnOption(pane.getValue(),options,choosed);
			}
			public void windowGainedFocus(WindowEvent we) {
				// Once window gets focus, set initial focus
				if (!gotFocus) {
					pane.selectInitialValue();
					gotFocus = true;
				}
			}
		});
		dialog.addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
				// reset value to ensure closing works properly
					pane.setValue(JOptionPane.UNINITIALIZED_VALUE);
				}
		});
		pane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// Let the defaultCloseOperation handle the closing
				// if the user closed the window without selecting a button
				// (newValue = null in that case).  Otherwise, close the dialog.
				if(dialog.isVisible() && event.getSource() == pane &&
				   (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) &&
					event.getNewValue() != null &&
			event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
					 
					//System.out.println(event.getNewValue()    );
					returnOption(pane.getValue(),options,choosed);
					dialog.dispose();	  
				}
								
				
			}
		});
		pane.selectInitialValue();
		dialog.show();
	}
		
	private static void returnOption(Object selectedValue, Object[] options,Popups.OptionChoosed choosed)
	{
		//System.out.println(selectedValue );
		if(choosed == null) return;//messagedialog does not use choosed
		if(selectedValue == null)	choosed.optionChoosed(JOptionPane.CLOSED_OPTION);
		else if(options == null) {
			if(selectedValue instanceof Integer)
			choosed.optionChoosed(((Integer)selectedValue).intValue());
			else choosed.optionChoosed(JOptionPane.CLOSED_OPTION);
		}
		else
		{
			for(int counter = 0, maxCounter = options.length;counter < maxCounter; counter++) 
			{
				if(options[counter].equals(selectedValue))
				{
					choosed.optionChoosed(counter);
					return;
				}
			}
			choosed.optionChoosed(JOptionPane.CLOSED_OPTION);
		}
	}

	private static int styleFromMessageType(int messageType) {
		   switch (messageType) {
		   case JOptionPane.ERROR_MESSAGE:
			   return JRootPane.ERROR_DIALOG;
		   case JOptionPane.QUESTION_MESSAGE:
			   return JRootPane.QUESTION_DIALOG;
		   case JOptionPane.WARNING_MESSAGE:
			   return JRootPane.WARNING_DIALOG;
		   case JOptionPane.INFORMATION_MESSAGE:
			   return JRootPane.INFORMATION_DIALOG;
		   case JOptionPane.PLAIN_MESSAGE:
		   default:
			   return JRootPane.PLAIN_DIALOG;
		   }
	   }
}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
