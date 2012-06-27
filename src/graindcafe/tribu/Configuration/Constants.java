package graindcafe.tribu.Configuration;

import java.io.File;

import org.bukkit.ChatColor;

public class Constants {
	public static final byte ConfigFileVersion = 2;
	public static final byte LanguageFileVersion = 1;
	public static String dataFolder="plugins"+File.separator+"Tribu"+File.separator;
	public static String languagesFolder = dataFolder+"languages"+File.separator;
	public static String levelFolder = dataFolder+"levels";
	public static String perLevelFolder = dataFolder+"per-level"+File.separator;
	public static String perWorldFolder = dataFolder+"per-world"+File.separator;
	public static String configFile = dataFolder+"config.yml";
	public static final byte LevelFileVersion = 3;
	public static String MessageMoneyPoints = ChatColor.GREEN + "Money: " + ChatColor.DARK_PURPLE + "%s $" + ChatColor.GREEN + " Points: "
			+ ChatColor.RED + "%s";
	public static String MessageZombieSpawnList = ChatColor.GREEN + "%s";
	// 20 ticks = 1 second
	public static final int TickDelay = 1;
	public static final int TicksBySecond = 20;

	public static final int VoteDelay = TicksBySecond * 30;
	
	public static void rebuildPath(String dataFolder)
	{
		Constants.dataFolder=dataFolder;
		languagesFolder = dataFolder+"languages"+File.separator;
		levelFolder = dataFolder+"levels";
		perLevelFolder = dataFolder+"per-level"+File.separator;
		perWorldFolder = dataFolder+"per-world"+File.separator;
		configFile = dataFolder+"config.yml";
	}
}
