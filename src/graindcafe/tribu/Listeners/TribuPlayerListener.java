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
		// If he is playing, then he is inside the world...
		if(plugin.isPlaying(event.getPlayer()))
		{
			plugin.restoreInventory(event.getPlayer());
			plugin.removePlayer(event.getPlayer());
			
		}
		else if(plugin.config().PluginModeWorldExclusive && plugin.isInsideLevel(event.getPlayer().getLocation()))
		{
			// Timed out add player
			//you need this orelse you will get kicked "Moving to fast Hacking?" 
			// its just a .5 of a second delay (it can be set to even less)
			plugin.addPlayer(event.getPlayer(),0.5); 
		}
	}
	
	
	 
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
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
		plugin.restoreInventory(event.getPlayer());
		plugin.removePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.config().PluginModeServerExclusive || plugin.config().PluginModeWorldExclusive && plugin.isInsideLevel(event.getPlayer().getLocation())) {
			plugin.addPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
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
		
		Player player = event.getPlayer();
		if(plugin.config().LevelJail && plugin.isRunning() && plugin.isPlaying(player) && !plugin.isAlive(player)
		&& plugin.getLevel().getDeathSpawn().distance(player.getLocation())>plugin.config().LevelJailRadius)
		{
			player.teleport(plugin.getLevel().getDeathSpawn());
			Tribu.messagePlayer(player,plugin.getLocale("Message.PlayerDSpawnLeaveWarning"));
		}
		
	}

	
	 
	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}
