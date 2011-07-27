package graindcafe.tribu;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CleverMob {
	private LivingEntity ent;
	private Player lastAttacker;

	public CleverMob(LivingEntity entity) {
		ent = entity;
		lastAttacker = null;
	}

	public LivingEntity getEntity() {
		return ent;
	}

	public Player getLastAttacker() {
		return lastAttacker;
	}

	public void setAttacker(Player player) {
		lastAttacker = player;
	}

}
