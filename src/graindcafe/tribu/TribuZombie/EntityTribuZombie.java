/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import graindcafe.tribu.Tribu;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityZombie;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MonsterType;
import net.minecraft.server.PathfinderGoalBreakDoor;
import net.minecraft.server.PathfinderGoalFloat;
import net.minecraft.server.PathfinderGoalHurtByTarget;
import net.minecraft.server.PathfinderGoalLookAtPlayer;
import net.minecraft.server.PathfinderGoalMeleeAttack;
import net.minecraft.server.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.PathfinderGoalRandomLookaround;
import net.minecraft.server.PathfinderGoalRandomStroll;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

public class EntityTribuZombie extends EntityZombie {
	@SuppressWarnings("unused")
	private Tribu plugin;
	private boolean fireResistant;
	public EntityTribuZombie(World world)
	{
		super(world);
		this.bukkitEntity= new CraftTribuZombie(world.getServer(), this);
	}
	private EntityTribuZombie(World world, double d0, double d1, double d2) {
		this(world);
		this.setPosition(d0, d1, d2);
		
	}

	public EntityTribuZombie(Tribu plugin, World world, double d0, double d1, double d2) {
		this(world, d0, d1, d2);
		fireResistant=plugin.config().ZombiesFireResistant;
		this.texture = "/mob/zombie.png";
		this.bb = 0.23F;
		this.damage = 4;
		this.al().b(true);
		this.goalSelector.a(0, new PathfinderGoalFloat(this));
		this.goalSelector.a(1, new PathfinderGoalBreakDoor(this));
		this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, this.bb, false));
		this.goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityVillager.class, this.bb, true));
		String focus = plugin.config().ZombiesFocus;
		if (focus.equals("None"))
			this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, this.bb));
		else if (focus.equals("Nearest") || focus.equals("Random")) {
			if (focus.equals("Random"))
				this.setTarget(((CraftPlayer) plugin.getRandomPlayer()).getHandle());
			this.goalSelector.a(4, new PathfinderGoalMoveTowardsTarget(this, this.bb, 1000f));
		} else if (focus.equals("InitialSpawn") || focus.equals("DeathSpawn")) {
			this.goalSelector.a(4, new PathfinderGoalMoveToLocation(this, focus.equals("DeathSpawn") ? plugin.getLevel().getDeathSpawn() : plugin
					.getLevel().getInitialSpawn(), this.bb, false));
		}
		
		this.goalSelector.a(5, new PathfinderGoalMoveThroughVillage(this, this.bb, false));
		this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, this.bb));
		this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		this.targetSelector.a(2,
				new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, focus.equals("Nearest") ? 100.0F : 16.0F, 0, true));
		this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, 16.0F, 0, false));
		this.plugin = plugin;
	}

	public int getMaxHealth() {
		return 20;
	}

	public int T() {
		return 2;
	}

	protected boolean c_() {
		return true;
	}

	public void e() {
		if (this.world.e() && !this.world.isStatic && !fireResistant) {
			float f = this.b(1.0F);

			if (f > 0.5F && this.world.isChunkLoaded(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
					&& this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				
					this.setOnFire(8);
				
			}
		}

		super.e();
	}

	protected String i() {
		return "mob.zombie";
	}

	protected String j() {
		return "mob.zombiehurt";
	}

	protected String k() {
		return "mob.zombiedeath";
	}

	protected int getLootId() {
		return -1;
	}

	public MonsterType getMonsterType() {
		return MonsterType.UNDEAD;
	}

	// CraftBukkit start - return rare dropped item instead of dropping it
	protected ItemStack b(int i) {
		return null;
	}
	// CraftBukkit end
	
	public static EntityTribuZombie spawn(Tribu plugin, WorldServer world, double x, double y, double z) {
		EntityTribuZombie tz = new EntityTribuZombie(plugin, world, x, y, z);

		if(world.addEntity(tz, SpawnReason.CUSTOM))
			return tz;
		return null;
	}
}
