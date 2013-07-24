/*******************************************************************************
 * Copyright or � or Copr. Quentin Godron (2011)
 * 
 * cafe.en.grain@gmail.com
 * 
 * This software is a computer program whose purpose is to create zombie 
 * survival games on Bukkit's server. 
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package graindcafe.tribu.Configuration;

import java.io.File;

import org.bukkit.ChatColor;

public class Constants {
	public static final byte ConfigFileVersion = 2;
	public static final byte LanguageFileVersion = 1;
	public static final String[] languages = { "bulgarian", "dutch", "custom",
			"english", "french", "german-folk", "german", "spanish" };
	public static String dataFolder = "plugins" + File.separator + "Tribu"
			+ File.separator;
	public static String languagesFolder = dataFolder + "languages"
			+ File.separator;
	public static String levelFolder = dataFolder + "levels" + File.separator;
	public static String perLevelFolder = dataFolder + "per-level"
			+ File.separator;
	public static String perWorldFolder = dataFolder + "per-world"
			+ File.separator;
	public static String configFile = dataFolder + "config.yml";
	public static final byte LevelFileVersion = 3;
	public static String MessageMoneyPoints = ChatColor.GREEN + "Money: "
			+ ChatColor.DARK_PURPLE + "%s $" + ChatColor.GREEN + " Points: "
			+ ChatColor.RED + "%s";
	public static String MessageZombieSpawnList = ChatColor.GREEN + "%s";
	// 20 ticks = 1 second
	public static final int TickDelay = 1;
	public static final int TicksBySecond = 20;

	public static final int VoteDelay = TicksBySecond * 30;

	public static boolean rebuildPath(final String dataFolder) {
		Constants.dataFolder = dataFolder;
		languagesFolder = dataFolder + "languages" + File.separator;
		levelFolder = dataFolder + "levels" + File.separator;
		perLevelFolder = dataFolder + "per-level" + File.separator;
		perWorldFolder = dataFolder + "per-world" + File.separator;
		configFile = dataFolder + "config.yml";
		File file = (new File(languagesFolder));
		boolean success = true;
		if (!file.exists())
			success &= file.mkdirs();
		file = (new File(languagesFolder));
		if (!file.exists())
			success &= file.mkdirs();
		file = (new File(levelFolder));
		if (!file.exists())
			success &= file.mkdirs();
		file = (new File(perLevelFolder));
		if (!file.exists())
			success &= file.mkdirs();
		file = (new File(perWorldFolder));
		if (!file.exists())
			success &= file.mkdirs();
		return success;
	}
}
