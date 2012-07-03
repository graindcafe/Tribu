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
