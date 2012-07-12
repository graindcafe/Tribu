package graindcafe.tribu.Rollback;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import net.minecraft.server.Block;
import net.minecraft.server.TileEntitySign;

public class EntrySign extends EntryBlockState {
	String[] lines;

	public EntrySign(BlockState sign) throws WrongBlockException {
		super(sign);
		if(sign instanceof Sign)
			this.lines = ((Sign) sign).getLines().clone();
		else
			new WrongBlockException(Block.SIGN_POST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}



	@Override
	public void restore() throws WrongBlockException {
		if (world == null)
			ChunkMemory.debugMsg("Null world");
		if (lines == null)
			ChunkMemory.debugMsg("Null lines");
		if(world.getTypeId(x,y,z)!=Block.SIGN_POST.id && world.getTypeId(x,y,z)!=Block.WALL_SIGN.id)
			throw new WrongBlockException(Block.SIGN_POST.id,world.getTypeId(x,y,z),x,y,z,world.getWorld());
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
