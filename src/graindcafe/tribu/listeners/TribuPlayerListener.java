package graindcafe.tribu.listeners;

import graindcafe.tribu.Tribu;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

public class TribuPlayerListener extends PlayerListener {
	private final Tribu plugin;

	public TribuPlayerListener(Tribu instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		// TODO:Delete all this
		if (!event.isCancelled()
				&& event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& plugin.isRunning()) {

			Block block = event.getClickedBlock();
			if (block != null) {

				if (block.getType() == Material.SIGN_POST
						|| block.getType() == Material.WALL_SIGN && plugin.getLevel() != null) {
					// Get the sign
					plugin.getLevel().updateSigns(event);
				}
			}
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		plugin.addPlayer(event.getPlayer());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {

		plugin.removePlayer(event.getPlayer());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (plugin.getLevel() != null) {
			plugin.setDead(event.getPlayer());
			event.setRespawnLocation(plugin.getLevel().getDeathSpawn());
		}
	}

	public void registerEvents(PluginManager pm) {
		if (plugin.isDedicatedServer()) {
			pm.registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Normal,
					plugin);
			pm.registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Normal,
					plugin);
		}
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Normal,
				plugin);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this, Priority.Normal,
				plugin);
	}
}
