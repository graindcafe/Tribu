/*
 * 
 */
package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.WorldServer;

public class EntryBlock {
	protected int			x, y, z;
	protected int			typeId, data;
	protected WorldServer	world;

	public EntryBlock(final int x, final int y, final int z, final int typeId, final int data, final WorldServer world) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.typeId = typeId;
		this.data = data;
		this.world = world;
	}

	public int getData() {
		return data;
	}

	public int getTypeId() {
		return typeId;
	}

	public WorldServer getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean restore() {
		if (world.setRawTypeIdAndData(x, y, z, typeId, data))
		// Maybe the data is modified by something else...
			if (world.getTypeId(x, y, z) == typeId /*&& world.getData(x, y, z) == data*/) return true;
		return false;
	}

	public void setData(final int data) {
		this.data = data;
	}

	public void setTypeId(final int typeId) {
		this.typeId = typeId;
	}

	public void setWorld(final WorldServer world) {
		this.world = world;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public void setZ(final int z) {
		this.z = z;
	}

}
