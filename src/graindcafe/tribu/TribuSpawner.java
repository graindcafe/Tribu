package graindcafe.tribu;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

class Attack extends MoveTo {

	LivingEntity entity;

	public Attack(Tribu Plugin, Creature Subject, LivingEntity entity) {
		super(Plugin, Subject, entity.getLocation());
		this.entity = entity;
	}

	@Override
	public void run() {
		if (subject.getTarget() == null && !subject.isDead() && !entity.isDead() && !subject.getLocation().getBlock().equals(target.getBlock())) {

			target = entity.getLocation();
			((org.bukkit.craftbukkit.entity.CraftCreature) subject).getHandle().setPathEntity(
					new net.minecraft.server.PathEntity(new net.minecraft.server.PathPoint[] { new net.minecraft.server.PathPoint(target.getBlockX(),
							target.getBlockY(), target.getBlockZ()) }));
			// subject.setTarget(entity);

			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 20);
		}
	}
}

class MoveTo implements Runnable {
	Tribu plugin;
	Creature subject;
	Location target;

	public MoveTo(Tribu Plugin, Creature Subject) {
		plugin = Plugin;
		subject = Subject;
	}

	public MoveTo(Tribu Plugin, Creature Subject, Location Target) {
		plugin = Plugin;
		subject = Subject;
		target = Target;

	}

	@Override
	public void run() {
		if (!subject.isDead() && !subject.getLocation().getBlock().equals(target.getBlock())) {

			((org.bukkit.craftbukkit.entity.CraftCreature) subject).getHandle().setPathEntity(
					new net.minecraft.server.PathEntity(new net.minecraft.server.PathPoint[] { new net.minecraft.server.PathPoint(target.getBlockX(),
							target.getBlockY(), target.getBlockZ()) }));
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 20);
		}
	}
}

public class TribuSpawner {
	private boolean finished;
	private int health;

	private boolean justspawned;
	// number of zombies to spawn
	private int maxSpawn;
	private final Tribu plugin;
	private boolean starting;
	// spawned zombies
	private int totalSpawned;
	private HashMap<LivingEntity, CleverMob> zombies = new HashMap<LivingEntity, CleverMob>();

	public TribuSpawner(Tribu instance) {
		plugin = instance;
		totalSpawned = 0;
		maxSpawn = 5;
		finished = false;
		starting = true;
		health = 10;
	}

	// check if a zombie has been despawned (too far, killed but not caught by
	// event,...)
	public void checkZombies() {
		Stack<LivingEntity> toDelete = new Stack<LivingEntity>();
		for (LivingEntity e : zombies.keySet())
			if (e.isDead())
				toDelete.push(e);
		if (finished && !toDelete.isEmpty())
			finished = false;
		while (!toDelete.isEmpty())
			removedZombieCallback(toDelete.pop());

	}

	public void clearZombies() {
		for (LivingEntity zombie : zombies.keySet()) {
			zombie.remove();
		}
		resetTotal();
		zombies.clear();
	}

	public void despawnZombie(LivingEntity zombie, List<ItemStack> drops) {
		if (zombies.containsKey(zombie)) {
			zombies.remove(zombie);
			drops.clear();
			tryStartNextWave();
		} else {
			plugin.LogWarning("Unreferenced zombie despawned");
		}
	}

	public void finishCallback() {
		finished = true;
	}

	public CleverMob getCleverMob(LivingEntity mob) {
		return zombies.get(mob);
	}

	// Debug command
	public Location getFirstZombieLocation() {
		if (totalSpawned > 0)
			if (!zombies.isEmpty()) {
				plugin.LogInfo("Health : " + ((LivingEntity) zombies.keySet().toArray()[0]).getHealth());
				plugin.LogInfo("LastDamage : " + ((LivingEntity) zombies.keySet().toArray()[0]).getLastDamage());
				plugin.LogInfo("isDead : " + ((LivingEntity) zombies.keySet().toArray()[0]).isDead());
				return ((LivingEntity) zombies.keySet().toArray()[0]).getLocation();
			} else {
				plugin.getSpawnTimer().getState();
				plugin.LogSevere("No zombie currently spawned " + zombies.size() + " zombie of " + totalSpawned + "/" + maxSpawn
						+ " spawned  actually alive. The wave is " + (finished ? "finished" : "in progress"));
				return null;
			}
		else
			return null;
	}

	public int getTotal() {
		return totalSpawned;
	}

	// get the first spawn that is loaded
	public Location getValidSpawn() {
		for (Location curPos : plugin.getLevel().getSpawns().values()) {

			if (curPos.getWorld().isChunkLoaded(curPos.getWorld().getChunkAt(curPos))) {
				return curPos;
			}
		}
		plugin.LogInfo(plugin.getLocale("Warning.AllSpawnsCurrentlyUnloaded"));
		return null;

	}

	public boolean haveZombieToSpawn() {
		return totalSpawned != maxSpawn;
	}

	public boolean isSpawned(LivingEntity ent) {
		return zombies.containsKey(ent);
	}

	public boolean isWaveCompleted() {
		return !haveZombieToSpawn() && zombies.isEmpty();
	}

	public boolean justSpawned() {
		return justspawned;
	}

	public void removedZombieCallback(LivingEntity e) {
		e.damage(10000000);
		zombies.remove(e);
		totalSpawned--;
	}

	public void resetTotal() {
		totalSpawned = 0;
		finished = false;
	}

	public void setHealth(int value) {
		health = value;
	}

	public void setMaxSpawn(int count) {
		maxSpawn = count;
	}

	public void SpawnZombie() {
		if (totalSpawned >= maxSpawn || finished) {
			return;
		}

		Location pos = plugin.getLevel().getRandomZombieSpawn();
		if (pos == null) {
			return;
		}
		if (!pos.getWorld().isChunkLoaded(pos.getWorld().getChunkAt(pos))) {
			this.checkZombies();

			pos = this.getValidSpawn();
			if (pos == null)
				return;

		}
		// Surrounded with justspawned so that the zombie isn't
		// removed in the entity spawn listener
		justspawned = true;
		Zombie zombie = pos.getWorld().spawn(pos, Zombie.class);
		justspawned = false;
		MoveTo dest = null;
		String focus = plugin.getConfiguration().getString("Zombies.Focus", "None");
		if (focus.equalsIgnoreCase("Nearest")) {
			if (plugin.getPlayersCount() != 0 && zombie.getTarget() == null) {
				List<org.bukkit.entity.Entity> targets;
				int x = 10, y = 5, z = 10, i = 0, c = 0;

				do {
					targets = zombie.getNearbyEntities(x, y, z);
					c = targets.size();
					i = 0;
					while (i < c) {
						if (targets.get(i) instanceof Player && (plugin.getPlayers().contains((targets.get(i))))) {
							dest = new Attack(plugin, (Creature) zombie, (LivingEntity) targets.get(i));
							break;
						}
						i++;
					}
					x += 10;
					y += 10;
					z += 10;
				} while (dest == null && plugin.getPlayersCount() != 0);
			}
		} else if (focus.equalsIgnoreCase("Random"))
			dest = new Attack(plugin, zombie, plugin.getRandomPlayer());
		else if (focus.equalsIgnoreCase("DeathSpawn"))
			dest = new MoveTo(plugin, zombie, plugin.getLevel().getDeathSpawn());
		else if (focus.equalsIgnoreCase("InitialSpawn"))
			dest = new MoveTo(plugin, zombie, plugin.getLevel().getInitialSpawn());
		else {
			if (!focus.equalsIgnoreCase("None"))
				plugin.LogWarning(String.format(plugin.getLocale("Warning.UnknownFocus"), plugin.getConfiguration().getString("Zombies.Focus")));
		}
		zombies.put(zombie, new CleverMob(zombie, dest));
		zombie.setHealth(health);
		totalSpawned++;

	}

	public void startingCallback() {
		starting = false;
	}

	// Try to start the next wave if possible and return if it's starting
	public boolean tryStartNextWave() {
		if (zombies.isEmpty() && finished && !starting) {
			starting = true;
			plugin.getServer().broadcastMessage(plugin.getLocale("Broadcast.WaveComplete"));
			plugin.getWaveStarter().incrementWave();
			plugin.getWaveStarter().scheduleWave(Constants.TicksBySecond * plugin.getConfiguration().getInt("WaveStart.Delay", 10));
		}
		return starting;
	}

}
