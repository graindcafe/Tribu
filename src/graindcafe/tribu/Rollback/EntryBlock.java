package graindcafe.tribu.Rollback;

import net.minecraft.server.WorldServer;

public class EntryBlock {
	protected int x, y, z;
	protected int typeId, data;
	protected WorldServer world;

	public EntryBlock(int x, int y, int z, int typeId, int data, WorldServer world) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.typeId = typeId;
		this.data = data;
		this.world = world;
	}

	public boolean restore() {
		if (world.setRawTypeIdAndData(x, y, z, typeId, data))
			if (world.getTypeId(x, y, z) == typeId && world.getData(x, y, z) == data)
				return true;
		return false;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public WorldServer getWorld() {
		return world;
	}

	public void setWorld(WorldServer world) {
		this.world = world;
	}

}
