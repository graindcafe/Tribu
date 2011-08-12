package graindcafe.tribu;

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
	private ArrayList<Location> activeZombieSpawns;
	private boolean changed; // For deciding whether the level needs saving
								// again
	private Location deathSpawn;
	private Location initialSpawn;
	private String name;
	private Random rnd = new Random();
	private HashMap<Location, TribuSign> Signs;
	private HashMap<String, Location> zombieSpawns;

	public TribuLevel(String name, Location spawn) {
		this.zombieSpawns = new HashMap<String, Location>();
		this.activeZombieSpawns = new ArrayList<Location>();
		this.name = name;
		this.initialSpawn = spawn;
		this.deathSpawn = spawn;
		this.changed = false;
		/*
		 * this.highscoreSigns = new ArrayList<HighscoreSign>(); this.scSigns =
		 * new ArrayList<SpawnControlSign>(); this.shopSigns = new
		 * ArrayList<ShopSign>(); this.tSigns = new ArrayList<TollSign>();
		 */
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
		if (sign == null)
			return false;
		Signs.put(sign.getLocation(), sign);
		/*
		 * if (sign instanceof SpawnControlSign) scSigns.add((SpawnControlSign)
		 * sign); else if (sign instanceof HighscoreSign)
		 * highscoreSigns.add((HighscoreSign) sign); else if (sign instanceof
		 * ShopSign) shopSigns.add((ShopSign) sign); else if (sign instanceof
		 * TollSign) tSigns.add((TollSign) sign); else return false;
		 */
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

	public void initSigns() {
		for (TribuSign s : Signs.values())
			s.init();
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

	public void onClick(PlayerInteractEvent e) {
		for (TribuSign s : Signs.values())
			if (s.isUsedEvent(e))
				s.raiseEvent(e);
	}

	public void onRedstoneChange(BlockRedstoneEvent e) {
		for (TribuSign s : Signs.values())
			if (s.isUsedEvent(e))
				s.raiseEvent(e);
		/*
		 * for (SpawnControlSign scs : scSigns) { scs.raiseEvent(e); } for
		 * (TollSign ts : tSigns) { ts.raiseEvent(e); }
		 */

	}

	public void onSignClicked(PlayerInteractEvent e) {
		if (Signs.containsKey(e.getClickedBlock().getLocation())) {
			TribuSign ss = Signs.get(e.getClickedBlock().getLocation());
			if (ss.isUsedEvent(e))
				ss.raiseEvent(e);

			/*
			 * if (ss instanceof ShopSign) ((ShopSign) ss).raiseEvent(e);
			 */
		}
	}

	public void onWaveStart() {
		/*
		 * for (HighscoreSign hs : highscoreSigns) { hs.raiseEvent(); }
		 */
		for (TribuSign s : Signs.values())
			if (s.isUsedEvent(null))
				s.raiseEvent(null);
	}

	public boolean removeSign(Location pos) {
		if (Signs.containsKey(pos)) {
			removeSign(Signs.get(pos));
			return true;
		}
		return false;
	}

	public void removeSign(TribuSign sign) {
		Signs.remove(sign.getLocation());
		/*
		 * if (sign instanceof SpawnControlSign) scSigns.remove(sign); else if
		 * (sign instanceof HighscoreSign) highscoreSigns.remove(sign); else if
		 * (sign instanceof ShopSign) shopSigns.remove(sign);
		 */
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

}
