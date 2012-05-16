package graindcafe.tribu.listeners;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.TribuZombie.CraftTribuZombie;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
	double clearZone = 50f;

	public TribuEntityListener(Tribu instance) {
		plugin = instance;
	}

	public void resetClearZone() {
		clearZone = plugin.getConfig().getDouble("Level.ClearZone", 50.0);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if ((plugin.isDedicatedServer() || (plugin.isRunning() && plugin.getLevel() != null && event.getEntity().getLocation()
				.distance(plugin.getLevel().getInitialSpawn()) <= clearZone))
				&& !plugin.getSpawner().justSpawned()) {
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent dam) {
		if (dam.isCancelled()) {
			return;
		}
		if (dam.getEntity() instanceof Player) {
			Player p = (Player) dam.getEntity();
			if (plugin.isPlaying(p)) {
				if (p.getHealth() - dam.getDamage() < 0) {
					dam.setCancelled(true);
					p.teleport(plugin.getLevel().getDeathSpawn());
					p.setHealth(1);
					if (!plugin.getConfig().getBoolean("Players.DontLooseItem", false))
						p.getInventory().clear();
					plugin.setDead(p);
				}
			} else
				plugin.restoreInventory(p);
		}
		if (dam.getEntity() instanceof CraftTribuZombie) {
			if (dam.getCause().equals(DamageCause.FIRE_TICK) && plugin.getConfig().getBoolean("Zombies.FireResistant", false)) {
				dam.setCancelled(true);
				dam.getEntity().setFireTicks(0);
				return;
			}

			if (plugin.isRunning()
					&& (dam.getCause() == DamageCause.ENTITY_ATTACK || dam.getCause() == DamageCause.PROJECTILE || dam.getCause() == DamageCause.POISON)) {
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dam;

				CraftTribuZombie zomb = (CraftTribuZombie) event.getEntity();
				Player p = null;
				if (event.getDamager() instanceof Projectile) {
					Projectile pj = (Projectile) event.getDamager();
					if (pj.getShooter() instanceof Player)
						p = (Player) pj.getShooter();
				} else if (event.getDamager() instanceof Player) {
					p = (Player) event.getDamager();
				} else if (zomb.getTarget() instanceof Player)
					p = (Player) zomb.getTarget();
				if (p != null)
					zomb.addAttack(p, event.getDamage());
			}

		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			plugin.setDead(player);

			if (plugin.getConfig().getBoolean("Players.DontLooseItem", false))
				plugin.keepTempInv((Player) event.getEntity(), event.getDrops().toArray(new ItemStack[] {}));

			event.getDrops().clear();

		} else if (event.getEntity() instanceof CraftTribuZombie) {
			CraftTribuZombie zombie = (CraftTribuZombie) event.getEntity();

			Player player = (Player) zombie.getBestAttacker();
			if (player == null && zombie.getTarget() instanceof Player)
				player = (Player) zombie.getTarget();
			if (player != null && player.isOnline()) {
				PlayerStats stats = plugin.getStats(player);
				if (stats != null) {
					stats.addMoney(plugin.getConfig().getInt("Stats.OnZombieKill.Money", 10));
					stats.addPoints(plugin.getConfig().getInt("Stats.OnZombieKill.Points", 15));
					stats.msgStats();
					plugin.getLevel().onWaveStart();
				}
			}

			plugin.getSpawner().despawnZombie(zombie, event.getDrops());
		}
	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}