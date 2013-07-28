package graindcafe.tribu.TribuZombie;

import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.EntityInsentient;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Navigation;
import net.minecraft.server.v1_6_R2.PathEntity;
import net.minecraft.server.v1_6_R2.PathPoint;
import net.minecraft.server.v1_6_R2.PathfinderGoal;

public class PathfinderGoalBreakBlock extends PathfinderGoal {
	protected EntityInsentient creature;
	protected int b;
	protected int c;
	protected int d;
	protected Block block;
	boolean f;
	float g;
	float h;

	private int i;
	private int j = -1;

	public boolean a1() {
		if (!this.creature.positionChanged) {
			return false;
		} else {
			Navigation navigation = this.creature.getNavigation();
			PathEntity pathentity = navigation.e();

			if (pathentity != null && !pathentity.b() && navigation.c()) {
				for (int i = 0; i < Math
						.min(pathentity.e() + 2, pathentity.d()); ++i) {
					PathPoint pathpoint = pathentity.a(i);

					this.b = pathpoint.a;
					this.c = pathpoint.b + 1;
					this.d = pathpoint.c;
					if (this.creature.e(this.b, this.creature.locY, this.d) <= 2.25D) {
						this.block = this.a(this.b, this.c, this.d);
						if (this.block != null) {
							return true;
						}
					}
				}

				this.b = MathHelper.floor(this.creature.locX);
				this.c = MathHelper.floor(this.creature.locY + 1.0D);
				this.d = MathHelper.floor(this.creature.locZ);
				this.block = this.a(this.b, this.c, this.d);
				return this.block != null;
			} else {
				return false;
			}
		}
	}

	public boolean b1() {
		return !this.f;
	}

	public void c1() {
		this.f = false;
		this.g = (float) (this.b + 0.5F - this.creature.locX);
		this.h = (float) (this.d + 0.5F - this.creature.locZ);
	}

	public void e1() {
		float f = (float) (this.b + 0.5F - this.creature.locX);
		float f1 = (float) (this.d + 0.5F - this.creature.locZ);
		float f2 = this.g * f + this.h * f1;

		if (f2 < 0.0F) {
			this.f = true;
		}
	}

	private Block a(int i, int j, int k) {
		int l = this.creature.world.getTypeId(i, j, k);

		return Block.byId[l];
	}

	public PathfinderGoalBreakBlock(EntityInsentient entityinsentient) {
		creature = entityinsentient;
	}

	@Override
	public boolean a() {
		return !a1() ? false : true;
	}

	@Override
	public void c() {
		c1();
		this.i = 0;
	}

	@Override
	public boolean b() {
		double d0 = this.creature.e(this.b, this.c, this.d);

		return this.i <= 240 && d0 < 4.0D;
	}

	@Override
	public void d() {
		super.d();
		this.creature.world.f(this.creature.id, this.b, this.c, this.d, -1);
	}

	@Override
	public void e() {
		e1();
		if (this.creature.aC().nextInt(20) == 0) {
			this.creature.world.triggerEffect(1010, this.b, this.c, this.d, 0);
		}

		++this.i;
		int i = (int) (this.i / 240.0F * 10.0F);

		if (i != this.j) {
			this.creature.world.f(this.creature.id, this.b, this.c, this.d, i);
			this.j = i;
		}

		if (this.i == 240 && this.creature.world.difficulty == 3) {
			this.creature.world.setAir(this.b, this.c, this.d);
			this.creature.world.triggerEffect(1012, this.b, this.c, this.d, 0);
			this.creature.world.triggerEffect(2001, this.b, this.c, this.d,
					this.block.id);
		}
	}
}