package graindcafe.tribu;

import graindcafe.tribu.executors.CmdDspawn;
import graindcafe.tribu.executors.CmdIspawn;
import graindcafe.tribu.executors.CmdTribu;
import graindcafe.tribu.executors.CmdZspawn;
import graindcafe.tribu.listeners.TribuBlockListener;
import graindcafe.tribu.listeners.TribuEntityListener;
import graindcafe.tribu.listeners.TribuPlayerListener;
import graindcafe.tribu.listeners.TribuWorldListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Tribu extends JavaPlugin {
	public static String getExceptionMessage(Exception e) {
		String message = e.getLocalizedMessage() + "\n";
		for (StackTraceElement st : e.getStackTrace())
			message += "[" + st.getFileName() + ":" + st.getLineNumber() + "] " + st.getClassName() + "->" + st.getMethodName() + "\n";
		return message;
	}

	private TribuPlayerListener playerListener;
	private TribuEntityListener entityListener;
	private TribuBlockListener blockListener;
	private TribuWorldListener worldListener;
	private LinkedList<PlayerStats> sortedStats;
	private LevelFileLoader levelLoader;
	private LevelSelector levelSelector;
	private Random rnd;

	private TribuLevel level;
	private TribuSpawner spawner;
	private SpawnTimer spawnTimer;

	private WaveStarter waveStarter;
	private Logger log;
	private boolean isRunning;
	private int aliveCount;

	private Language Language;
	private boolean dedicatedServer = false;

	private HashMap<Player, PlayerStats> players;

	public void addPlayer(Player player) {
		if (!players.containsKey(player)) {
			PlayerStats stats = new PlayerStats(player);
			players.put(player, stats);
			sortedStats.add(stats);
			if (getLevel() != null && isRunning) {
				player.teleport(level.getDeathSpawn());
				player.sendMessage(Language.get("Message.GameInProgress"));
			}
		}
	}

	public void checkAliveCount() {
		if (aliveCount == 0 && isRunning) {
			stopRunning();
			getServer().broadcastMessage(Language.get("Message.ZombieHavePrevailed"));
			getServer().broadcastMessage(String.format(Language.get("Message.YouHaveReachedWave"), String.valueOf(getWaveStarter().getWaveNumber())));
			if (getPlayersCount() != 0)
				getLevelSelector().startVote(Constants.VoteDelay);
		}
	}

	public int getAliveCount() {
		return aliveCount;
	}

	public TribuLevel getLevel() {
		return level;
	}

	public LevelFileLoader getLevelLoader() {
		return levelLoader;
	}

	public LevelSelector getLevelSelector() {
		return levelSelector;
	}

	public String getLocale(String key) {
		String r = Language.get(key);
		if (r == null)
			LogWarning(key + " not found");
		return r;
	}

	public Set<Player> getPlayers() {
		return this.players.keySet();
	}

	public Player getRandomPlayer() {
		int n = rnd.nextInt(players.size());
		Iterator<Player> i = players.keySet().iterator();
		while (n > 0) {
			i.next();
			n++;
		}
		return i.next();
	}

	public int getPlayersCount() {
		return this.players.size();
	}

	public LinkedList<PlayerStats> getSortedStats() {
		Collections.sort(this.sortedStats);
		int c = this.sortedStats.size();
		int i = 0;
		while (i < c) {
			LogInfo(this.sortedStats.get(i).getPlayer().getDisplayName() + this.sortedStats.get(i).getPoints());
			i++;
		}
		return this.sortedStats;
	}

	public TribuSpawner getSpawner() {
		return spawner;
	}

	public SpawnTimer getSpawnTimer() {
		return spawnTimer;
	}

	public PlayerStats getStats(Player player) {
		return players.get(player);
	}

	public WaveStarter getWaveStarter() {
		return waveStarter;
	}

	public boolean isAlive(Player player) {
		return players.get(player).isalive();
	}

	public boolean isDedicatedServer() {
		return dedicatedServer;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void LogInfo(String message) {
		log.info(message);
	}

	public void LogSevere(String message) {
		log.severe(message);
	}

	public void LogWarning(String message) {
		log.warning(message);
	}

	public void Message(CommandSender sender, String message) {
		if (sender instanceof Player)
			sender.sendMessage(message);
		else if (sender == null)
			log.info(ChatColor.stripColor(message));
		else
			sender.sendMessage(message);
	}

	@Override
	public void onDisable() {
		players.clear();
		sortedStats.clear();
		stopRunning();
		LogInfo(Language.get("Info.Disable"));
	}

	@Override
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		rnd = new Random();
		getConfiguration().setHeader(
				"# Tribu Config File Version " + Constants.ConfigFileVersion + " \n" + "# Here is the default settings :\n" + "# PluginMode:\n"
						+ "#      ServerExclusive: true\n" + "#      Language: english\n" + "# WaveStart:\n" + "#      SetTime: true\n"
						+ "#      SetTimeTo: 37000\n" + "#      Delay: 10\n" + "#      TeleportPlayers:false\n" + "# Zombies:\n"
						+ "#      Health: [0.5,4.0]\n" + "#      Quantity: [0.5,1.0,1.0]\n" + "#      LightResistant: false\n"
						+ "#      # Could be None,Nearest,Random,DeathSpawn,InitialSpawn\n" + "#      Focus: None\n" + "# Stats:\n"
						+ "#    OnZombieKill:\n" + "#       Money: 15\n" + "#       Points: 10\n" + "#    OnPlayerDeath:\n" + "#       Money:10000\n"
						+ "#       Points:50\n" + "# Players:\n" + "#    DontLooseItem: false\n");
		// Create the file if it doesn't exist
		getConfiguration().save();
		if (getConfiguration().getString("PluginMode.Language", null) != null) {
			// LogInfo("#"+getConfiguration().getString("PluginMode.Language",null)+"#");
			Language = new Language(getConfiguration().getString("PluginMode.Language"));
		} else {
			Language = new DefaultLanguage();
		}
		LogInfo(String.format(Language.get("Info.ChosenLanguage"), getConfiguration().getString("PluginMode.Language"), Language.getAuthor()));
		if (Language.getVersion() != Constants.LanguageFileVersion)
			if (Language.getVersion() == 255)
				LogWarning(Language.get("Warning.LanguageFileMissing"));
			else
				LogWarning(Language.get("Warning.LanguageFileOutdated"));
		Constants.MessageMoneyPoints = Language.get("Message.MoneyPoints");
		Constants.MessageZombieSpawnList = Language.get("Message.ZombieSpawnList");
		dedicatedServer = getConfiguration().getBoolean("PluginMode.ServerExclusive", true);
		/*
		 * if(dedicatedServer) LogInfo("dedicated"); else
		 * LogInfo("not dedicated");
		 * 
		 * for(Entry<String, Object> cur :
		 * getConfiguration().getAll().entrySet()) {
		 * LogInfo(cur.getKey()+" = "+cur.getValue().toString()); }
		 */
		isRunning = false;
		aliveCount = 0;
		level = null;
		players = new HashMap<Player, PlayerStats>();
		sortedStats = new LinkedList<PlayerStats>();
		levelLoader = new LevelFileLoader(this);
		levelSelector = new LevelSelector(this);
		// Create listeners
		playerListener = new TribuPlayerListener(this);
		entityListener = new TribuEntityListener(this);
		blockListener = new TribuBlockListener(this);
		worldListener = new TribuWorldListener(this);
		PluginManager pm = getServer().getPluginManager();
		playerListener.registerEvents(pm);
		entityListener.registerEvents(pm);
		blockListener.registerEvents(pm);
		worldListener.registerEvents(pm);
		spawner = new TribuSpawner(this);
		spawnTimer = new SpawnTimer(this);
		waveStarter = new WaveStarter(this);

		getCommand("dspawn").setExecutor(new CmdDspawn(this));
		getCommand("zspawn").setExecutor(new CmdZspawn(this));
		getCommand("ispawn").setExecutor(new CmdIspawn(this));
		getCommand("tribu").setExecutor(new CmdTribu(this));
		// getCommand("vote").setExecutor(new CmdVote(this));
		// getCommand("level").setExecutor(new CmdLevel(this));
		/*
		 * List<String> zmAliases = new LinkedList<String>();
		 * zmAliases.add("zm"); zmAliases.add("zombie");
		 * getCommand("zombiemode").setAliases(zmAliases);
		 * getCommand("zombiemode").setExecutor(new CmdZombieMode(this));
		 */

		int i = 0;
		Player[] tmpPlayerList = this.getServer().getOnlinePlayers();
		if (dedicatedServer) {
			int c = tmpPlayerList.length;
			while (i < c) {
				this.addPlayer(tmpPlayerList[i]);
				i++;
			}
		}
		LogInfo(Language.get("Info.Enable"));
	}

	public void removePlayer(Player player) {
		if (player != null && players.containsKey(player)) {
			if (isAlive(player)) {
				aliveCount--;
			}
			checkAliveCount();
			sortedStats.remove(players.get(player));
			players.remove(player);

		}
	}

	public void revivePlayer(Player player) {

		players.get(player).revive();
		player.setHealth(20);
		aliveCount++;
	}

	public void revivePlayers(boolean teleportAll) {
		Player[] players = getServer().getOnlinePlayers();
		aliveCount = 0;
		for (Player player : players) {
			revivePlayer(player);
			if (isRunning && level != null && (teleportAll || !isAlive(player))) {
				player.teleport(level.getInitialSpawn());
			}

		}
	}

	public void setDead(Player player) {
		if (isAlive(player)) {
			aliveCount--;
			PlayerStats p = players.get(player);
			p.resetMoney();
			p.subtractmoney(getConfiguration().getInt("Stats.OnPlayerDeath.Money", 10000));
			p.subtractPoints(getConfiguration().getInt("Stats.OnPlayerDeath.Points", 50));
			p.msgStats();
			/*
			 * Set<Entry<Player, PlayerStats>> stats = players.entrySet(); for
			 * (Entry<Player, PlayerStats> stat : stats) {
			 * stat.getValue().subtractPoints(50); stat.getValue().resetMoney();
			 * stat.getValue().msgStats(); }
			 */
		}
		players.get(player).kill();
		if (getLevel() != null && isRunning) {
			checkAliveCount();
		}
	}

	public void setLevel(TribuLevel level) {
		this.level = level;
	}

	public void startRunning() {
		if (!isRunning && getLevel() != null && !players.isEmpty()) {
			isRunning = true;
			if (dedicatedServer)
				for (LivingEntity e : level.getInitialSpawn().getWorld().getLivingEntities()) {

					if (!(e instanceof Player) && !(e instanceof Wolf))
						e.damage(100000);

				}
			// Make sure no data is lost if server decides to die
			// during a game and player forgot to /level save
			if (!getLevelLoader().saveLevel(getLevel())) {
				LogWarning(Language.get("Warning.UnableToSaveLevel"));
			} else {
				LogInfo(Language.get("Info.LevelSaved"));
			}
			getLevel().initSigns();
			Set<Entry<Player, PlayerStats>> stats = players.entrySet();
			for (Entry<Player, PlayerStats> stat : stats) {
				stat.getValue().resetPoints();
				stat.getValue().resetMoney();
			}

			getWaveStarter().resetWave();
			revivePlayers(true);
			getWaveStarter().scheduleWave(Constants.TicksBySecond * getConfiguration().getInt("WaveStart.Delay", 10));
		}
	}

	public void stopRunning() {
		if (isRunning) {
			isRunning = false;
			getSpawnTimer().Stop();
			getWaveStarter().cancelWave();
			getSpawner().clearZombies();
			getLevelSelector().cancelVote();
			if (!dedicatedServer)
				players.clear();
		}

	}

}
