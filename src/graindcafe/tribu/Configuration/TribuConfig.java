package graindcafe.tribu.Configuration;

import graindcafe.tribu.Package;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

public class TribuConfig extends TribuDefaultConfiguration{
	
	protected static LinkedList<Package> getDefaultPackages(FileConfiguration config) {
		LinkedList<Package> DefaultPackages=null;
		if (config.isConfigurationSection("DefaultPackages")) {
			DefaultPackages=new LinkedList<Package>();
			Package pck;
			List<Integer> enchIds;
			List<Integer> enchLvls;
			ConfigurationSection defaultPackage = config.getConfigurationSection("DefaultPackages");
			ConfigurationSection pckCs, item;
			byte i = 0;
			HashMap<Enchantment, Integer> enchts = new HashMap<Enchantment, Integer>();

			for (String pckName : defaultPackage.getKeys(false)) {

				pckCs = defaultPackage.getConfigurationSection(pckName);
				if (pckCs != null) {
					pck = new Package(pckName);
					for (String itemName : pckCs.getKeys(false)) {

						item = pckCs.getConfigurationSection(itemName);
						if (item != null && item.contains("id")) {

							enchts.clear();
							if (item.contains("enchantmentsId")) {
								enchIds = item.getIntegerList("enchantmentsId");
								if (item.contains("enchantmentsLevel"))
									enchLvls = item.getIntegerList("enchantmentsLevel");
								else
									enchLvls = new LinkedList<Integer>();
								i = 0;
								for (Integer id : enchIds) {
									enchts.put(Enchantment.getById(id), (enchLvls.size() > i) ? enchLvls.get(i) : 1);
									i++;
								}
							}
							pck.addItem(item.getInt("id"), (short) item.getInt("data", item.getInt("subid", item.getInt("durability", 0))),
									(short) item.getInt("amount", 1), enchts);
						} else
							Logger.getLogger("Minecraft").log(Level.INFO,itemName + " not loaded");
					}
					DefaultPackages.push(pck);
				} else
					Logger.getLogger("Minecraft").log(Level.INFO,pckName + " not loaded");

			}
		}
		return DefaultPackages;
	}
	
	public TribuConfig(FileConfiguration config){
		this(config,new TribuDefaultConfiguration());
	
	
	}
	public TribuConfig()
	{
		this(Constants.configFile);
	}
	public TribuConfig(String config)
	{
		this(new File(config));
	}
	public TribuConfig(File config)
	{
		this(config,new TribuDefaultConfiguration() );
	}
	public TribuConfig(File config,TribuDefaultConfiguration DefaultConfig)
	{
		this(YamlConfiguration.loadConfiguration(config),DefaultConfig);
		Logger.getLogger("Minecraft").info(config.getAbsoluteFile().toString());
	}
	public TribuConfig(FileConfiguration config,TribuDefaultConfiguration DefaultConfig){
		/*try {
			config.load(config);
		} catch (FileNotFoundException e2) {

		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (InvalidConfigurationException e2) {
			e2.printStackTrace();
		}*/
		
		for(String key : config.getKeys(true))
		{
			Logger.getLogger("Minecraft").info(key);
		}
		config.options().header("# Tribu Config File Version " + Constants.ConfigFileVersion + " \n");
		
		HashMap<String, Object> DefaultConfiguration = (HashMap<String, Object>) DefaultConfig.toMap();
		
		for (String key: config.getKeys(true)) {
			this.load(key,config);
			DefaultConfiguration.remove(key);
		}
		// Add missing keys
		for (Entry<String, Object> e : DefaultConfiguration.entrySet()) {
			config.set(e.getKey(), e.getValue());
		}
		// Create the file if it doesn't exist
		try {
			config.save(Constants.configFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//PluginModeServerExclusive = config.getBoolean("PluginMode.ServerExclusive", false);
		checkServerWorldExclusive();

	}
	private void checkServerWorldExclusive() 
	{
		//check servermode and worldmode 
		if(PluginModeServerExclusive && PluginModeWorldExclusive)
		{
			LogSevere("Server Exclusive and World Exclusive can't be both enabled.");
			LogSevere("Server Exclusive Mode is enabled. World Exclusive Mode DISABLED");
			PluginModeWorldExclusive = false;
		}
		if(PluginModeWorldExclusive)
		{
			if(PluginModeWorldExclusiveWorldName == "")
			{
				LogSevere("World Exclusive Mode doesnt contain the set world. DISABLING it.");
				PluginModeWorldExclusive = false;
			}
		}
		
	}

	protected void info(String info)
	{
		Logger.getLogger("Minecraft").info("[Tribu] " + info);
	}
	
	protected void LogSevere(String string)
	{
		Logger.getLogger("Minecraft").severe("[Tribu] " + string);
	}
	public void load(String key,FileConfiguration config)
	{
		String[] keyNode =key.split("\\.");
		byte nodeCount=(byte) keyNode.length;
		info(key);
		if(nodeCount>=2)
		{
		//	info(keyNode[0] + " - "+keyNode[1] );
			if(keyNode[0].equalsIgnoreCase("PluginMode"))
			{
				//if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("ServerExclusive"))
					{
						PluginModeServerExclusive=config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("WorldExclusive"))
					{
						PluginModeWorldExclusive= config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("WorldExclusiveWorldName"))
					{
						PluginModeWorldExclusiveWorldName = config.getString(key);
					}
					else if(keyNode[1].equalsIgnoreCase("Language"))
					{
						PluginModeLanguage= config.getString(key);
					}
					else if(keyNode[1].equalsIgnoreCase("AutoStart"))
					{
						PluginModeAutoStart=config.getBoolean(key) ;
					}
					else if(keyNode[1].equalsIgnoreCase("DefaultLevel"))
					{
						PluginModeDefaultLevel=config.getString(key);
					}
				}
			}else if(keyNode[0].equalsIgnoreCase("Level"))
			{
				//if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("ClearZone"))
					{
						LevelClearZone=(Double) config.getDouble(key);
					}
				}
			}
			else if(keyNode[0].equalsIgnoreCase("WaveStart"))
			{
				//if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("SetTime"))
					{
						WaveStartSetTime=(Boolean) config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("SetTimeTo"))
					{
						info("WaveStartSetTimeTo < "+WaveStartSetTimeTo);
						WaveStartSetTimeTo=(Integer) config.getInt(key);
						info("WaveStartSetTimeTo > "+WaveStartSetTimeTo);
					}
					else if(keyNode[1].equalsIgnoreCase("Delay"))
					{
						 WaveStartDelay=(Integer) config.getInt(key);
					}
					else if(keyNode[1].equalsIgnoreCase("TeleportPlayers"))
					{
						 WaveStartTeleportPlayers=(Boolean) config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("HealPlayers"))
					{
						 WaveStartHealPlayers=(Boolean) config.getBoolean(key);
					}
						
				}
			}
			else if(keyNode[0].equalsIgnoreCase("Zombies"))
			{
				//if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("Quantity"))
					{
						 ZombiesQuantity=(List<Double>) config.getDoubleList(key);
					}
					else if(keyNode[1].equalsIgnoreCase("Health"))
					{
						 ZombiesHealth=(List<Double>) config.getDoubleList(key);
					}
					else if(keyNode[1].equalsIgnoreCase("FireResistant"))
					{
						 ZombiesFireResistant=(Boolean) config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("Focus"))
					{
						 ZombiesFocus=(String) config.getString(key);
					}
					else if(keyNode[1].equalsIgnoreCase("TimeToSpawn"))
						ZombiesTimeToSpawn=(List<Double>) config.getDoubleList(key);
				}
			}
			else if(keyNode[0].equalsIgnoreCase("Stats"))
			{
				if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("OnZombieKill"))
					{
						if(keyNode[2].equalsIgnoreCase("Points"))
						{
							 StatsOnZombieKillPoints=(Integer) config.getInt(key);
						}
						else if(keyNode[2].equalsIgnoreCase("Money"))
						{
							 StatsOnZombieKillMoney=(Integer) config.getInt(key);
						}
					}
					else if(keyNode[1].equalsIgnoreCase("OnPlayerDeath"))
					{
						if(keyNode[2].equalsIgnoreCase("Points"))
						{
							 StatsOnPlayerDeathPoints=(Integer) config.getInt(key);
						}
						else if(keyNode[2].equalsIgnoreCase("Money"))
						{
							 StatsOnPlayerDeathMoney=(Integer) config.getInt(key);
						}
					}
					else if(keyNode[1].equalsIgnoreCase("RewardMethod"))
						StatsRewardMethod=config.getString(key);
					else if(keyNode[1].equalsIgnoreCase("RewardOnlyAlive"))
						StatsRewardOnlyAlive=config.getBoolean(key);
				}
			}
			else if(keyNode[0].equalsIgnoreCase("Players"))
			{
				//if(nodeCount>2)
				{
					if(keyNode[1].equalsIgnoreCase("DontLooseItem"))
					{
						 PlayersDontLooseItem=(Boolean) config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("StoreInventory"))
					{
						 PlayersStoreInventory=(Boolean) config.getBoolean(key);
					}
					else if(keyNode[1].equalsIgnoreCase("RevertBlocksChanges"))
					{
						 PlayersRevertBlocksChanges=(Boolean) config.getBoolean(key);
					}
				}
			}
			else if(keyNode[0].equalsIgnoreCase("DefaultPackages"))
			{
				 return;
			}
			else
			{
				info("Not found : "+key);
				try {
					this.getClass().getField(key).set(toMap().get(key), config.get(key));
				} catch (Exception e)
				{
					info("Failed "+key);
					return;
				}
				
			}
		}
		else if(key.equalsIgnoreCase("DefaultPackages"))
		{
			 DefaultPackages=(LinkedList<Package>) TribuConfig.getDefaultPackages(config);
		}
		else
			info("Section : "+key);
		return;
	}
	
	
	
}
