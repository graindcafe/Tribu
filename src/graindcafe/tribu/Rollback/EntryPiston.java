package graindcafe.tribu.Rollback;

import net.minecraft.server.Block;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntityPiston;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;

public class EntryPiston extends EntryBlockState {
	private int		blockId;
	private int		blockData;
	private int		facing;
	private boolean	extending;
	// Don't know what it is but not used even in TileEntityPiston !
	// private boolean e;
	// Not sure what it is but it's not used
	// private float maxProgress;
	private float	progress;

	public EntryPiston(final BlockState bs) throws WrongBlockException {
		super(bs);
		if (bs instanceof TileEntityPiston) {
			blockId = ((TileEntityPiston) bs).a();
			blockData = ((TileEntityPiston) bs).n();
			extending = ((TileEntityPiston) bs).b();
			facing = ((TileEntityPiston) bs).c();
			// this.maxProgress=((TileEntityPiston) bs).a(1f);
			progress = ((TileEntityPiston) bs).a(0f);
		} else
			throw new WrongBlockException(Block.PISTON_MOVING.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}

	@Override
	public void restore() throws WrongBlockException, Exception {
		Validate.notNull(world, "World is null");

		if (world.getTypeId(x, y, z) != Block.SIGN_POST.id && world.getTypeId(x, y, z) != Block.WALL_SIGN.id) throw new WrongBlockException(Block.SIGN_POST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
		TileEntityPiston piston = ((TileEntityPiston) world.getTileEntity(x, y, z));
		if (piston == null) {
			ChunkMemory.debugMsg("Null sign tile entity");
			piston = new TileEntityPiston();
			setPistonData(piston);
			world.setTileEntity(x, y, z, piston);
		} else
			setPistonData(piston);

		world.notify(x, y, z);
	}

	private void setPistonData(final TileEntityPiston piston) {
		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInt("x", x);
		nbttagcompound.setInt("y", y);
		nbttagcompound.setInt("z", z);
		nbttagcompound.setInt("blockId", blockId);
		nbttagcompound.setInt("blockData", blockData);
		nbttagcompound.setInt("facing", facing);
		nbttagcompound.setFloat("progress", progress);
		nbttagcompound.setBoolean("extending", extending);
		piston.a(nbttagcompound);
	}

}
