package graindcafe.tribu.TribuZombie;

import net.minecraft.server.EntityCreature;

import org.bukkit.entity.Player;

public class PathfinderGoalTrackPlayer extends PathfinderGoalMoveToLocation{
	Player tracked;
	/**
	 * Make the creature tracks a player 
	 * @param entitycreature
	 * @param p
	 * @param speed
	 * @param can break door ?
	 */
	public PathfinderGoalTrackPlayer(EntityCreature entitycreature, Player p, float speed, boolean canBreakDoor) {
		super(entitycreature, p.getLocation(), speed, canBreakDoor);
		tracked=p;
	}
	/* (non-Javadoc)
	 * @see graindcafe.tribu.TribuZombie.PathfinderGoalMoveToLocation#a()
	 */
	public boolean a() {
		if(tracked==null)
			return false;
		
		this.loc=tracked.getLocation();
		return super.a();
	}
}
