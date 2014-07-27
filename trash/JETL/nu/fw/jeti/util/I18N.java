/*
 * Creado el 30/06/2004 por Rodolfo Gonzalez Gonzalez <rodolfo@equinoxe.g-networks.net>
 *
 * This class is a wrapper for easier i18n of Jeti. It uses the standard i18n 
 * mechanisms given by Java. 
 * 
 */
package nu.fw.jeti.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

import nu.fw.jeti.backend.Start;
import nu.fw.jeti.backend.URLClassloader;

/**
 * @author Rodolfo Gonzalez <rodolfo@equinoxe.g-networks.net>
 */
public class I18N
{
	private static ResourceBundle jetiBundle;
	//private static ResourceBundle pluginsBundle;
	private Language[] languages;
	private Map countries;

	public I18N()
	{
		countries = new HashMap();
		//TODO remove programurl =null fix webstart
		if (Start.programURL != null)
		{
			try
			{
				BufferedReader data = null;
				data =new BufferedReader(new InputStreamReader(new URL(Start.programURL + "languages/list.txt").openStream()));
				List locales = new LinkedList();
				while (true)
				{
					String file = data.readLine();
					if (file ==null) break;//end of stream
					locales.add(new Locale(file));
				}
				data.close();
				extractLanguages(locales);
				extractCountries();
			}
			catch (IOException ex)
			{
				extractLanguages(searchTranslations());
				extractCountries();
				return;
			}
		}
		else
		{
			extractLanguages(searchTranslations());
			extractCountries();
		}
	}

	private List searchTranslations()
	{
		String urlString = Start.path + "languages" + File.separator;
		File path = new File(urlString);
		File file[] = path.listFiles();
		List locales = new LinkedList();
		//locales.add(new Locale("en"));//english is available, but java always selects default locale before base locale
		if (file == null) return locales;
		for (int i = 0; i < file.length; i++)
		{
			Locale locale = getLocale(file[i].getName());
			if (locale != null) locales.add(locale);
		}
		return locales;
	}

	private Locale getLocale(String filename)
	{
		if (!filename.startsWith("jeti") || !filename.endsWith(".properties")) return null;
		int nlen = "jeti".length();
		String language, country = "";
		switch (filename.length() - nlen - 11)
		{
			case 6:
				// E.g. name+"_en_US"+".properties"
				if (filename.charAt(3 + nlen) != '_') return null;
				country = filename.substring(nlen + 4, nlen + 6);
			// no break; here!
			case 3:
				if (filename.charAt(nlen) != '_') return null;
				language = filename.substring(nlen + 1, nlen + 3);
				Locale locale = new Locale(language, country);
				return locale;
		}
		return null;
	}

	private void extractLanguages(List availableLocales)
	{
		Set lang = new HashSet();
		for (Iterator i = availableLocales.iterator(); i.hasNext();)
		{
			Locale l = (Locale) i.next();
			String language = l.getLanguage();
			List langLocs = (List) countries.get(language);
			if (langLocs == null)
			{
				langLocs = new ArrayList();
				countries.put(language, langLocs);
			}
			langLocs.add(new Country(l));
			lang.add(new Language(l));
		}
		//add default locale:
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		List langLocs = (List) countries.get(language);
		if (langLocs == null)
		{
			langLocs = new ArrayList();
			countries.put(language, langLocs);
		}
		lang.add(new Language(l));
		languages = (Language[]) lang.toArray(new Language[0]);
	}

	private void extractCountries()
	{
		Locale[] allLocales = Locale.getAvailableLocales();
		for (int i = 0; i < allLocales.length; i++)
		{
			String language = allLocales[i].getLanguage();
			for (int j = 0; j < languages.length; j++)
			{
				if (language.equals(languages[j].getLanguageCode()))
				{
					List langLocs = (List) countries.get(language);
					Country country = new Country(allLocales[i]);
					if (!langLocs.contains(country)) langLocs.add(country);
					//System.out.println(allLocales[i].getDisplayCountry());
				}
			}
		}
	}

	/**
	 * @return The available languages
	 */
	public Language[] getLanguages()
	{
		return languages;
	}

	/**
	 * @return The countries that are possible with the available languages
	 */
	public Map getCountries()
	{
		return countries;
	}

	/**
	 * Gives back a translation of the input key without '&' (which is used for mnemonics)
	 * 
	 * @param msgid is the English phrase used as key, following the GNU gettext style
	 * @param bundle is the name of the base resource bundle file
	 * @return result (or msgid in case of error)
	 */
	public static String gettext(String msgid)
	{
		String translation = getTextWithAmp(msgid);
		//remove &
		int i = translation.indexOf("&");
		if (i > -1)
		{
			translation = translation.substring(0, i) + translation.substring(i + 1, translation.length());
			//i = translation.indexOf("&",i);
		}
		return translation;
	}

	/**
	 * Gives back a translation of the input key
	 * 
	 * @param msgid is the English phrase used as key, following the GNU gettext style
	 * @param bundle is the name of the base resource bundle file
	 * @return result (or msgid in case of error)
	 */
	public static String getTextWithAmp(String msgid)
	{//TODO change back to private when string prep is implemented
		//ResourceBundle myCatalog = null;
		//if(bundle.equals("jeti")) myCatalog = jetiBundle;
		//if(bundle.equals("plugins")) myCatalog = pluginsBundle;
		if(jetiBundle!=null)
		{
			try
			{
				return (String) jetiBundle.getObject(msgid);
			} catch (MissingResourceException e)
			{
				System.out.println(msgid + " is not translated");
			}
		}
		int dotPos = msgid.lastIndexOf('.');
		if (dotPos>0) msgid = msgid.substring(dotPos+1);
		//return msgid.replaceAll("_"," "); 
		return msgid.replace('_',' '); 
	}

	public static void setTextAndMnemonic(String msgid,JLabel label) {
        setTextAndMnemonic(msgid, label, false);
    }

	public static void setTextAndMnemonic(String msgid,JLabel label, boolean addDots)
	{
		String mnemonicText = getTextWithAmp(msgid);
		String translation=mnemonicText;
		int pos = mnemonicText.indexOf("&");
		if (pos > -1)
		{
			translation = mnemonicText.substring(0, pos) + mnemonicText.substring(pos + 1, mnemonicText.length());
		}
        if (addDots) {
            translation += "...";
        }
		label.setText(translation);
		pos = getMnemonicPosition(mnemonicText);
		if(pos>-1) label.setDisplayedMnemonic(mnemonicText.charAt(pos));
		if(pos>0) label.setDisplayedMnemonicIndex(pos-1);
	}
	
	public static void setMnemonic(String msgid, JLabel label)
	{
		String translation = getTextWithAmp(msgid);
		int pos = getMnemonicPosition(translation);
		if(pos>-1)label.setDisplayedMnemonic(translation.charAt(pos));
		if(pos>1)label.setDisplayedMnemonicIndex(pos-1);
	}
	
	public static void setMnemonic(String msgid, AbstractButton button)
	{
		String translation = getTextWithAmp(msgid);
		int pos = getMnemonicPosition(translation);
		if(pos>-1)button.setMnemonic(translation.charAt(pos));
		if(pos>1)button.setDisplayedMnemonicIndex(pos-1);
	}

	/**
	 * @param msgid is the English phrase used as key, following the
     *              GNU gettext style
	 * @param bundle is the name of the base resource bundle file
	 * @param button The button to set the mnemonic on
	 */
	public static void setTextAndMnemonic(String msgid,AbstractButton button)
	{
        setTextAndMnemonic(msgid, button, false);
    }

	/**
	 * @param msgid is the English phrase used as key, following the
     *              GNU gettext style
	 * @param bundle is the name of the base resource bundle file
	 * @param button The button to set the mnemonic on
	 * @param addDots If true add '...' to the translated text
	 */
	public static void setTextAndMnemonic(String msgid,AbstractButton button,
                                          boolean addDots)
	{
		String mnemonicText = getTextWithAmp(msgid);
		
		String translation=mnemonicText;
		int pos = mnemonicText.indexOf("&");
		if (pos > -1)
		{
			translation = mnemonicText.substring(0, pos) + mnemonicText.substring(pos + 1, mnemonicText.length());
		}
        if (addDots) {
            translation += "...";
        }
		button.setText(translation);
		pos = getMnemonicPosition(mnemonicText);
		if(pos>-1)button.setMnemonic(mnemonicText.charAt(pos));
		if(pos>0) button.setDisplayedMnemonicIndex(pos-1);
		
	}

	private static int getMnemonicPosition(String translation)
	{
		int index = translation.indexOf("&");
		if (index >= 0) return index+1;
		return -1;
	}

	static public void init()
	{
		ClassLoader classLoader=null;
        if (!Start.applet) {
            try {
				URL url = new URL(Start.programURL, "languages/");
                classLoader = new URLClassloader(
                    new URL[] { url}, I18N.class.getClassLoader());
            } catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else classLoader = I18N.class.getClassLoader();
		//		 see if the user has some preferences
		String myLanguage = Preferences.getString("jeti", "language", "");
		String myCountry = Preferences.getString("jeti", "country", "");
		Locale myLocale=null;
        if (myLanguage != "") {
            myLocale = new Locale(myLanguage, myCountry);
        } else {
            myLocale = Locale.getDefault();
        }
        loadLanguage(classLoader,myLocale);
        if(jetiBundle==null || jetiBundle.getLocale().getLanguage().equals(""))
        {
	        // Make it fall back to English, this way we always get at least
	        // the english texts (which contains the menu mnemonic markings)
        	myLocale= new Locale(Locale.ENGLISH.getLanguage(),myCountry);
        	System.out.println("lang not found, default: " + myLocale);
        	loadLanguage(classLoader,myLocale);
        }
        JComponent.setDefaultLocale(myLocale);
		   
        //loadLanguage(classLoader, myLocale);
		UIManager.put("OptionPane.okButtonText", gettext("OK"));
		UIManager.put("OptionPane.cancelButtonText", gettext("Cancel"));
		
	}

	private static void loadLanguage(ClassLoader classLoader, Locale myLocale)
	{
		try {
            jetiBundle = ResourceBundle.getBundle("languages/jeti", myLocale);
        } catch (MissingResourceException e) {
            try {
                jetiBundle = ResourceBundle.getBundle("jeti",
                                                      myLocale, classLoader);
            } catch (MissingResourceException e2) {
                System.out.println(e2.getMessage());
            }
        }
	}

	public class Country
	{
		private String country;
		private String countryCode;

		public Country(Locale locale)
		{
			country = locale.getDisplayCountry();
			if (country.equals("")) country = gettext("main.options.standard.Other");
			countryCode = locale.getCountry();
		}

		public String getCountryCode()
		{
			return countryCode;
		}

		public String toString()
		{
			return country;
		}

	}

	public class Language
	{
		private String language;
		private String languageCode;

		public Language(Locale locale)
		{
			language = locale.getDisplayLanguage();
			languageCode = locale.getLanguage();
		}

		public String getLanguageCode()
		{
			return languageCode;
		}

		public String toString()
		{
			return language;
		}
		
		public boolean equals(Object obj)
		{
			return language.equals(((Language)obj).language);
		}
		
		public int hashCode()
		{
			return language.hashCode();
		}
		
		
	}
}


/*
 * Overrides for emacs
 * Local variables:
 * tab-width: 4
 * End:
 */
