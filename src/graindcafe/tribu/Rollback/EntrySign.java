/*
 * 
 */
package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.TileEntitySign;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class EntrySign extends EntryBlockState {
	String[]	lines;

	public EntrySign(final BlockState sign) throws WrongBlockException {
		super(sign);
		if (sign instanceof Sign)
			lines = ((Sign) sign).getLines().clone();
		else
			new WrongBlockException(Block.SIGN_POST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}

	@Override
	public void restore() throws WrongBlockException {
		Validate.notNull(world, "World is null");
		Validate.notNull(lines, "Lines are null");
		if (world.getTypeId(x, y, z) != Block.SIGN_POST.id && world.getTypeId(x, y, z) != Block.WALL_SIGN.id) throw new WrongBlockException(Block.SIGN_POST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
		TileEntitySign sign = ((TileEntitySign) world.getTileEntity(x, y, z));
		if (sign == null) {
			ChunkMemory.debugMsg("Null sign tile entity");
			sign = new TileEntitySign();
			sign.lines = lines;
			world.setTileEntity(x, y, z, sign);
		} else
			sign.lines = lines;
		world.notify(x, y, z);
	}

}
