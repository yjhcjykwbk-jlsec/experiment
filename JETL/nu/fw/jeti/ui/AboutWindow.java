package nu.fw.jeti.ui;


import java.awt.SystemColor;
import java.awt.event.ActionEvent;

import javax.swing.*;

import nu.fw.jeti.plugins.PluginsInfo;
import nu.fw.jeti.util.I18N;

/*2001
 * @author E.S. de Boer
 * @version 1.0
 */

public class AboutWindow extends JFrame
{   
    public AboutWindow()
    {
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void jbInit() throws Exception
    {
    	setIconImage(nu.fw.jeti.images.StatusIcons.getImageIcon("jeti").getImage());
		setTitle(I18N.gettext("main.about.About JETI"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		JLabel label = new JLabel("JETI");
        label.setFont(new java.awt.Font("Serif", 1, 40));
        label.setAlignmentX((float) 0.5);
		getContentPane().add(label);
        label = new JLabel(I18N.gettext("main.about.Version") + " " + nu.fw.jeti.backend.Start.VERSION);
        label.setAlignmentX((float) 0.5);
        getContentPane().add(label);
        
        
		label = new JLabel("(c) 2001-2005");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(" ");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(I18N.gettext("main.about.Designed_by")+ " E.S. de Boer (eric@jeti.tk)");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(I18N.gettext("main.about.Contributions_by") + " M. Forssen, R. González Glz.");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(" ");
		label.setAlignmentX(0.5f);
		label = new JLabel(I18N.gettext("main.about.Translated_by_(PUT_YOUR_NAME_HERE)"));
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(" ");
		label.setAlignmentX(0.5f);
		label = new JLabel(I18N.gettext("main.about.OS/2_version_by") + " V. Ehlert");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(" ");
		label.setAlignmentX(0.5f);
		label = new JLabel(I18N.gettext("main.about.Tested_by") + " T. J. Verweij");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(" ");
		label.setAlignmentX(0.5f);
		getContentPane().add(label);
		label = new JLabel(I18N.gettext("main.about.About_the_Plugins"));
		label.setAlignmentX(1.0f);
		getContentPane().add(label);
		
		
		JTextPane textPane = new JTextPane(); 
		textPane.setBackground(SystemColor.control);
		textPane.setEnabled(false);
		textPane.setFont(new java.awt.Font("Dialog", 1, 12));
		//jTextPane1.setForeground(UIManager.getColor("button.background"));
		textPane.setOpaque(false);
		textPane.setEditable(false);
		textPane.setText(PluginsInfo.getAbout());
		textPane.setCaretPosition(0); 
		JScrollPane scrollPane = new JScrollPane(textPane); 
		scrollPane.setBorder(null);
		//jScrollPane1.getViewport().add(textPane);
        getContentPane().add(scrollPane);
		
		JButton button = new JButton();
        button.setAlignmentX((float) 0.5);
        I18N.setTextAndMnemonic("Close",button);
        button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButton1_actionPerformed(e);
            }
        });
		getRootPane().setDefaultButton(button);
		getContentPane().add(button);
		setSize(300,400);
		setLocationRelativeTo(null);
    }

    void jButton1_actionPerformed(ActionEvent e)
    {
		this.dispose();
    }
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
