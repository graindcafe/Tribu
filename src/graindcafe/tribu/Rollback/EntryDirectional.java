/*
 * 
 */
package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.Block;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

public class EntryDirectional extends EntryBlockState {
	int	facing;

	public EntryDirectional(final BlockState bs) throws WrongBlockException {
		super(bs);
		if (bs.getData() instanceof Directional)
			facing = ((Directional) bs.getData()).getFacing().ordinal();
		else
			throw new WrongBlockException(Block.DISPENSER.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}

	@Override
	public void restore() throws WrongBlockException, Exception {
		Validate.notNull(world, "World is null");
		final MaterialData materialData = world.getWorld().getBlockAt(x, y, z).getState().getData();
		if (materialData instanceof Directional) ((Directional) materialData).setFacingDirection(BlockFace.values()[facing]);

		world.notify(x, y, z);

	}

}
