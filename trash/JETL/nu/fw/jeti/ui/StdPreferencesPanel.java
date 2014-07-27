package nu.fw.jeti.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Locale;

import javax.swing.*;

import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.ui.models.LocaleModel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Preferences;
import nu.fw.jeti.util.SpringUtilities;
import nu.fw.jeti.util.SwingWorker;

/**
 * @author E.S. de Boer
 *
 * 
 */
public class StdPreferencesPanel extends PreferencesPanel
{
	private Backend backend;
	private JCheckBox chkEnter;
	public static JCheckBox chkBeep;
	private JCheckBox chkNick;
	private JCheckBox chkBMW;
	private JCheckBox chkTaskbar;
	private JCheckBox chkTitle;
	private JCheckBox chkOFFline;
	private JCheckBox chkJetiPos;
	private JCheckBox chkShowTimestamp;
	private JComboBox cmbLanguage;
	private JComboBox cmbCountry;
	private boolean oldOFFline;

	public StdPreferencesPanel(Backend backend)
	{
		this.backend = backend;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(3, 5, 0, 3);

		chkEnter = new JCheckBox(I18N.gettext("main.options.standard.Enter_key_sends_messages"));
		chkEnter.setToolTipText(I18N.gettext("main.options.standard.tooltip.Send_message_with_enter_key_instead_of_ctrl_or_shift_and_enter"));
		chkEnter.setSelected(Preferences.getBoolean("jeti","enterSends",true));
        add(chkEnter, c);

		chkShowTimestamp = new JCheckBox(I18N.gettext("main.options.standard.Show_timestamps"));
		chkShowTimestamp.setToolTipText(I18N.gettext("main.options.standard.tooltip.Show_timestamps_on_all_messages"));
		chkShowTimestamp.setSelected(Preferences.getBoolean("jeti","showTimestamp",true));
        add(chkShowTimestamp, c);

		chkBeep = new JCheckBox(I18N.gettext("main.options.standard.Beep_on_new_message"));
        chkBeep.setToolTipText(I18N.gettext("main.options.standard.tooltip.Beep_on_new_message_arrival")); 
		chkBeep.setSelected(Preferences.getBoolean("jeti","beep",true));
        add(chkBeep, c);
		
		chkNick = new JCheckBox(I18N.gettext("main.options.standard.Show_remote_msn_nicknames"));
		chkNick.setToolTipText(I18N.gettext("main.options.standard.tooltip.Show_remote_nicknames_instead_of_the_local_ones")); 
		chkNick.setSelected(Preferences.getBoolean("jeti","showRealNick", false));
        add(chkNick, c);
				
		chkOFFline = new JCheckBox(I18N.gettext("main.options.standard.Show_Offline_contacts"));
		chkOFFline.setToolTipText(I18N.gettext("main.options.standard.tooltip.Show_Offline_contacts")); 
		oldOFFline = Preferences.getBoolean("jeti","showoffline",true);
		chkOFFline.setSelected(oldOFFline);
        add(chkOFFline, c);

		chkTaskbar = new JCheckBox(I18N.gettext("main.options.standard.Don't_show_the_mainscreen_in_the_taskbar"));
		chkTaskbar.setToolTipText(I18N.gettext("main.options.standard.tooltip.Restart_required")); 
		chkTaskbar.setSelected(Preferences.getBoolean("jeti","showNotInTaskbar",false));
        add(chkTaskbar, c);
		
		chkBMW = new JCheckBox(I18N.gettext("main.options.standard.White_background_main_window"));
		chkBMW.setToolTipText(I18N.gettext("main.options.standard.tooltip.Makes_the_main_window_white_(restart_required)")); 
		chkBMW.setSelected(Preferences.getBoolean("jeti","bmw",true));
        add(chkBMW, c);
		
		chkTitle = new JCheckBox(I18N.gettext("main.options.standard.Java_window_decorations"));
		chkTitle.setToolTipText(I18N.gettext("main.options.standard.tooltip.Set_java_look_and_feel_decorations_for_windows_(restart_required)"));
		chkTitle.setSelected(Preferences.getBoolean("jeti","javadecorations",false));
        add(chkTitle, c);

        chkJetiPos = new JCheckBox(I18N.gettext("main.options.standard.Jeti_as_Menu_instead_of_button"));
		chkJetiPos.setToolTipText(I18N.gettext("main.options.standard.tooltip.Change_the_Jeti_button_to_a_menu_(restart_required)"));
		chkJetiPos.setSelected(Preferences.getBoolean("jeti","menutop",false));
        add(chkJetiPos, c);
        
        GridBagConstraints cl = new GridBagConstraints();
        cl.gridwidth = 1;
        cl.anchor = GridBagConstraints.LINE_START;
        cl.insets = new Insets(3, 5, 0, 3);

        add(new JLabel(I18N.gettext("main.options.standard.Language")), cl);
		cmbLanguage = new JComboBox(new Object[]{I18N.gettext("main.options.standard.Loading_Languages,_please_wait")});
        cmbLanguage.setPreferredSize(new Dimension(200, 21));
        add(cmbLanguage, c);

        add(new JLabel(I18N.gettext("main.options.standard.Country")), cl);
		cmbCountry = new JComboBox();
        cmbCountry.setPreferredSize(new Dimension(200, 21));
        add(cmbCountry, c);

        c.gridwidth = 3;
        c.weighty = 1.0;
        c.weightx = 1.0;
        add(Box.createVerticalGlue(), c);

	    SwingWorker worker = new SwingWorker() 
		{
	    	I18N i18n;

	    	public Object construct() 
	    	{
	        	i18n = new I18N();
	            return null;
	        }

	        //Runs on the event-dispatching thread.
	        public void finished() 
	        {
	        	cmbLanguage.setModel(new DefaultComboBoxModel(i18n.getLanguages()));
	        	LocaleModel lm = new LocaleModel(i18n);
	        	cmbLanguage.addActionListener(lm);
	        	String languageCode = Preferences.getString("jeti","language",getDefaultLocale().getLanguage());
	        	ComboBoxModel model = cmbLanguage.getModel();
	    		boolean selected=false;
	        	for(int i=0;i<model.getSize();i++)
	    		{
	    			if(((I18N.Language)model.getElementAt(i)).getLanguageCode().equals(languageCode))
	    			{
	    				cmbLanguage.setSelectedIndex(i);
	    				selected=true;
	    				break;
	    			}
	    		}
	    		if(!selected)
	    		{//language not found so set to default language
	    			languageCode = Locale.getDefault().getLanguage();
	    			for(int i=0;i<model.getSize();i++)
		    		{
		    			if(((I18N.Language)model.getElementAt(i)).getLanguageCode().equals(languageCode))
		    			{
		    				cmbLanguage.setSelectedIndex(i);
		    				break;
		    			}
		    		}
	    		}
	        	String countryCode = Preferences.getString("jeti","country",getDefaultLocale().getCountry());
	    		cmbCountry.setModel(lm);
	    		model = cmbCountry.getModel();
	    		selected=false;
	    		for(int i=0;i<model.getSize();i++)
	    		{
	    			if(((I18N.Country)model.getElementAt(i)).getCountryCode().equals(countryCode))
	    			{
	    				cmbCountry.setSelectedIndex(i);
	    				selected=true;
	    				break;
	    			}
	    		}
	    		if(!selected)
	    		{//country not found so set to other
	    			countryCode = "";
	    			for(int i=0;i<model.getSize();i++)
		    		{
		    			if(((I18N.Country)model.getElementAt(i)).getCountryCode().equals(countryCode))
		    			{
		    				cmbCountry.setSelectedIndex(i);
		    				break;
		    			}
		    		}
	    		}
	        }
	    };
	    worker.start(); 
	}

	public void savePreferences()
	{
		Preferences.putBoolean("jeti","enterSends", chkEnter.isSelected());
		Preferences.putBoolean("jeti","showTimestamp", chkShowTimestamp.isSelected());
		Preferences.putBoolean("jeti","beep", chkBeep.isSelected());
		Preferences.putBoolean("jeti","showRealNick", chkNick.isSelected());
		Preferences.putBoolean("jeti","bmw",chkBMW.isSelected());
		Preferences.putBoolean("jeti","showNotInTaskbar",chkTaskbar.isSelected());
		Preferences.putBoolean("jeti","javadecorations",chkTitle.isSelected());
		Preferences.putBoolean("jeti","menutop",chkJetiPos.isSelected());
		if (oldOFFline != chkOFFline.isSelected())
		{//only update when there is a change
			Preferences.putBoolean("jeti","showoffline", chkOFFline.isSelected());
			oldOFFline = chkOFFline.isSelected();
			backend.getMain().changeOFFlinePanel(oldOFFline);
		}
		if(cmbCountry.getSelectedIndex()!=-1) Preferences.putString("jeti", "country",((I18N.Country) cmbCountry.getSelectedItem()).getCountryCode());
		if(cmbLanguage.getSelectedIndex()!=-1
                    && cmbLanguage.getSelectedItem() instanceof I18N.Language)
		{
			String languageCode = ((I18N.Language)cmbLanguage.getSelectedItem()).getLanguageCode();
			if(!Preferences.getString("jeti","language",getDefaultLocale().getLanguage()).equals(languageCode))
			{//only update when there is a change
				Preferences.putString("jeti", "language",languageCode);
				I18N.init();
				backend.getMain().translate();
			}
		}
	}
}

/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
