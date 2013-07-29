/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import graindcafe.tribu.Tribu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftMonster;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class CraftTribuZombie extends CraftMonster implements Zombie {
	public static CraftEntity spawn(final Tribu plugin, final Location pos)
			throws CannotSpawnException {
		if (!pos.getChunk().isLoaded())
			pos.getChunk().load();

		final EntityTribuZombie tz = EntityTribuZombie.spawn(plugin,
				((CraftWorld) pos.getWorld()).getHandle(), pos.getX(),
				pos.getY(), pos.getZ());
		if (tz == null)
			return null;
		else
			return tz.getBukkitEntity();

	}

	private final HashMap<Player, Double> playerDamage;
	private Double maxAccrued, total, maxDamage;
	private Player bestAttacker;

	public CraftTribuZombie(final CraftServer server,
			final EntityTribuZombie entity) {
		super(server, entity);
		playerDamage = new HashMap<Player, Double>();
		maxAccrued = 0d;
		total = 0d;
		maxDamage = 0d;
	}

	public void addAttack(final Player p, final double damage) {
		if (maxDamage < damage)
			maxDamage = damage;
		Double i;
		if (playerDamage.containsKey(p)) {

			i = playerDamage.remove(p);
			i += damage;
		} else {
			i = new Double(damage);
		}
		playerDamage.put(p, i);
		if (maxAccrued < i) {
			maxAccrued = i;
			bestAttacker = p;
		}
		total += damage;
	}

	public Map<Player, Double> getAttackersPercentage() {
		final HashMap<Player, Double> r = new HashMap<Player, Double>();

		for (final Entry<Player, Double> e : playerDamage.entrySet())
			r.put(e.getKey(), e.getValue() / total);

		return r;
	}

	public Player getBestAttacker() {
		/*
		 * LinkedList<Integer> list = new LinkedList<Integer>();
		 * list.addAll(playerDamage.values()); Collections.sort(list,
		 * Collections.reverseOrder()); Player p=null; Integer max=list.get(0);
		 * for(Entry<Player,Integer> e : playerDamage.entrySet()) {
		 * if(e.getValue().equals(max)) { p=e.getKey(); break; } } return p;
		 */
		return bestAttacker;
	}

	public Player getFirstAttacker() {
		if (playerDamage.isEmpty())
			return null;
		return playerDamage.keySet().iterator().next();
	}

	@Override
	public EntityTribuZombie getHandle() {
		return (EntityTribuZombie) entity;
	}

	public Player getLastAttacker() {
		if (playerDamage.isEmpty())
			return null;
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

	public boolean isBaby() {
		return getHandle().isBaby();
	}

	public boolean isVillager() {
		return getHandle().isVillager();
	}

	public void setBaby(final boolean flag) {
		getHandle().setBaby(flag);
	}

	public void setBestAttacker(final Player p) {
		bestAttacker = p;
	}

	public void setMaxAccruedAttack(final double accrued) {
		maxAccrued = accrued;
	}

	public void setMaxAttack(final double m) {
		maxAccrued = m;
	}

	public void setNoAttacker() {
		playerDamage.clear();
		bestAttacker = null;
		setTarget(null);
	}

	@Override
	public void setTarget(final LivingEntity target) {
		if (target == null)
			getHandle().setTarget(null);
		else
			getHandle().setTarget(((CraftLivingEntity) target).getHandle());
	}

	public void setTotalAttack(final double t) {
		total = t;
	}

	public void setVillager(final boolean flag) {
		getHandle().setVillager(flag);
	}

	@Override
	public String toString() {
		return "CraftTribuZombie";
	}

	public int _INVALID_getLastDamage() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void _INVALID_setLastDamage(int damage) {
		// TODO Auto-generated method stub

	}

	public void _INVALID_damage(int amount) {
		// TODO Auto-generated method stub

	}

	public void _INVALID_damage(int amount, Entity source) {
		// TODO Auto-generated method stub

	}

	public int _INVALID_getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void _INVALID_setHealth(int health) {
		// TODO Auto-generated method stub

	}

	public int _INVALID_getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void _INVALID_setMaxHealth(int health) {
		// TODO Auto-generated method stub

	}
}
