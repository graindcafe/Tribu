package graindcafe.tribu.Configuration;

import graindcafe.tribu.Package;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TribuDefaultConfiguration {
	/*
	 * Configuration
	 */
	/*
	 * Plugin mode
	 */
	public boolean PluginModeServerExclusive=false;
	//Not used yet
	public boolean PluginModeWorldExclusive=false;
	public String PluginModeWorldExclusiveWorldName="world";
	
	public String PluginModeLanguage="english";
	public boolean PluginModeAutoStart=false;
	public String PluginModeDefaultLevel="";
	
	/*
	 * Level related
	 */
	public double LevelClearZone=50d;
	
	/*
	 * Wave related
	 */
	public boolean WaveStartSetTime=true;
	public int WaveStartSetTimeTo=37000;
	public int WaveStartDelay=10;
	public boolean WaveStartTeleportPlayers=false;
	public boolean WaveStartHealPlayers=false;
	
	/*
	 * Zombies
	 */
	public List<Double> ZombiesQuantity=Arrays.asList(0.5, 1.0, 1.0);
	public List<Double> ZombiesHealth=Arrays.asList(0.5, 4.0);
	public boolean ZombiesFireResistant=false;
	public String ZombiesFocus="None";
	public List<Double> ZombiesTimeToSpawn=Arrays.asList(1.0);
	/*
	 * Stats
	 */
	public int StatsOnZombieKillMoney=15;
	public int StatsOnZombieKillPoints=10;
	public int StatsOnPlayerDeathMoney=10000;
	public int StatsOnPlayerDeathPoints=50;
	public String StatsRewardMethod="Best";
	public boolean StatsRewardOnlyAlive=false;
	/*
	 * Players
	 */
	public boolean PlayersDontLooseItem = false;
	public boolean PlayersStoreInventory = false;
	public boolean PlayersRevertBlocksChanges = true;
	
	/* Default Packages */
	public LinkedList<Package> DefaultPackages=null;
	public Map<String, Object> toMap()
	{
		HashMap<String, Object> map = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
		

			{
				put("PluginMode.WorldExclusive", PluginModeWorldExclusive);
				put("PluginMode.WorldExclusiveWorldName", PluginModeWorldExclusiveWorldName);
				put("PluginMode.ServerExclusive", PluginModeServerExclusive);
				put("PluginMode.Language", PluginModeLanguage);
				put("PluginMode.AutoStart", PluginModeAutoStart);
				put("PluginMode.DefaultLevel", PluginModeDefaultLevel);
				put("Level.ClearZone", LevelClearZone);
				put("WaveStart.SetTime", WaveStartSetTime);
				put("WaveStart.SetTimeTo", WaveStartSetTimeTo);
				put("WaveStart.Delay", WaveStartDelay);
				put("WaveStart.TeleportPlayers", WaveStartTeleportPlayers);
				put("WaveStart.HealPlayers", WaveStartHealPlayers);
				put("Zombies.Quantity", ZombiesQuantity);
				put("Zombies.Health", ZombiesHealth);
				put("Zombies.FireResistant", ZombiesFireResistant);
				put("Zombies.Focus", ZombiesFocus);
				put("Zombies.TimeToSpawn",ZombiesTimeToSpawn);
				put("Stats.OnZombieKill.Money", StatsOnZombieKillMoney);
				put("Stats.OnZombieKill.Points", StatsOnZombieKillPoints);
				put("Stats.OnPlayerDeath.Money", StatsOnPlayerDeathMoney);
				put("Stats.OnPlayerDeath.Points", StatsOnPlayerDeathPoints);
				put("Stats.RewardMethod",StatsRewardMethod);
				put("Stats.RewardOnlyAlive",StatsRewardOnlyAlive);
				put("Players.DontLooseItem", PlayersDontLooseItem);
				put("Players.StoreInventory", PlayersStoreInventory);
				put("Players.RevertBlocksChanges", PlayersRevertBlocksChanges);
				//put("Signs.ShopSign.DropItem", true);
				put("DefaultPackages", DefaultPackages);
			}
		};
		return map;
	}
	
	
}
