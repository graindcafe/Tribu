/*
 * 
 */
package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.WorldServer;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;

public abstract class EntryBlockState {
	protected int			x, y, z;
	protected WorldServer	world;

	public EntryBlockState(final BlockState bs) throws WrongBlockException {
		this(bs.getBlock().getX(), bs.getBlock().getY(), bs.getBlock().getZ(), ((CraftWorld) bs.getBlock().getWorld()).getHandle());
	}

	private EntryBlockState(final int x, final int y, final int z, final WorldServer world) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
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

	public abstract void restore() throws WrongBlockException, Exception;

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
