package graindcafe.tribu.TribuZombie;

import java.util.logging.Logger;

import org.bukkit.Location;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathfinderGoal;
import net.minecraft.server.RandomPositionGenerator;
import net.minecraft.server.Vec3D;

/**
 * Based on TrackPlayer
 * @author Graindcafe
 *
 */
public class PathfinderGoalMoveTo extends PathfinderGoal {
	/**
	 * The controlled entity
	 */
	private EntityCreature	creature;
	
	/**
	 * Distance
	 */
	private float			squaredActiveDistance;
	/**
	 * not sure what it is for... 
	 */
	private int				counter;
	/**
	 * Default : 0.02
	 */
	private float			chance;
	private double destX,destY,destZ;
	private double			x, y, z;
	private float			speed;
	private boolean			doLookAt	= true;
	
	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 */
	public PathfinderGoalMoveTo(EntityCreature creature,Location loc, float speed, float distance) {
		this(creature,loc,speed,distance,0.02f);
	}

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 * @param chance (?) default 0.02
	 */
	public PathfinderGoalMoveTo(EntityCreature creature,Location loc, float speed,float distance, float chance) {
		trueDebugMsg("init");
		this.creature = creature;
		this.squaredActiveDistance = distance*distance;
		this.speed=speed;
		this.chance = chance;
		destX=loc.getX();
		destY=loc.getY();
		destZ=loc.getZ();
		// the goal priority
		a(1);
	}

	/**
	 * ? Decide if we should take an action or not
	 *
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do c() before adding it
	 */
	public boolean a() {
		
		// if too far away, do nothing
		debugMsg("testing distance");
		// if we are near enough, don't do anything
		if (this.creature.f(destX,destY,destZ) < this.squaredActiveDistance) return false;
		Vec3D localVec3D = RandomPositionGenerator.a(this.creature, 16, 7, Vec3D.create(destX,destY,destZ));
		// if generation failed (improbable) do nothing
		debugMsg("testing vec");
		if (localVec3D == null) return false;
		this.x = localVec3D.a;
		this.y = localVec3D.b;
		this.z = localVec3D.c;
		// lookat stuff
		doLookAt= (this.creature.an().nextFloat() >= this.chance);
		return true;
	}

	/**
	 * Decide if we should continue doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do d() before removing it
	 */
	public boolean b() {
		//lookAt stuff 
		this.doLookAt=this.counter > 0 ;
		// move stuff
		return trueDebugMsg("testing navigation") && (!this.creature.al().e()) && //
				((trueDebugMsg("testing distance") && (this.creature.f(x,y,z) < this.squaredActiveDistance) &&
				trueDebugMsg("distance ok"))  || falseDebugMsg("distance ko"));

	}

	/**
	 * Before adding it
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before adding it
	 */
	public void c() {
		debugMsg("gonna add it ! ");
		
		if (doLookAt) this.counter = (40 + this.creature.an().nextInt(40));
		this.creature.al().a(this.x, this.y, this.z, this.speed);
	}

	/**
	 * Before stop doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before removing it
	 */
	public void d() {
		debugMsg("gonna delete it");
	}
	private boolean trueDebugMsg(String msg)
	{
		debugMsg(msg);
		return true;
	}private boolean falseDebugMsg(String msg)
	{
		debugMsg(msg);
		return false;
	}
	private void debugMsg(String msg)
	{
		Logger.getLogger("Minecraft").info(msg);
	}
	/**
	 * Do the action
	 */
	public void e() {
		if(doLookAt)
		{
			debugMsg("run + lookAt");
			this.creature.getControllerLook().a(x,y,z, 10.0F, this.creature.D());
			this.counter -= 1;
		}else debugMsg("run - lookAt");
	}
}
