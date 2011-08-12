package graindcafe.tribu;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CleverMob {
	private MoveTo Destination;
	private LivingEntity ent;
	private Player lastAttacker;

	public CleverMob(LivingEntity entity, MoveTo Destination) {
		ent = entity;
		lastAttacker = null;
		this.Destination = Destination;
		if (this.Destination != null)
			this.Destination.run();
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
