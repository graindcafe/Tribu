package graindcafe.tribu.Rollback;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import net.minecraft.server.WorldServer;

public abstract class EntryBlockState {
	protected int x,y,z;
	public EntryBlockState(BlockState bs) throws WrongBlockException {
		this(bs.getBlock().getX(),bs.getBlock().getY(),bs.getBlock().getZ(),((CraftWorld) bs.getBlock().getWorld()).getHandle());
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
	public WorldServer getWorld() {
		return world;
	}
	public void setWorld(WorldServer world) {
		this.world = world;
	}
	protected WorldServer world; 
	public abstract void restore() throws WrongBlockException,Exception;
	private EntryBlockState(int x, int y, int z, WorldServer world) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
}
