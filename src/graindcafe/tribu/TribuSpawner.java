package graindcafe.tribu;

import graindcafe.tribu.Configuration.Constants;
import graindcafe.tribu.TribuZombie.CraftTribuZombie;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;


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
	private LinkedList<CraftTribuZombie> zombies;
	
	public TribuSpawner(Tribu instance) {
		plugin = instance;
		totalSpawned = 0;
		maxSpawn = 5;
		finished = false;
		starting = true;
		health = 10;
		zombies = new LinkedList<CraftTribuZombie>();
	}

	// check if a zombie has been despawned (too far, killed but not caught by
	// event,...)
	public void checkZombies() {
		Stack<LivingEntity> toDelete = new Stack<LivingEntity>();
		for (CraftTribuZombie e : zombies)
			if (e.isDead())
				toDelete.push(e);
		if (finished && !toDelete.isEmpty())
			finished = false;
		while (!toDelete.isEmpty())
			removedZombieCallback(toDelete.pop());

	}

	public void clearZombies() {
		for (CraftTribuZombie zombie : zombies) {
			zombie.remove();
		}
		resetTotal();
		zombies.clear();
	}

	public void despawnZombie(CraftTribuZombie zombie, List<ItemStack> drops) {
		if (zombies.contains(zombie)) {
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

	// Debug command
	public Location getFirstZombieLocation() {
		if (totalSpawned > 0)
			if (!zombies.isEmpty()) {
				plugin.LogInfo("Health : " + zombies.get(0).getHealth());
				plugin.LogInfo("LastDamage : " + zombies.get(0).getLastDamage());
				plugin.LogInfo("isDead : " +  zombies.get(0).isDead());
				return  zombies.get(0).getLocation();
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
	public int getMaxSpawn()
	{
		return this.maxSpawn;
	}
	public boolean haveZombieToSpawn() {
		return totalSpawned != maxSpawn;
	}

	public boolean isSpawned(LivingEntity ent) {
		return zombies.contains(ent);
	}

	public boolean isWaveCompleted() {
		return !haveZombieToSpawn() && zombies.isEmpty();
	}

	public boolean justSpawned() {
		return justspawned;
	}

	public void removedZombieCallback(LivingEntity e) {
		e.damage(Integer.MAX_VALUE);
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
		CraftTribuZombie zombie = (CraftTribuZombie) CraftTribuZombie.spawn(plugin,pos);
		justspawned = false;

		zombies.add(zombie);
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
			plugin.messagePlayers(plugin.getLocale("Broadcast.WaveComplete"));
			plugin.getWaveStarter().incrementWave();
			plugin.getWaveStarter().scheduleWave(Constants.TicksBySecond * plugin.config().WaveStartDelay);
		}
		return starting;
	}

}
