package graindcafe.tribu.listeners;

import graindcafe.tribu.CleverMob;
import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginManager;

public class TribuEntityListener extends EntityListener {
	private Tribu plugin;

	public TribuEntityListener(Tribu instance) {
		plugin = instance;
	}

	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if ((plugin.isDedicatedServer() || plugin.isRunning())
				&& !plugin.getSpawner().justSpawned()) {
			event.setCancelled(true);
		}

	}

	@Override
	public void onEntityDamage(EntityDamageEvent dam) {
		if (dam.isCancelled()) {
			return;
		}
		if (plugin.isRunning() && dam.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dam;
			if (event.getEntity() instanceof LivingEntity) {

				if (plugin.getSpawner().isSpawned(
						(LivingEntity) event.getEntity())) {

					if (event.getDamager() instanceof Player) {
						/* CleverMob mob = */plugin
								.getSpawner()
								.getCleverMob((LivingEntity) event.getEntity())
								/* ; */
								/* mob */.setAttacker(
										(Player) event.getDamager());
					}

				}

			}
		}
	}


	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (plugin.isRunning() && event.getEntity() instanceof LivingEntity) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				plugin.setDead(player);
				event.getDrops().clear();
			} else if (event.getEntity() instanceof Zombie) {
				Zombie zombie = (Zombie) event.getEntity();
				CleverMob mob = plugin.getSpawner().getCleverMob(zombie);
				if (mob != null) {
					Player player = mob.getLastAttacker();
					if (player != null && player.isOnline()) {
						PlayerStats stats = plugin.getStats(player);
						if (stats != null) {
							stats.addMoney(15);
							stats.addPoints(10);
							stats.msgStats();
							plugin.getLevel().updateSigns();
						} else {
							mob.setAttacker(null);
						}
					}
				}
				plugin.getSpawner().despawnZombie(zombie, event.getDrops());
			}
		}
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvent(Event.Type.ENTITY_DEATH, this, Priority.Monitor,
				plugin);
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this, Priority.Lowest,
				plugin);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this, Priority.High, plugin);
			
	}

}
