package graindcafe.tribu;

import graindcafe.tribu.Configuration.Constants;
import graindcafe.tribu.Configuration.TribuConfig;
import graindcafe.tribu.Level.TribuLevel;
import graindcafe.tribu.TribuZombie.EntityTribuZombie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import me.graindcafe.gls.Language;
import net.minecraft.server.v1_6_R2.EntityTypes;
import net.minecraft.server.v1_6_R2.EntityZombie;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class TribuPlugin extends JavaPlugin {
	Map<TribuLevel, Tribu> games;
	Language defaultLanguage;
	private Logger log;
	private Random rnd;
	private Metrics metrics;
	private TribuConfig defaultConfig;

	public String getLocale(final String key) {
		if (key != null) {
			String locale = defaultLanguage.get(key);
			if (locale != null)
				return locale;
		}
		return "(null)";
	}

	@Override
	public void onDisable() {
		for (Tribu game : games.values()) {
			game.finalize();
		}
		log.info(defaultLanguage.get("Info.Disable"));
	}

	@Override
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		rnd = new Random();
		final boolean mkdirs = Constants.rebuildPath(getDataFolder().getPath()
				+ File.separatorChar);
		boolean langCopy = true;
		for (final String name : Constants.languages) {
			final InputStream fis = this.getClass().getResourceAsStream(
					"/res/languages/" + name + ".yml");
			FileOutputStream fos = null;
			final File f = new File(Constants.languagesFolder + name + ".yml");
			{
				try {
					fos = new FileOutputStream(f);
					final byte[] buf = new byte[1024];
					int i = 0;

					if (f != null && fis != null && f.canWrite()
							&& fis.available() > 0)
						while ((i = fis.read(buf)) > 0)
							fos.write(buf, 0, i);
				} catch (final Exception e) {
					e.printStackTrace();
					langCopy = false;
				} finally {
					try {
						if (fis != null)
							fis.close();
						if (fos != null)
							fos.close();
					} catch (final Exception e) {
					}
				}
			}
		}
		try {
			final Method a = EntityTypes.class.getDeclaredMethod("a",
					Class.class, String.class, Integer.TYPE);
			a.setAccessible(true);

			a.invoke(a, EntityTribuZombie.class, "Zombie", 54);
			a.invoke(a, EntityZombie.class, "Zombie", 54);

		} catch (final Exception e) {
			setEnabled(false);
			e.printStackTrace();
			return;
		}

		if (!mkdirs)
			log.severe(getLocale("Severe.TribuCantMkdir"));
		if (!langCopy)
			log.severe(getLocale("Severe.CannotCopyLanguages"));
		try {
			metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
		// TODO:Load default config
		// reloadConf()

		log.info(defaultLanguage.get("Info.Enable"));
		if (defaultConfig.PluginModeAutoStart) {
			// TODO: Handle autostart
			// startRunning();
		}
	}

}
