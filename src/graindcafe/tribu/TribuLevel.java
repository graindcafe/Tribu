package graindcafe.tribu;

import graindcafe.tribu.signs.HighscoreSign;
import graindcafe.tribu.signs.ShopSign;
import graindcafe.tribu.signs.SpawnControlSign;
import graindcafe.tribu.signs.TribuSign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class TribuLevel {
	private String name;
	private Location initialSpawn;
	private Location deathSpawn;
	private HashMap<String, Location> zombieSpawns;
	private HashMap<Location, TribuSign> Signs;
	private ArrayList<ShopSign> shopSigns;
	private ArrayList<SpawnControlSign> scSigns;
	private ArrayList<HighscoreSign> highscoreSigns;
	private ArrayList<Location> activeZombieSpawns;
	private Random rnd = new Random();
	private boolean changed; // For deciding whether the level needs saving
								// again

	public TribuLevel(String name, Location spawn) {
		this.zombieSpawns = new HashMap<String, Location>();
		this.activeZombieSpawns = new ArrayList<Location>();
		this.name = name;
		this.initialSpawn = spawn;
		this.deathSpawn = spawn;
		this.changed = false;
		this.highscoreSigns = new ArrayList<HighscoreSign>();
		this.scSigns = new ArrayList<SpawnControlSign>();
		this.shopSigns = new ArrayList<ShopSign>();
		this.Signs = new HashMap<Location, TribuSign>();
	}

	public void activateZombieSpawn(String name) {
		for (String sname : zombieSpawns.keySet()) {
			if (sname.equalsIgnoreCase(name)) {
				Location spawn = zombieSpawns.get(sname);
				if (!this.activeZombieSpawns.contains(spawn))
					this.activeZombieSpawns.add(spawn);
				
				return;
			}
		}
	}

	public boolean addSign(TribuSign sign) {
		if(sign==null)
			return false;
		Signs.put(sign.getLocation(), sign);
		if (sign instanceof SpawnControlSign)
			scSigns.add((SpawnControlSign) sign);
		else if (sign instanceof HighscoreSign)
			highscoreSigns.add((HighscoreSign) sign);
		else if (sign instanceof ShopSign)
			shopSigns.add((ShopSign) sign);
		else return false;
		return true;

	}

	public void addZombieSpawn(Location loc, String name) {
		zombieSpawns.put(name, loc);
		activeZombieSpawns.add(loc);
		changed = true;
	}

	public void desactivateZombieSpawn(String name) {
		for (String sname : zombieSpawns.keySet()) {
			if (sname.equalsIgnoreCase(name)) {
				Location spawn = zombieSpawns.get(sname);
				if (this.activeZombieSpawns.contains(spawn))
					this.activeZombieSpawns.remove(spawn);
				return;
			}
		}
	
	}

	public Location getDeathSpawn() {
		return deathSpawn;
	}

	public Location getInitialSpawn() {
		return initialSpawn;
	}

	public String getName() {
		return name;
	}

	public Location getRandomZombieSpawn() {
		if (activeZombieSpawns.size() == 0) {
			return null;
		}
		return activeZombieSpawns.get(rnd.nextInt(activeZombieSpawns.size()));
	}

	public TribuSign[] getSigns() {
		return Signs.values().toArray(new TribuSign[] {});
	}

	public HashMap<String, Location> getSpawns() {
		return zombieSpawns;
	}

	public Location getZombieSpawn(String name) {
		return zombieSpawns.get(name);
	}

	public boolean hasChanged() {
		return changed;
	}

	public boolean isSpecialSign(Location pos) {
		return Signs.containsKey(pos);
	}

	public void listZombieSpawns(Player player) {
		Set<String> names = zombieSpawns.keySet();
		String nameList = "";
		String separator = "";
		for (String name : names) {
			nameList += separator + name;
			separator = ", ";
		}
		player.sendMessage(String.format(Constants.MessageZombieSpawnList, nameList));
	}

	public void removeSign(Location pos) {
		if (Signs.containsKey(pos))
			removeSign(Signs.get(pos));
	}

	public void removeSign(TribuSign sign) {
		Signs.remove(sign.getLocation());
		if (sign instanceof SpawnControlSign)
			scSigns.remove(sign);
		else if (sign instanceof HighscoreSign)
			highscoreSigns.remove(sign);
		else if (sign instanceof ShopSign)
			shopSigns.remove(sign);
	}

	public void removeZombieSpawn(String name) {
		zombieSpawns.remove(name);
		changed = true;
	}

	public boolean setDeathSpawn(Location loc) {
		if (loc.getWorld() == initialSpawn.getWorld()) {
			deathSpawn = loc;
			changed = true;
			return true;
		}
		return false;
	}

	public boolean setInitialSpawn(Location loc) {
		if (loc.getWorld() == initialSpawn.getWorld()) {
			initialSpawn = loc;
			changed = true;
			return true;
		}
		return false;
	}

	public void setSaved() {
		changed = false;
	}

	public void updateSigns() {
		for (HighscoreSign hs : highscoreSigns) {
			hs.raiseEvent();
		}
	}

	public void updateSigns(BlockRedstoneEvent e) {
		for (SpawnControlSign scs : scSigns) {
			scs.raiseEvent(e);
		}
	}

	public void updateSigns(PlayerInteractEvent e) {
		// quite ugly code
		if (Signs.containsKey(e.getClickedBlock().getLocation())) {
			TribuSign ss = Signs.get(e.getClickedBlock().getLocation());
			if (ss instanceof ShopSign)
				((ShopSign) ss).raiseEvent(e);
		}
	}

}
