package graindcafe.tribu.TribuZombie; 

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathfinderGoal;
import net.minecraft.server.RandomPositionGenerator;
import net.minecraft.server.Vec3D;

public class PathfinderGoalMoveToLocation extends PathfinderGoal {

	private EntityCreature a;
	private float b;
	private PathEntity c;
	private boolean e;
	@SuppressWarnings("rawtypes")
	private List f = new ArrayList();
	private Location loc;

	public PathfinderGoalMoveToLocation(EntityCreature entitycreature,Location loc,  float f, boolean flag) {
		this.a = entitycreature;
		this.b = f;
		this.e = flag;
		this.loc = loc;
		this.a(1);
	}

	public boolean a() {
		this.f();
		if (this.e && this.a.world.e()) {
			return false;
		} else {

			boolean flag = this.a.al().b();

			this.a.al().b(false);
			this.c = this.a.al().a(this.loc.getX(), this.loc.getY(), this.loc.getZ());
			this.a.al().b(flag);
			if (this.c != null) {
				return true;
			} else {
				Vec3D vec3d = RandomPositionGenerator.a(this.a, 10, 7, Vec3D.create(this.loc.getX(), this.loc.getY(), this.loc.getZ()));

				if (vec3d == null) {
					return false;
				} else {
					this.a.al().b(false);
					this.c = this.a.al().a(vec3d.a, vec3d.b, vec3d.c);
					this.a.al().b(flag);
					return this.c != null;
				}
			}

		}
	}

	public boolean b() {
		if (this.a.al().e()) {
			return false;
		} else {
			float f = this.a.width + 4.0F;
			return this.a.e(this.loc.getX(), this.loc.getY(), this.loc.getZ()) > (double) (f * f);
		}
	}

	public void c() {
		this.a.al().a(this.c, this.b);
	}

	private void f() {
		if (this.f.size() > 15) {
			this.f.remove(0);
		}
	}
}