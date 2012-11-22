/*******************************************************************************
 * Copyright or © or Copr. Quentin Godron (2011)
 * 
 * cafe.en.grain@gmail.com
 * 
 * This software is a computer program whose purpose is to create zombie 
 * survival games on Bukkit's server. 
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/

/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import java.util.Calendar;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.Configuration.FocusType;
import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityZombie;
import net.minecraft.server.EnumMonsterType;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PathfinderGoalBreakDoor;
import net.minecraft.server.PathfinderGoalFloat;
import net.minecraft.server.PathfinderGoalHurtByTarget;
import net.minecraft.server.PathfinderGoalLookAtPlayer;
import net.minecraft.server.PathfinderGoalMeleeAttack;
import net.minecraft.server.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.PathfinderGoalRandomLookaround;
import net.minecraft.server.PathfinderGoalRandomStroll;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityTribuZombie extends EntityMonster {
	public static EntityTribuZombie spawn(final Tribu plugin, final WorldServer world, final double x, final double y, final double z) throws CannotSpawnException {
		final EntityTribuZombie tz = new EntityTribuZombie(plugin, world, x, y, z);
		synchronized (tz) {
			if (world.addEntity(tz, CreatureSpawnEvent.SpawnReason.CUSTOM))
				return tz;
			else
				throw new CannotSpawnException();
		}

	}

	@SuppressWarnings("unused")
	private Tribu	plugin;
	private int		maxHealth	= 20;
	private boolean	sunProof	= true;
	protected int   d = 0;
	private int damage = 4;

	public EntityTribuZombie(final Tribu plugin, final World world, final double x, final double y, final double z) {
		this(world, x, y, z);
		// fireProof = plugin.config().ZombiesFireResistant;
		sunProof = plugin.config().ZombiesFireProof || plugin.config().ZombiesSunProof;
		texture = "/mob/zombie.png";
		// from 0.85 to 1.18
		final float normalSpeedCoef = ((plugin.config().ZombiesSpeedRandom) ? .1f + (random.nextFloat() / 3f) : .25f) + (plugin.config().ZombiesSpeedBase - .25f);
		// from 1 to 1.77
		// .85 * 1.18 = 1 and we'll have normalSpeed * rushSpeed * speed, if
		// normalSpeed=.85 * rushSpeed=1 = 1
		final float rushSpeedCoef = ((1 / normalSpeedCoef) * (((plugin.config().ZombiesSpeedRandom) ? (random.nextFloat() / 2f) : .25f) + (plugin.config().ZombiesSpeedRush - .25f)));
		// Speed: 0.23 normal speed
		bG = 0.23F * normalSpeedCoef;
		damage = plugin.getWaveStarter().getCurrentDamage();
		maxHealth = plugin.getWaveStarter().getCurrentHealth();
		// Can break wooden door ?
		getNavigation().b(true);
		// useful : this.a(width,length) (float,float)

		goalSelector.a(0, new PathfinderGoalFloat(this));
		goalSelector.a(1, new PathfinderGoalBreakDoor(this));
		goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, bG * rushSpeedCoef, false));
		goalSelector.a(3, new PathfinderGoalMeleeAttack(this, EntityVillager.class, bG * rushSpeedCoef, true));

		
		final FocusType focus = plugin.config().ZombiesFocus;

		if (focus.equals(FocusType.None))
			goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, bG));
		else if (focus.equals(FocusType.NearestPlayer) || focus.equals(FocusType.RandomPlayer))
			goalSelector.a(4, new PathfinderGoalTrackPlayer(plugin, focus.equals(FocusType.RandomPlayer), this, bG * rushSpeedCoef, 8f));
		// this.goalSelector.a(5, new PathfinderGoalTrackPlayer(this, plugin,
		// focus.equals(FocusType.RandomPlayer), this.bb, true));
		else if (focus.equals(FocusType.InitialSpawn) || focus.equals(FocusType.DeathSpawn))
			goalSelector.a(4, new PathfinderGoalMoveTo(this, focus.equals(FocusType.InitialSpawn) ? plugin.getLevel().getInitialSpawn() : plugin.getLevel().getDeathSpawn(), //
					bG * normalSpeedCoef, 4f));

		goalSelector.a(5, new PathfinderGoalMoveThroughVillage(this, bG, false));
		goalSelector.a(6, new PathfinderGoalRandomStroll(this, bG));
		goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
		targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 16f, 0, true));
		targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, 16.0F, 0, false));
		this.plugin = plugin;
	}

	public EntityTribuZombie(final World world) {
		super(world);

		bukkitEntity = new CraftTribuZombie(world.getServer(), this);

	}

	private EntityTribuZombie(final World world, final double x, final double y, final double z) {
		this(world);
		setPosition(x, y, z);
	}
	


    public float bB() {
        return super.bB() * (this.isBaby() ? 1.5F : 1.0F);
    }

    protected void a() {
        super.a();
        this.getDataWatcher().a(12, Byte.valueOf((byte) 0));
        this.getDataWatcher().a(13, Byte.valueOf((byte) 0));
        this.getDataWatcher().a(14, Byte.valueOf((byte) 0));
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int aW() {
        int i = super.aW() + 2;

        if (i > 20) {
            i = 20;
        }

        return i;
    }

    protected boolean be() {
        return true;
    }

    public boolean isBaby() {
        return this.getDataWatcher().getByte(12) == 1;
    }

    public void setBaby(boolean flag) {
        this.getDataWatcher().watch(12, Byte.valueOf((byte) (flag ? 1 : 0))); // CraftBukkit - added flag
    }

    public boolean isVillager() {
        return this.getDataWatcher().getByte(13) == 1;
    }

    public void setVillager(boolean flag) {
        this.getDataWatcher().watch(13, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public void c() {
        if (!sunProof && this.world.u() && !this.world.isStatic && !this.isBaby()) {
            float f = this.c(1.0F);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.k(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))) {
                boolean flag = true;
                ItemStack itemstack = this.getEquipment(4);

                if (itemstack != null) {
                    if (itemstack.f()) {
                        itemstack.setData(itemstack.i() + this.random.nextInt(2));
                        if (itemstack.i() >= itemstack.k()) {
                            this.a(itemstack);
                            this.setEquipment(4, (ItemStack) null);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    // CraftBukkit start
                    EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), 8);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        this.setOnFire(event.getDuration());
                    }
                    // CraftBukkit end
                }
            }
        }

        super.c();
    }

    public void j_() {
        if (!this.world.isStatic && this.o()) {
            int i = this.q();

            this.d -= i;
            if (this.d <= 0) {
                this.p();
            }
        }

        super.j_();
    }

    public int c(Entity entity) {
        ItemStack itemstack = this.bD();
        int i = damage;

        if (itemstack != null) {
            i += itemstack.a((Entity) this);
        }

        return i;
    }

    protected String aY() {
        return "mob.zombie.say";
    }

    protected String aZ() {
        return "mob.zombie.hurt";
    }

    protected String ba() {
        return "mob.zombie.death";
    }

    protected void a(int i, int j, int k, int l) {
        this.makeSound("mob.zombie.step", 0.15F, 1.0F);
    }

    protected int getLootId() {
        return -1;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    // CraftBukkit start - return rare dropped item instead of dropping it
    /*protected ItemStack l(int i) {
        return null;
    }*/
    // CraftBukkit end

    protected void bE() {
        super.bE();
        if (this.random.nextFloat() < (this.world.difficulty == 3 ? 0.05F : 0.01F)) {
            int i = this.random.nextInt(3);

            if (i == 0) {
                this.setEquipment(0, new ItemStack(Item.IRON_SWORD));
            } else {
                this.setEquipment(0, new ItemStack(Item.IRON_SPADE));
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.isBaby()) {
            nbttagcompound.setBoolean("IsBaby", true);
        }

        if (this.isVillager()) {
            nbttagcompound.setBoolean("IsVillager", true);
        }

        nbttagcompound.setInt("ConversionTime", this.o() ? this.d : -1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.getBoolean("IsBaby")) {
            this.setBaby(true);
        }

        if (nbttagcompound.getBoolean("IsVillager")) {
            this.setVillager(true);
        }

        if (nbttagcompound.hasKey("ConversionTime") && nbttagcompound.getInt("ConversionTime") > -1) {
            this.a(nbttagcompound.getInt("ConversionTime"));
        }
    }

    public void a(EntityLiving entityliving) {
        super.a(entityliving);
        if (this.world.difficulty >= 2 && entityliving instanceof EntityVillager) {
            if (this.world.difficulty == 2 && this.random.nextBoolean()) {
                return;
            }

            EntityZombie entityzombie = new EntityZombie(this.world);

            entityzombie.k(entityliving);
            this.world.kill(entityliving);
            entityzombie.bG();
            entityzombie.setVillager(true);
            if (entityliving.isBaby()) {
                entityzombie.setBaby(true);
            }

            this.world.addEntity(entityzombie);
            this.world.a((EntityHuman) null, 1016, (int) this.locX, (int) this.locY, (int) this.locZ, 0);
        }
    }

    public void bG() {
        this.canPickUpLoot = this.random.nextFloat() < as[this.world.difficulty];
        if (this.world.random.nextFloat() < 0.05F) {
            this.setVillager(true);
        }

        this.bE();
        this.bF();
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.T();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Block.JACK_O_LANTERN : Block.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.bT();

        if (itemstack != null && itemstack.getItem() == Item.GOLDEN_APPLE && itemstack.getData() == 0 && this.isVillager() && this.hasEffect(MobEffectList.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
            }

            if (itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
            }

            if (!this.world.isStatic) {
                this.a(this.random.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    protected void a(int i) {
        this.d = i;
        this.getDataWatcher().watch(14, Byte.valueOf((byte) 1));
        this.o(MobEffectList.WEAKNESS.id);
        this.addEffect(new MobEffect(MobEffectList.INCREASE_DAMAGE.id, i, Math.min(this.world.difficulty - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    public boolean o() {
        return this.getDataWatcher().getByte(14) == 1;
    }

    protected void p() {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.k(this);
        entityvillager.bG();
        entityvillager.q();
        if (this.isBaby()) {
            entityvillager.setAge(-24000);
        }

        this.world.kill(this);
        this.world.addEntity(entityvillager);
        entityvillager.addEffect(new MobEffect(MobEffectList.CONFUSION.id, 200, 0));
        this.world.a((EntityHuman) null, 1017, (int) this.locX, (int) this.locY, (int) this.locZ, 0);
    }
    protected int q() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;

            for (int k = (int) this.locX - 4; k < (int) this.locX + 4 && j < 14; ++k) {
                for (int l = (int) this.locY - 4; l < (int) this.locY + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ - 4; i1 < (int) this.locZ + 4 && j < 14; ++i1) {
                        int j1 = this.world.getTypeId(k, l, i1);

                        if (j1 == Block.IRON_FENCE.id || j1 == Block.BED.id) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }
    
}
