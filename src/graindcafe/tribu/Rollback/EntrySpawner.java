/*
 * 
 */
package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.TileEntityMobSpawner;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;

public class EntrySpawner extends EntryBlockState {
	String mobName;

	public EntrySpawner(final BlockState bs) throws WrongBlockException {
		super(bs);
		if (bs instanceof TileEntityMobSpawner)
			mobName = ((TileEntityMobSpawner) bs).a().getMobName();
		else
			throw new WrongBlockException(Block.MOB_SPAWNER.id, getWorld()
					.getTypeId(x, y, z), x, y, z, getWorld().getWorld());
	}

	@Override
	public void restore() throws WrongBlockException, Exception {
		Validate.notNull(getWorld(), "getWorld() is null");
		Validate.notEmpty(mobName, "Mob name is empty");
		if (getWorld().getTypeId(x, y, z) != Block.MOB_SPAWNER.id)
			throw new WrongBlockException(Block.MOB_SPAWNER.id, getWorld()
					.getTypeId(x, y, z), x, y, z, getWorld().getWorld());
		TileEntityMobSpawner spawner = ((TileEntityMobSpawner) getWorld()
				.getTileEntity(x, y, z));
		if (spawner == null) {
			ChunkMemory.debugMsg("Null mob spawner tile entity");
			spawner = new TileEntityMobSpawner();
			spawner.a().a(mobName);
			getWorld().setTileEntity(x, y, z, spawner);
		} else
			spawner.a().a(mobName);
		getWorld().notify(x, y, z);
	}
}
