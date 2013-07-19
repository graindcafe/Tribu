package graindcafe.tribu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import de.ntcomputer.minecraft.controllablemobs.api.ControllableMob;

public class TribuZombie {
	ControllableMob<Zombie>					innerMob;

	private final HashMap<Player, Double>	playerDamage;
	private Double							maxAccrued, total, maxDamage;
	private Player							bestAttacker;

	public TribuZombie(ControllableMob<Zombie> mob) {
		innerMob = mob;
		playerDamage = new HashMap<Player, Double>();
		maxAccrued = 0d;
		total = 0d;
		maxDamage = 0d;
	}

	public void addAttack(final Player p, final double d) {
		if (maxDamage < d) maxDamage = d;
		Double i;
		if (playerDamage.containsKey(p)) {

			i = playerDamage.get(p);
			i += d;
		} else {
			i = new Double(d);
			playerDamage.put(p, i);
		}
		if (maxAccrued < i) {
			maxAccrued = i;
			bestAttacker = p;
		}
		total += d;
	}

	public Map<Player, Double> getAttackersPercentage() {
		final HashMap<Player, Double> r = new HashMap<Player, Double>();

		for (final Entry<Player, Double> e : playerDamage.entrySet())
			r.put(e.getKey(), (e.getValue()) / total);

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

	public void setTarget(final LivingEntity target) {
		innerMob.getEntity().setTarget(target);
		Logger.getLogger("Minecraft").info("Setting target to " + target);
	}

	public void setTotalAttack(final double t) {
		total = t;
	}

	public ControllableMob<Zombie> getControl() {
		return innerMob;
	}
}
