/*
 * 
 */
package graindcafe.tribu.TribuZombie;

import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.PathfinderGoal;
import net.minecraft.server.v1_6_R2.RandomPositionGenerator;
import net.minecraft.server.v1_6_R2.Vec3D;

import org.bukkit.Location;

/**
 * Based on TrackPlayer
 * 
 * @author Graindcafe
 * 
 */
public class PathfinderGoalMoveTo extends PathfinderGoal {
	/**
	 * The controlled entity
	 */
	private final EntityCreature creature;

	/**
	 * Distance
	 */
	private final float squaredActiveDistance;
	/**
	 * Default : 0.02
	 */
	private final double destX, destY, destZ;
	private final float speed;
	double c, d;

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 */
	public PathfinderGoalMoveTo(final EntityCreature creature,
			final Location loc, final float speed, final float distance) {
		this.creature = creature;
		squaredActiveDistance = distance * distance;
		this.speed = speed;
		destX = loc.getX();
		destY = loc.getY();
		destZ = loc.getZ();
		// the goal priority
		a(1);
	}

	/**
	 * ? Decide if we should take an action or not
	 * 
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be
	 * executed, then do c() before adding it
	 */
	@Override
	public boolean a() {
		return (creature.f(destX, destY, destZ) > squaredActiveDistance);
	}

	/**
	 * Decide if we should continue doing this if b() false and
	 * (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then
	 * do d() before removing it
	 */
	@Override
	public boolean b() {
		return !creature.getNavigation().g();
	}

	/**
	 * Before adding it if a() true and
	 * (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then
	 * do this before adding it
	 */
	@Override
	public void c() {
		// navigation.a() <- move method

		if (this.creature.e(destX, destY, destZ) > 256.0D) {
			Vec3D vec3d = RandomPositionGenerator.a(
					this.creature,
					14,
					3,
					this.creature.world.getVec3DPool().create(destX + 0.5D,
							destY, destZ + 0.5D));

			if (vec3d != null) {
				this.creature.getNavigation().a(vec3d.c, vec3d.d, vec3d.e,
						speed);
			}
		} else {
			this.creature.getNavigation().a(destX + 0.5D, destY, destZ + 0.5D,
					speed);
		}
	}
}
