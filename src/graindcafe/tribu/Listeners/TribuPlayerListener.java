package graindcafe.tribu.Listeners;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.Signs.TribuSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.block.Action;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.bukkit.plugin.PluginManager;

public class TribuPlayerListener implements Listener {
	private final Tribu plugin;

	public TribuPlayerListener(Tribu instance) {
		plugin = instance;
	}
	public Tribu getPlugin()
	{
		return plugin;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event)
	{
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) 
		{
			if(!plugin.config().PluginModeAutoStart) return;
			plugin.restoreInventory(event.getPlayer());
			plugin.removePlayer(event.getPlayer());
			checkIfNeededAutoStart();
			return; //They are leaving this world so if they are a player then remove them
		}
		checkIfNeededAutoStart();
		//if(!plugin.config().PluginModeAutoStart) return;
		if(plugin.config().PluginModeWorldExclusive) // it will always be true because of above statement, but just in case if error.
		{
			timedOutAddPlayer(event.getPlayer(),0.5); //you need this orelse you will get kicked "Moving to fast Hacking?" its just a .5 of a second delay (it can be set to even less)
			checkIfNeededAutoStart();
		}
	}
	
	public void checkIfNeededAutoStart()
	{
		if(plugin.config().PluginModeServerExclusive) return;
		if(plugin.config().PluginModeAutoStart)
		{
			if(plugin.getPlayers().size() < 1) //if no players
			{
				plugin.setWaitingForPlayers(true);
			}
		}
	}
	
	public void timedOutAddPlayer(final Player player, final double timeout)
	{
		 new Thread(new Runnable() 
		 {
			 public void run() 
			 {
				 double timerat = 0;
				long timeout2 = 500;
				 
				while (timerat <= timeout) 
				{
					try 
					{
						double getTime = timeout - timerat;
						if(getTime == 0)
						{
							plugin.addPlayer(player);
						}
						Thread.sleep(timeout2);
					}
					catch (InterruptedException ie) 
					{
						ie.printStackTrace();
					}			  
					timerat = timerat + 0.5;
				}
				
			 }
		 }).start();
	}
	 
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) return; //world check
		if (!event.isCancelled()) {
			Block block = event.getClickedBlock();
			if (block != null && plugin.isPlaying(event.getPlayer())) {
				plugin.getBlockTrace().push(block, true);
				if (Sign.class.isInstance(block.getState()) && plugin.getLevel() != null) {
					if (plugin.isRunning()) {
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
							plugin.getLevel().onSignClicked(event);
					} else if (event.getPlayer().hasPermission("tribu.signs.place")) {
						if (plugin.getLevel().removeSign(block.getLocation()))
							Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.TribuSignRemoved"));
						else if (plugin.getLevel().addSign(TribuSign.getObject(plugin, block.getLocation())))
							Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.TribuSignAdded"));
					}
				} else if (plugin.isRunning()) {
					plugin.getLevel().onClick(event);
				}
			}
		}
	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) return; //world check
		plugin.restoreInventory(event.getPlayer());
		plugin.removePlayer(event.getPlayer());
		checkIfNeededAutoStart();
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) return; //world check
		if (plugin.config().PluginModeServerExclusive || plugin.config().PluginModeWorldExclusive) {
			if(!plugin.config().PluginModeAutoStart) return;
			if(plugin.getPlayers().size() < 1)
			{
				//if players size is < 1 (0) then send new wave start message (because not server exclusive)
				Tribu.messagePlayer(event.getPlayer(),String.format(plugin.getLocale("Broadcast.Wave"), String.valueOf(plugin.getWaveStarter().getWaveNumber()),String.valueOf(10)));
			}
			plugin.addPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) return; //world check
		if (plugin.getLevel() != null) {
			plugin.setDead(event.getPlayer());
			plugin.resetedSpawnAdd(event.getPlayer(),event.getRespawnLocation());
			
			event.setRespawnLocation(plugin.getLevel().getDeathSpawn());
			plugin.restoreTempInv(event.getPlayer());
			if (!plugin.isPlaying(event.getPlayer()))
				plugin.restoreInventory(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if(!plugin.isCorrectWorld(event.getPlayer().getWorld())) return; //world check
		Player player = event.getPlayer();
		for(Player pp:plugin.deadPeople.keySet())
		{
			if(player == pp)
			{
				if(getDist((int)player.getLocation().getBlockX(),(int)player.getLocation().getBlockY(),(int)player.getLocation().getBlockZ(),(int)plugin.getLevel().getDeathSpawn().getBlockX(),(int)plugin.getLevel().getDeathSpawn().getBlockY(),(int)plugin.getLevel().getDeathSpawn().getBlockZ()) >= 5)
				{
					pp.teleport(plugin.getLevel().getDeathSpawn());
					Tribu.messagePlayer(player,plugin.getLocale("Message.PlayerDSpawnLeaveWarning"));
				}
			}
		}
	}

	public static int getDist(int x, int y, int z, int xx, int yy, int zz)
	{
		int d = x - xx;
		int d1 = y - yy;
		int d2 = z - zz;
		return (int) Math.sqrt(d * d + d1 * d1 + d2 * d2);
	}
	 
	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}
