/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import graindcafe.tribu.Tribu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class CraftTribuZombie extends CraftZombie implements Zombie {
	public static Entity spawn(final Tribu plugin, final Location pos) throws CannotSpawnException {
		if (!pos.getChunk().isLoaded()) pos.getChunk().load();

		final EntityTribuZombie tz = EntityTribuZombie.spawn(plugin, ((CraftWorld) pos.getWorld()).getHandle(), pos.getX(), pos.getY(), pos.getZ());
		if (tz == null)
			return null;
		else
			return tz.getBukkitEntity();
	}

	private final HashMap<Player, Integer>	playerDamage;
	private Integer							maxAccrued, total, maxDamage;
	private Player							bestAttacker;

	public CraftTribuZombie(final CraftServer server, final EntityTribuZombie entity) {
		super(server, entity);
		playerDamage = new HashMap<Player, Integer>();
		maxAccrued = 0;
		total = 0;
		maxDamage = 0;
	}

	public void addAttack(final Player p, final int damage) {
		if (maxDamage < damage) maxDamage = damage;
		Integer i;
		if (playerDamage.containsKey(p)) {

			i = playerDamage.get(p);
			i += damage;
		} else {
			i = new Integer(damage);
			playerDamage.put(p, i);
		}
		if (maxAccrued < i) {
			maxAccrued = i;
			bestAttacker = p;
		}
		total += damage;
	}

	public Map<Player, Float> getAttackersPercentage() {
		final HashMap<Player, Float> r = new HashMap<Player, Float>();

		for (final Entry<Player, Integer> e : playerDamage.entrySet())
			r.put(e.getKey(), ((float) e.getValue()) / ((float) total));

		return r;
	}

	public Player getBestAttacker() {
		/*
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.addAll(playerDamage.values());
		Collections.sort(list, Collections.reverseOrder());
		Player p=null;
		Integer max=list.get(0);
		for(Entry<Player,Integer> e : playerDamage.entrySet())
		{
			if(e.getValue().equals(max))
			{
				p=e.getKey();
				break;
			}
		}
		return p;*/
		return bestAttacker;
	}

	public Player getFirstAttacker() {
		if (playerDamage.isEmpty()) return null;
		return playerDamage.keySet().iterator().next();
	}

	@Override
	public EntityTribuZombie getHandle() {
		return (EntityTribuZombie) entity;
	}

	public Player getLastAttacker() {
		if (playerDamage.isEmpty()) return null;
		final Iterator<Player> i = playerDamage.keySet().iterator();

		final Player beforeLast = i.next();
		Player p;
		do
			p = i.next();
		while (p != null);
		return beforeLast;

	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	public void setBestAttacker(final Player p) {
		bestAttacker = p;
	}

	public void setMaxAccruedAttack(final int accrued) {
		maxAccrued = accrued;
	}

	public void setMaxAttack(final int m) {
		maxAccrued = m;
	}

	public void setNoAttacker() {
		playerDamage.clear();
		bestAttacker = null;
		setTarget(null);
	}

	@Override
	public void setTarget(final LivingEntity target) {
		Logger.getLogger("Minecraft").info("Fuck you, you and your " + target);
	}

	public void setTotalAttack(final int t) {
		total = t;
	}

	@Override
	public String toString() {
		return "CraftTribuZombie";
	}
}
