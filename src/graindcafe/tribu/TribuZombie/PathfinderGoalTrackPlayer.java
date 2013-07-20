/*
 * 
 */
package graindcafe.tribu.TribuZombie;

import graindcafe.tribu.Tribu;
import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.EntityCreature;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Navigation;
import net.minecraft.server.v1_6_R2.PathfinderGoal;

import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;

/**
 * Based on FollowOwner
 * @author Graindcafe
 *
 */

public class PathfinderGoalTrackPlayer extends PathfinderGoal {
	/**
	 * The controlled entity
	 */
	private final EntityCreature						creature;
	/**
	 * The entity to look at and move towards
	 */
	private Entity										target;
	/**
	 * Distance
	 */
	private final float									squaredActiveDistance;

	private double										x				= 0;
	private double										y				= 0;
	private double										z				= 0;
	private boolean										getRandomPlayer	= false;
	private final Tribu									plugin;
	private final Navigation							nav;
	private final net.minecraft.server.v1_6_R2.World	a;
	private boolean										i;
	private int											h;
	private double										rushCoef		= 1d;

	/**
	 * 
	 * @param creature
	 * @param targetClass
	 * @param distance
	 */
	public PathfinderGoalTrackPlayer(final Tribu plugin, final boolean getRandomPlayer, final EntityCreature creature, final double rushCoef, final float distance) {
		this.creature = creature;
		squaredActiveDistance = distance * distance;
		this.plugin = plugin;
		this.getRandomPlayer = getRandomPlayer;
		this.nav = creature.getNavigation();
		this.a = creature.world;
		this.rushCoef = rushCoef;
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
		EntityLiving entityliving = null;
		/*if (target != null) {
			System.err.println("We shouldn't start doing this");
			return false;
		}*/
		// get target
		// target = creature.az();
		x = creature.locX;
		y = creature.locY;
		z = creature.locZ;
		if (creature.target == null)
			entityliving = (getRandomPlayer ? ((CraftPlayer) plugin.getRandomPlayer()) : ((CraftPlayer) plugin.getNearestPlayer(x, y, z))).getHandle();
		else if (creature instanceof EntityLiving) entityliving = (EntityLiving) creature.target;
		if (entityliving == null) {
			return false;
		} else if (this.creature.e(entityliving) < squaredActiveDistance) {
			return false;
		} else {
			this.target = entityliving;
			creature.setTarget(entityliving);
			return true;
		}
	}

	/**
	 * Decide if we should continue doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do d() before removing it
	 * nav.g() : return PathEntity == null || PathEntity.isDone() 
	 */
	@Override
	public boolean b() {
		return !this.nav.g() && this.creature.e(this.target) > (squaredActiveDistance);
	}

	/**
	 * Before adding it
	 * if a() true and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before adding it
	 */
	@Override
	public void c() {
		this.h = 0;
		this.i = this.creature.getNavigation().a();
		this.creature.getNavigation().a(false);
	}

	/**
	 * Before stop doing this
	 * if b() false and (?:goalSelector.a(PathfinderGoalSelectorItem)) should be executed, then do this before removing it
	 */
	@Override
	public void d() {
		this.target = null;
		this.nav.h();
		this.creature.getNavigation().a(this.i);
	}

	boolean	tooFarAway	= false;
	int		predX		= 0;
	int		predZ		= 0;

	/**
	 * Do the action
	 */
	@Override
	public void e() {

		if (this.target == null) {
			System.err.println("Target is null");
			return;
		}
		this.creature.getControllerLook().a(this.target, 10.0F, this.creature.bp());
		if (--this.h <= 0) {
			this.h = 10;
			if (!this.nav.a(this.target, rushCoef)) {
				if (!this.creature.bH()) {
					if (this.creature.e(this.target) >= 1400.0D) {
						if (tooFarAway) {
							predX += this.target.locX > creature.locX ? 1 : -1;
							predZ += this.target.locZ > creature.locZ ? 1 : -1;
						} else {
							predX = 0;
							predZ = 0;
						}
						int i = MathHelper.floor((this.target.locX + creature.locX + predX) / 2);
						int j = MathHelper.floor((this.target.locZ + creature.locZ + predZ) / 2);
						int k = MathHelper.floor(this.target.boundingBox.b);
						tooFarAway = true;
						for (int l = 0; l <= 24; ++l) {
							for (int i1 = 0; i1 <= 24; ++i1) {
								if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.a.w(i + l, k - 1, j + i1) && !this.a.u(i + l, k, j + i1) && !this.a.u(i + l, k + 1, j + i1)) {
									this.creature.setPositionRotation(i + l + 0.5F, k, j + i1 + 0.5F, this.creature.yaw, this.creature.pitch);
									this.nav.h();
									return;
								}
							}
						}
					} else
						tooFarAway = false;
				}
			}
		}

	}
}
