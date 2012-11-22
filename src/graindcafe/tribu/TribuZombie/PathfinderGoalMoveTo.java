package graindcafe.tribu.TribuZombie;

import java.util.logging.Logger;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathfinderGoal;
import net.minecraft.server.RandomPositionGenerator;
import net.minecraft.server.Vec3D;

import org.bukkit.Location;

/**
 * Based on TrackPlayer
 * @author Graindcafe
 *
 */
public class PathfinderGoalMoveTo extends PathfinderGoal {
	/**
	 * The controlled entity
	 */
	private final EntityCreature	creature;

	/**
	 * Distance
	 */
	private final float				squaredActiveDistance;
	/**
	 * not sure what it is for... 
	 */
	private int						counter;
	/**
	 * Default : 0.02
	 */
	private final float				chance;
	private final double			destX, destY, destZ;
	private double					x, y, z;
	private final float				speed;
	private boolean					doLookAt	= true;

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 */
	public PathfinderGoalMoveTo(final EntityCreature creature, final Location loc, final float speed, final float distance) {
		this(creature, loc, speed, distance, 0.02f);
	}

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 * @param chance (?) default 0.02
	 */
	public PathfinderGoalMoveTo(final EntityCreature creature, final Location loc, final float speed, final float distance, final float chance) {
		trueDebugMsg("init");
		this.creature = creature;
		squaredActiveDistance = distance * distance;
		this.speed = speed;
		this.chance = chance;
		destX = loc.getX();
		destY = loc.getY();
		destZ = loc.getZ();
		// the goal priority
		a(1);
	}

	/**
	 * ? Decide if we should take an action or not
	 *
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do c() before adding it
	 */
	@Override
	public boolean a() {

		// if too far away, do nothing
		debugMsg("testing distance");
		// if we are near enough, don't do anything
		if (creature.f(destX, destY, destZ) < squaredActiveDistance) return false;
		final Vec3D localVec3D = RandomPositionGenerator.a(creature, 16, 7, Vec3D.a(destX, destY, destZ));
		// if generation failed (improbable) do nothing
		debugMsg("testing vec");
		if (localVec3D == null) return false;
		x = localVec3D.c;
		y = localVec3D.d;
		z = localVec3D.e;
		// lookat stuff
		doLookAt = (creature.aB().nextFloat() >= chance);
		return true;
	}

	/**
	 * Decide if we should continue doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do d() before removing it
	 */
	@Override
	public boolean b() {
		// lookAt stuff
		doLookAt = counter > 0;
		// move stuff
		return trueDebugMsg("testing navigation") && (!creature.getNavigation().f()) && //
				((trueDebugMsg("testing distance") && (creature.f(x, y, z) < squaredActiveDistance) && trueDebugMsg("distance ok")) || falseDebugMsg("distance ko"));

	}

	/**
	 * Before adding it
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before adding it
	 */
	@Override
	public void c() {
		debugMsg("gonna add it ! ");

		if (doLookAt) counter = (40 + creature.aB().nextInt(40));
		creature.getNavigation().a(x, y, z, speed);
	}

	/**
	 * Before stop doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before removing it
	 */
	@Override
	public void d() {
		debugMsg("gonna delete it");
	}

	private void debugMsg(final String msg) {
		Logger.getLogger("Minecraft").info(msg);
	}

	/**
	* Do the action
	*/
	@Override
	public void e() {
		if (doLookAt) {
			debugMsg("run + lookAt");
			// cf. PathfinderGoalLookAtPlayer
			creature.getControllerLook().a(x, y, z, 10.0F, creature.bp());
			counter -= 1;
		} else
			debugMsg("run - lookAt");
	}

	private boolean falseDebugMsg(final String msg) {
		debugMsg(msg);
		return false;
	}

	private boolean trueDebugMsg(final String msg) {
		debugMsg(msg);
		return true;
	}
}
