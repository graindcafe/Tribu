package graindcafe.tribu;

import java.util.HashMap;

import org.bukkit.util.config.Configuration;

public class Language {
	protected Configuration File;
	protected Language Default;
	protected HashMap<String, String> finalStrings;

	protected Language() {

	}

	public Language(String LanguageName) {
		java.io.File f = new java.io.File(Constants.languagesFolder + LanguageName);
		if (!f.exists()) {
			f = new java.io.File(Constants.languagesFolder + LanguageName + ".yml");
		}

		if (f.exists()) {
			File = new Configuration(f);
			File.load();

			if (File.getString("Default", null) != null) {
				Default = new Language(File.getString("Default"));
			} else {
				Default = new DefaultLanguage();
			}
		} else {
			File = null;
			Default = new DefaultLanguage();
		}
		finalStrings = new HashMap<String, String>();

	}

	public String get(String key) {
		if (finalStrings.containsKey(key)) {
			return finalStrings.get(key);
		} else {
			String finalString;
			if (File != null) {
				finalString = File.getString(key, null);
				if (finalString != null)
					finalString = parseColor(finalString);
				else
					finalString = Default.get(key);
			} else
				finalString = Default.get(key);
			finalStrings.put(key, finalString);
			return finalString;
		}
	}

	public String getAuthor() {
		if (File != null)
			return File.getString("Author", "Anonymous") + (Default instanceof DefaultLanguage ? "" : ", " + Default.getAuthor());
		else
			return Default.getAuthor();
	}

	public byte getVersion() {
		if (File != null)
			return (byte) File.getInt("Version", Default instanceof DefaultLanguage ? 0 : Default.getVersion());
		else
			return (byte) 255;
	}

	public String parseColor(String s) {
		return s.replaceAll("&(\\w{1})", "\u00A7$1");
	}
}
