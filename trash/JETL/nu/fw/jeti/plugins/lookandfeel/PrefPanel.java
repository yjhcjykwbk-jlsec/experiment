package nu.fw.jeti.plugins.lookandfeel;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.URLClassloader;
import nu.fw.jeti.jabber.Backend;
import nu.fw.jeti.plugins.PreferencesPanel;
import nu.fw.jeti.util.I18N;
import nu.fw.jeti.util.Popups;
import nu.fw.jeti.util.Preferences;

/**
 * @author E.S. de Boer
 * @version 1.0
 */

public class PrefPanel extends PreferencesPanel
{
	private BorderLayout borderLayout1 = new BorderLayout();
	//private JScrollPane jScrollPane1 = new JScrollPane();
	private JButton jButton1 = new JButton();
	private JPanel pnlLookAndFeels;
	private Map lookAndFeels;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private Backend backend;
	private JPanel pnlCommand;
	private boolean themeChanged = false;

	public PrefPanel(Backend backend)
	{
		this.backend = backend;
		try
		{
			jbInit();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	void jbInit() throws Exception
	{
		//TableModel dataModel = new ListTableModel(new String[] { "Name", "Enabled" }, Preferences.getPlugableCopy("emoticons"));

		//jTable1 = new JTable(dataModel);
		//jTable1.setRowSelectionAllowed(false);

		pnlLookAndFeels = new JPanel();
		pnlLookAndFeels.setLayout(new BoxLayout(pnlLookAndFeels, BoxLayout.Y_AXIS));
		setLayout(borderLayout1);
		//jButton1.setText(I18N.gettext("lookandfeel.Show_Look_and_Feels"));
		I18N.setTextAndMnemonic("lookandfeel.Show_Look_and_Feels",jButton1);
		jButton1.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jButton1_actionPerformed(e);
			}
		});
		//this.add(jScrollPane1, BorderLayout.CENTER);
		this.add(pnlLookAndFeels, BorderLayout.CENTER);
		this.add(jButton1, BorderLayout.NORTH);

		//jScrollPane1.getViewport().add(pnlLookAndFeels, null);
	}

	void jButton1_actionPerformed(ActionEvent e)
	{
		try
		{
			lookAndFeels = Plugin.loadLookAndFeelData();

		}
		catch (Exception e2)
		{
			Popups.errorPopup(I18N.gettext("lookandfeel.lookandfeel_cfg_not_found"), I18N.gettext("lookandfeel.File_not_Found"));
			//e2.printStackTrace();
			return;
		}
		getAvailableLookAndFeels();

		//((ListTableModel) jTable1.getModel()).reload(Preferences.getPlugableCopy("emoticons"));
	}

	private void getAvailableLookAndFeels()
	{
		pnlCommand = new JPanel();
		for (Iterator i = lookAndFeels.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			checkLookAndFeel((String) entry.getKey(), (String) entry.getValue());
		}
		add(pnlCommand, BorderLayout.SOUTH);
		invalidate();
		validate();
		//pnlLookAndFeels.invalidate();
		//pnlLookAndFeels.validate();
		//repaint(); 

	}

	private void checkLookAndFeel(String name, String file)
	{

		//test with pascals claspath thing
		//		ClassLoader systemLoader = ClassLoader.getSystemClassLoader();   
		//		try
		//		{
		//			systemLoader.loadClass(name);
		//		}
		//		catch (ClassNotFoundException e)
		//		{e.printStackTrace(); }

		//System.out.println(file);
		URL url = null;
		try
		{

			//if (Start.programURL != null)
			{
				//loadremote plugins
				url = new URL(Start.programURL + "plugins/lf/" + file);
			}
			//else url = new URL(Start.localURL + "plugins/lf/" + file);
		}
		catch (MalformedURLException e)
		{
			return;
		}

		//File fil = new File(Start.path + "plugins" + File.separator + "lf" + File.separator + file);
		//if (fil.canRead())

		try
		{
			if (url.openConnection().getContentLength() > 0) //check if file exists
			{
				//	System.out.println(file);
				JRadioButton rb = new JRadioButton(name.substring(name.lastIndexOf('.') + 1, name.length()));
				rb.setActionCommand(name);
				buttonGroup.add(rb);
				pnlLookAndFeels.add(rb);
				if (name.equals("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel"))
				{
					JPanel oya = new JPanel();
					oya.add(addFileButton(I18N.gettext("lookandfeel.Set_Oyoaha_Theme"), "oyoahatheme"));
					oya.add(addFileButton(I18N.gettext("lookandfeel.Set_Metal_Theme"), "metaltheme"));
					oya.setBorder(new TitledBorder(I18N.gettext("lookandfeel.Oyoaha_options")));
					pnlCommand.add(oya);
				}
				else if (name.equals("com.l2fprod.gui.plaf.skin.SkinLookAndFeel"))
				{
					JPanel skinlfpanel = new JPanel();
					skinlfpanel.add(addFileButton(I18N.gettext("lookandfeel.Set_Theme"), "skinlftheme"));
					//skinlfpanel.add(addFileButton("Set GTK Theme", "gtktheme"));
					//skinlfpanel.add(addFileButton("Set KDE Theme", "kdetheme"));
					skinlfpanel.setBorder(new TitledBorder(I18N.gettext("lookandfeel.SkinLF_options")));
					pnlCommand.add(skinlfpanel);
				}
				else if (name.equals("org.compiere.plaf.CompiereLookAndFeel"))
				{
					JPanel compierefpanel = new JPanel();
					//JButton btnOK = new JButton(I18N.gettext("lookandfeel.Configure"));
					JButton btnOK = new JButton();
					I18N.setTextAndMnemonic("lookandfeel.Configure",btnOK);
					
					btnOK.addActionListener(new java.awt.event.ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							URLClassloader classloader = (URLClassloader)UIManager.get("ClassLoader");
							try {
								Class s = classloader.loadClass("org.compiere.plaf.CompierePLAFEditor");
								Constructor c = s.getConstructor(new Class[] {Frame.class,boolean.class});
								c.newInstance(new Object[] {backend.getMainFrame(),Boolean.TRUE});
								Class s2 = classloader.loadClass("org.compiere.util.Ini");
								Method m = s2.getMethod("saveProperties",new Class[] {boolean.class});
								m.invoke(null, new Object[] {Boolean.FALSE});
							} catch (Exception e2)
							{
								e2.printStackTrace();
							}
						//CompierePLAF.setPLAF(backend.getMainWindow());
						}
					});
					compierefpanel.add(btnOK);
					compierefpanel.setBorder(new TitledBorder("Compiere"));
					pnlCommand.add(compierefpanel);
				}
				
				
				/*
				else if (
					name.equals("com.jgoodies.plaf.plastic.PlasticLookAndFeel")
						|| name.equals("com.jgoodies.plaf.plastic.Plastic3DLookAndFeel")
						|| name.equals("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel"))
				{
					JPanel skinlfpanel = new JPanel();
					skinlfpanel.add(addileButton("Set Theme", "plastictheme"));
					//skinlfpanel.add(addFileButton("Set GTK Theme", "gtktheme"));
					//skinlfpanel.add(addFileButton("Set KDE Theme", "kdetheme"));
					skinlfpanel.setBorder(new TitledBorder("Plastic options"));
					pnlCommand.add(skinlfpanel);
				}
				*/
			}
		}
		catch (IOException e)
		{}
	}

	private JButton addFileButton(String buttonName, final String key)
	{
		JButton button = new JButton(buttonName);
		final Window window = (Window) this.getTopLevelAncestor();
		button.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				//				ExampleFileFilter filter = new ExampleFileFilter();
				//				   filter.addExtension("jpg");
				//				   filter.addExtension("gif");
				//				   filter.setDescription("JPG & GIF Images");
				//				   chooser.setFileFilter(filter);

				//chooser.setSelectedFile(new File(Start.path));
				int returnVal = chooser.showOpenDialog(window);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					try
					{
						Preferences.putString("lf", key, chooser.getSelectedFile().toURL().toString());
						themeChanged = true;
					}
					catch (MalformedURLException e2)
					{}
				}
			}
		});
		return button;
	}

	public void savePreferences()
	{
		ButtonModel bm = buttonGroup.getSelection();
		if (bm == null)return;
		String name = bm.getActionCommand();
		if (!name.equals(Preferences.getString("lf", "currentLF",null)) || themeChanged)
		{
			String file = (String) lookAndFeels.get(name);
			try
			{
				Plugin.loadLookAndFeel(backend, name, file);
				Preferences.putString("lf", "currentLF", name);
			}
			catch (Exception e)
			{
				Popups.errorPopup(MessageFormat.format(I18N.gettext("lookandfeel.{0}_Could_not_be_loaded"), new Object[] { new String(name) }), I18N.gettext("lookandfeel.Look_and_feel_error"));
			}
			themeChanged = false;
		}
	}
}
/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
