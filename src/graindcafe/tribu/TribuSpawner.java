package graindcafe.tribu;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

class MoveZombieTo implements Runnable {
	Location target;
	Zombie subject;
	Tribu plugin;
	Thread runner;

	public MoveZombieTo(Tribu plugin, Zombie subject, Location target) {
		this.plugin = plugin;
		this.subject = subject;
		this.target = target;
		runner = new Thread(this);
		// this.run();
	}

	@Override
	public void run() {

		// if(subject.getTarget() == null)
		while (subject != null && !subject.isDead()
				&& subject.getTarget() == null) {

			// plugin.LogInfo("tick");

			// get yaw in radians

			// double yaw = Math.toRadians((double)
			// subject.getLocation().getYaw());

			// plugin.LogInfo("ry"+Math.toRadians(subject.getLocation().getYaw()));
			subject.getLocation().setYaw(180);
			subject.getLocation().setPitch(90);
			subject.teleport(subject.getLocation());
			// subject.teleport(subject.getLocation());
			// divide a force over z and x based on yaw
			// final double force = .5;

			subject.setVelocity(new Vector(0, 0, 0));
			// Vector direction=new Vector(force
			// *(target.getLocation().getX()>subject.getLocation().getX() ? 1 :
			// -1)* Math.cos(-yaw), 0, force *
			// (target.getLocation().getZ()>subject.getLocation().getZ() ? 1 :
			// -1)*Math.sin(yaw));

			// Vector direction =new Vector(force *
			// Math.sin(-yaw),subject.getVelocity().getY(), force *
			// Math.cos(-yaw));
			// subject.setVelocity(direction);

			// subject.teleport(subject.getLocation().add(direction.toLocation(target.getWorld())));
			// plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
			// this, 1);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// subject.setTarget(null);
		}
	}

}

public class TribuSpawner {
	private final Tribu plugin;
	private HashMap<LivingEntity, CleverMob> zombies = new HashMap<LivingEntity, CleverMob>();

	// spawned zombies
	private int totalSpawned;
	// number of zombies to spawn
	private int maxSpawn;
	private boolean finished;
	private int health;
	private boolean justspawned;

	public TribuSpawner(Tribu instance) {
		plugin = instance;
		totalSpawned = 0;
		maxSpawn = 5;
		finished = false;
		health = 10;
	}

	public void CheckZombies() {
		for (LivingEntity e : zombies.keySet()) {
			if (!e.getLocation()
					.getWorld()
					.isChunkLoaded(e.getLocation().getWorld().getChunkAt(e.getLocation())))
				removedZombieCallback(e);
		}
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
			if (zombies.size() == 0 && finished == true) {
				plugin.getServer().broadcastMessage(
						plugin.getLocale("Broadcast.WaveComplete"));
				plugin.getWaveStarter().incrementWave();
				plugin.getWaveStarter().scheduleWave(Constants.TicksBySecond*plugin.getConfiguration().getInt("WaveStart.Delay", 10));
			}
		}
	}

	public void enableFinishCallback() {
		finished = true;
	}

	public CleverMob getCleverMob(LivingEntity mob) {
		return zombies.get(mob);
	}

	public Location getFirstZombieLocation() {
		if (totalSpawned > 0)
			if (zombies.size() > 0)
				return ((LivingEntity) zombies.keySet().toArray()[0])
						.getLocation();
			else {
				plugin.getSpawnTimer().getState();
				plugin.LogSevere("No zombie currently spawned "
						+ zombies.size() + " zombie of " + totalSpawned + "/"
						+ maxSpawn + " spawned  actually alive. The wave is "
						+ (finished ? "finished" : "in progress"));
				return null;
			}
		else
			return null;
	}

	public int getTotal() {
		return totalSpawned;
	}

	public Location GetValidSpawn() {
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

	public boolean justSpawned() {
		return justspawned;
	}

	public void removedZombieCallback(LivingEntity e) {
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
		if (totalSpawned >= maxSpawn || finished == true) {
			return;
		}

		Location pos = plugin.getLevel().getRandomZombieSpawn();
		if (pos == null) {
			return;
		}
		if (!pos.getWorld().isChunkLoaded(pos.getWorld().getChunkAt(pos))) {
			this.CheckZombies();
			
			pos=this.GetValidSpawn();
			if (pos == null)
				return;

		}
		// Surrounded with justspawned so that the zombie isn't
		// removed in the entity spawn listene	r
		justspawned = true;
		Zombie zombie = pos.getWorld().spawn(pos, Zombie.class);
		justspawned = false;

		/*
		 * if(plugin.getPlayersCount() != 0 && zombie.getTarget() ==null) {
		 * boolean targeted=false; List<Entity> targets; int
		 * x=10,y=5,z=10,i=0,c=0;
		 */
		// new MoveZombieTo(plugin,zombie, plugin.getLevel().getInitialSpawn());
		/*
		 * do{ targets=zombie.getNearbyEntities(x, y, z); c=targets.size(); i=0;
		 * while(i<c) {
		 * 
		 * if((plugin.getPlayers().contains((targets.get(i))))){
		 * //zombie.setTarget((LivingEntity)targets.get(i)); targeted=true;
		 * ((Player) targets.get(i)).sendMessage("Tu es ciblé"); new
		 * MoveTo(plugin,zombie,(LivingEntity)targets.get(i)); break; } i++; }
		 * x*=2; y*=2; z*=2; } while(!targeted && plugin.getPlayersCount()!= 0);
		 * 
		 * }
		 */
		zombies.put(zombie, new CleverMob(zombie));
		zombie.setHealth(health);
		totalSpawned++;

	}

}