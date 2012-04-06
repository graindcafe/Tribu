package graindcafe.tribu.listeners;

import graindcafe.tribu.CleverMob;
import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class TribuEntityListener implements Listener {
	private Tribu plugin;

	public TribuEntityListener(Tribu instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL )
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if ((plugin.isDedicatedServer() || plugin.isRunning()) && !plugin.getSpawner().justSpawned()) {
			event.setCancelled(true);
		}

	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent dam) {
		if (dam.isCancelled()) {
			return;
		}
		if (dam.getCause().equals(DamageCause.FIRE_TICK) && plugin.getConfig().getBoolean("Zombies.FireResistant", false)) {
			dam.setCancelled(true);
			dam.getEntity().setFireTicks(0);
			return;
		}
		if (plugin.isRunning() && dam.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dam;
			if (event.getEntity() instanceof LivingEntity) {

				if (plugin.getSpawner().isSpawned((LivingEntity) event.getEntity())) {

					if (event.getDamager() instanceof Player) {
						/* CleverMob mob = */plugin.getSpawner().getCleverMob((LivingEntity) event.getEntity())
						/* ; */
						/* mob */.setAttacker((Player) event.getDamager());
					}

				}

			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (plugin.isRunning() && event.getEntity() instanceof LivingEntity) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				plugin.setDead(player);

				if (plugin.getConfig().getBoolean("Players.DontLooseItem", false))
					plugin.keepTempInv((Player) event.getEntity(), event.getDrops().toArray(new ItemStack[] {}));
				event.getDrops().clear();

			} else if (event.getEntity() instanceof Zombie) {
				Zombie zombie = (Zombie) event.getEntity();
				CleverMob mob = plugin.getSpawner().getCleverMob(zombie);
				if (mob != null) {
					Player player = mob.getLastAttacker();
					if (player == null && zombie.getTarget() instanceof Player)
						player = (Player) zombie.getTarget();
					if (player != null && player.isOnline()) {
						PlayerStats stats = plugin.getStats(player);
						if (stats != null) {
							stats.addMoney(plugin.getConfig().getInt("Stats.OnZombieKill.Money", 10));
							stats.addPoints(plugin.getConfig().getInt("Stats.OnZombieKill.Points", 15));
							stats.msgStats();
							plugin.getLevel().onWaveStart();
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
		pm.registerEvent(org.bukkit.event.entity.EntityDeathEvent.class, this, org.bukkit.event.EventPriority.MONITOR, null, plugin);
		pm.registerEvent(org.bukkit.event.entity.CreatureSpawnEvent.class, this, org.bukkit.event.EventPriority.LOWEST, null, plugin);
		pm.registerEvent(org.bukkit.event.entity.EntityDamageEvent.class, this, org.bukkit.event.EventPriority.HIGH, null, plugin);

	}

}
