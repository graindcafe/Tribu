package graindcafe.tribu.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class TribuYaml extends YamlConfiguration implements ValueReader {

	public static TribuYaml loadConfiguration(File file) {
		Validate.notNull(file, "File cannot be null");

		TribuYaml config = new TribuYaml();

		try {
			config.load(file);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return config;
	}

	public static TribuYaml reload(File configFile, InputStream defConfigStream) {
		TribuYaml newConfig;
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			newConfig = TribuYaml.loadConfiguration(configFile);
			newConfig.setDefaults(defConfig);
		} else
			newConfig = TribuYaml.loadConfiguration(configFile);

		return newConfig;
	}
}
