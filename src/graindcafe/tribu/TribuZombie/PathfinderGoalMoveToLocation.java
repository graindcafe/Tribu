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

	/**
	 * The controlled creature
	 */
	private EntityCreature a;
	/**
	 * Speed of the creature
	 */
	private float b;
	/**
	 * The path to use
	 */
	private PathEntity c;
	/**
	 * Can break wooden door ?
	 */
	private boolean e;
	@SuppressWarnings("rawtypes")
	private List f = new ArrayList();
	/**
	 * The location to go
	 */
	private Location loc;
	
	/**
	 * Move the entity to the location  
	 * @param entitycreature
	 * @param loc Location
	 * @param speed Speed of the entity
	 * @param flag Can break wooden door
	 */
	public PathfinderGoalMoveToLocation(EntityCreature entitycreature,Location loc,  float speed, boolean flag) {
		this.a = entitycreature;
		this.b = speed;
		this.e = flag;
		this.loc = loc;
		this.a(1);
	}
	
	/** 
	 * Does the goal succeed ?
	 * @see net.minecraft.server.PathfinderGoal#a()
	 * 
	 */
	public boolean a() {
		this.f();
		if (this.e && this.a.world.e()) {
			return false;
		} else {
			// Get the flag
			boolean flag = this.a.al().b();
			// Don't go thru wooden door
			this.a.al().b(false);
			// Try to go directly to the location
			this.c = this.a.al().a(this.loc.getX(), this.loc.getY(), this.loc.getZ());
			// Set back the flag
			this.a.al().b(flag);
			// if it's possible, then the goal succeed
			if (this.c != null) {
				return true;
			} else {
				// creature, x/z gap, y gap, location
				Vec3D vec3d = RandomPositionGenerator.a(this.a, 2, 10, Vec3D.create(this.loc.getX(), this.loc.getY(), this.loc.getZ()));

				if (vec3d == null) {
					return false;
				} else {
					// Don't go thru wooden door
					this.a.al().b(false);
					// Try to get to the new location
					this.c = this.a.al().a(vec3d.a, vec3d.b, vec3d.c);
					// Set back the flag
					this.a.al().b(flag);
					// If there is a path
					return this.c != null;
				}
			}

		}
	}

	/**
	 * Is the goal useless ?
	 * @see net.minecraft.server.PathfinderGoal#b()
	 */
	public boolean b() {
		// Is the path ended ?
		if (this.a.al().e()) {
			// False : we gonna give a new path
			return false;
		} else {
			float f = this.a.width + 10.0F;
			// this.a.e : square distance of the entity from the point
			return this.a.e(this.loc.getX(), this.loc.getY(), this.loc.getZ()) > (double) (f * f);
		}
	}

	/** 
	 * Try to move to use the path
	 * @see net.minecraft.server.PathfinderGoal#c()
	 */
	public void c() {
		this.a.al().a(this.c, this.b);
	}

	/**
	 * If there is more than 15 elements in f remove the first one
	 */
	private void f() {
		if (this.f.size() > 15) {
			this.f.remove(0);
		}
	}
}
