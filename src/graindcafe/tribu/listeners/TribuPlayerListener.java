package graindcafe.tribu.listeners;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.signs.TribuSign;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
		if (!event.isCancelled()) {

			Block block = event.getClickedBlock();
			if (block != null) {

				if (Sign.class.isInstance(block.getState()) && plugin.getLevel() != null) {
					if (plugin.isRunning()) {
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
							plugin.getLevel().onSignClicked(event);
					} else if (event.getPlayer().isOp()) {
						if (plugin.getLevel().removeSign(block.getLocation()))
							event.getPlayer().sendMessage(plugin.getLocale("Message.TribuSignRemoved"));
						else if (plugin.getLevel().addSign(TribuSign.getObject(plugin, block.getLocation())))
							event.getPlayer().sendMessage(plugin.getLocale("Message.TribuSignAdded"));
					}
				} else if (plugin.isRunning()) {
					plugin.getLevel().onClick(event);
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
			plugin.restoreTempInv(event.getPlayer());
			if (!plugin.isPlaying(event.getPlayer()))
				plugin.restoreInventory(event.getPlayer());
		}
	}

	public void registerEvents(PluginManager pm) {
		if (plugin.isDedicatedServer()) {
			pm.registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Normal, plugin);
		}
		pm.registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this, Priority.Normal, plugin);
	}
}
