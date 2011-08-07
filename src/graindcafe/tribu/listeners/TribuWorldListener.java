package graindcafe.tribu.listeners;

import graindcafe.tribu.Tribu;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;

public class TribuWorldListener extends WorldListener {
	private Tribu plugin;

	public TribuWorldListener(Tribu instance) {
		plugin = instance;
	}

	@Override
	public void onChunkUnload(ChunkUnloadEvent event) {

		for (Entity e : event.getChunk().getEntities()) {
			if (e instanceof Zombie && plugin.getSpawner().isSpawned((LivingEntity) e))
				plugin.getSpawner().removedZombieCallback((LivingEntity) e);
		}

	}

	@Override
	public void onWorldUnload(WorldUnloadEvent event) {
		plugin.stopRunning();
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvent(Event.Type.CHUNK_UNLOAD, this, Priority.High, plugin);
		pm.registerEvent(Event.Type.WORLD_UNLOAD, this, Priority.Low, plugin);
	}
}
