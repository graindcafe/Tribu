package graindcafe.tribu.Listeners;

import graindcafe.tribu.Tribu;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;

public class TribuWorldListener implements Listener {
	private Tribu plugin;

	public TribuWorldListener(Tribu instance) {
		plugin = instance;
	}

	@EventHandler 
	public void onChunkUnload(ChunkUnloadEvent event) {
		if(!plugin.isCorrectWorld(event.getWorld())) return; //world check
		for (Entity e : event.getChunk().getEntities()) {
			if (e instanceof Zombie && plugin.getSpawner().isSpawned((LivingEntity) e))
				plugin.getSpawner().removedZombieCallback((LivingEntity) e);
		}

	}

	@EventHandler
	public void onWorldUnload(WorldUnloadEvent event) {
		if(!plugin.isCorrectWorld(event.getWorld())) return; //world check
		plugin.stopRunning();
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}
