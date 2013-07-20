/*
 * 
 */
package graindcafe.tribu.TribuZombie;

import net.minecraft.server.v1_6_R2.GroupDataEntity;

class GroupDataTribuZombie implements GroupDataEntity {

	public boolean			a;
	public boolean			b;

	final EntityTribuZombie	c;

	private GroupDataTribuZombie(final EntityTribuZombie entityzombie, final boolean flag, final boolean flag1) {
		c = entityzombie;
		a = false;
		b = false;
		a = flag;
		b = flag1;
	}

	GroupDataTribuZombie(final EntityTribuZombie entityzombie, final boolean flag, final boolean flag1, final Object emptyclass4) {
		this(entityzombie, flag, flag1);
	}
}
