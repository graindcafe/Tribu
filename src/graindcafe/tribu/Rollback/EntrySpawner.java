package graindcafe.tribu.Rollback;

import net.minecraft.server.Block;
import net.minecraft.server.TileEntityMobSpawner;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;

public class EntrySpawner extends EntryBlockState {
	String	mobName;

	public EntrySpawner(final BlockState bs) throws WrongBlockException {
		super(bs);
		if (bs instanceof TileEntityMobSpawner)
			mobName = ((TileEntityMobSpawner) bs).mobName;
		else
			throw new WrongBlockException(Block.MOB_SPAWNER.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}

	@Override
	public void restore() throws WrongBlockException, Exception {
		Validate.notNull(world, "World is null");
		Validate.notEmpty(mobName, "Mob name is empty");
		if (world.getTypeId(x, y, z) != Block.MOB_SPAWNER.id) throw new WrongBlockException(Block.MOB_SPAWNER.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
		TileEntityMobSpawner spawner = ((TileEntityMobSpawner) world.getTileEntity(x, y, z));
		if (spawner == null) {
			ChunkMemory.debugMsg("Null mob spawner tile entity");
			spawner = new TileEntityMobSpawner();
			spawner.mobName = mobName;
			world.setTileEntity(x, y, z, spawner);
		} else
			spawner.mobName = mobName;
		world.notify(x, y, z);
	}

}
