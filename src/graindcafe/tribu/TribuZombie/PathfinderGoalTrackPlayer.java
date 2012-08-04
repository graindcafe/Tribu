package graindcafe.tribu.TribuZombie;

import graindcafe.tribu.Tribu;

import java.util.logging.Logger;

import org.bukkit.craftbukkit.entity.CraftPlayer;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathfinderGoal;
import net.minecraft.server.RandomPositionGenerator;
import net.minecraft.server.Vec3D;

/**
 * Based on LookAtPlayer added MoveTowardsTarget
 * @author Graindcafe
 *
 */

@SuppressWarnings("unused")
public class PathfinderGoalTrackPlayer extends PathfinderGoal {
	/**
	 * The controlled entity
	 */
	private EntityCreature	creature;
	/**
	 * The entity to look at and move towards
	 */
	private Entity			target;
	private Entity			lastTarget;
	/**
	 * Distance
	 */
	private float			squaredActiveDistance;

	/**
	 * Default : 0.02
	 */
	private float			chance;

	private double			x=0, y=0, z=0;
	private float			speed;
	private boolean			doLookAt	= true;
	private boolean	getRandomPlayer=false;
	private Tribu plugin;
	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 */
	public PathfinderGoalTrackPlayer(Tribu plugin,boolean getRandomPlayer,EntityCreature creature,float speed, float distance) {
		this(plugin,getRandomPlayer,creature,speed,distance,0.02f);
	}

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 * @param chance (?) default 0.02
	 */
	public PathfinderGoalTrackPlayer(Tribu plugin,boolean getRandomPlayer,EntityCreature creature,float speed, float distance, float chance) {
		trueDebugMsg("init");
		this.creature = creature;
		this.squaredActiveDistance = distance*distance;
		this.speed=speed;
		this.chance = chance;
		this.plugin=plugin;
		this.getRandomPlayer=getRandomPlayer;
		// the goal priority
		a(1);
	}

	/**
	 * ? Decide if we should take an action or not
	 *
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do c() before adding it
	 */
	public boolean a() {
		// get target
		lastTarget=target;
		this.target=this.creature.at();
		if(this.target==null)
		this.target = (getRandomPlayer ? ((CraftPlayer)plugin.getRandomPlayer()) :((CraftPlayer)plugin.getNearestPlayer(x,y,z))).getHandle();//
		// if no target, do nothing
		debugMsg("testing target");
		if (this.target != null)
		{
		// if too far away, do nothing
		debugMsg("testing distance");
		// if it's near enough
		if (this.target.j(this.creature) < this.squaredActiveDistance) return false;
		Vec3D localVec3D = RandomPositionGenerator.a(this.creature, 16, 7, Vec3D.create(this.target.locX, this.target.locY, this.target.locZ));
		// if generation failed (improbable) do nothing
		debugMsg("testing vec");
		if (localVec3D == null) return false;
		this.x = localVec3D.a;
		this.y = localVec3D.b;
		this.z = localVec3D.c;
		}
		else if(lastTarget==null || x==0 && y==0 && z==0) return false;
		else
			target=lastTarget;
		
		
		return true;
	}

	/**
	 * Decide if we should continue doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do d() before removing it
	 */
	public boolean b() {
		// lookat stuff
		doLookAt= (this.creature.an().nextFloat() >= this.chance);
		// move stuff
		return trueDebugMsg("testing navigation") && (!this.creature.al().e()) && //
				trueDebugMsg("testing alive") && this.target.isAlive() && //
				trueDebugMsg("testing distance") && this.creature.j(this.target) < this.squaredActiveDistance;

	}
	private boolean trueDebugMsg(String msg)
	{
		debugMsg(msg);
		return true;
	}
	private void debugMsg(String msg)
	{
		//if(msg.startsWith("run"))
		//Logger.getLogger("Minecraft").info(msg);
	}
	/**
	 * Before adding it
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before adding it
	 */
	public void c() {
		debugMsg("gonna add it ! "+((CraftPlayer)this.target.getBukkitEntity()).getDisplayName());
		this.creature.al().a(this.x, this.y, this.z, this.speed);
	}

	/**
	 * Before stop doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before removing it
	 */
	public void d() {
		debugMsg("gonna delete it");
		this.target = null;
	}

	/**
	 * Do the action
	 */
	public void e() {
		
		if(doLookAt)
		{
			debugMsg("run + lookAt");
			this.creature.getControllerLook().a(this.target.locX, this.target.locY + this.target.getHeadHeight(), this.target.locZ, 10.0F, this.creature.D());
		}
		else debugMsg("run - lookAt");
	}
}
