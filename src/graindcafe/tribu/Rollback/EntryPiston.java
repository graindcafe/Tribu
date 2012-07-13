package graindcafe.tribu.Rollback;

import net.minecraft.server.Block;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntityPiston;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;

public class EntryPiston extends EntryBlockState {
    private int blockId;
    private int blockData;
    private int facing;
    private boolean extending;
    // Don't know what it is but not used even in TileEntityPiston !
    // private boolean e;
    // Not sure what it is but it's not used
    //private float maxProgress;
    private float progress;
	public EntryPiston(BlockState bs) throws WrongBlockException {
		super(bs);
		if(bs instanceof TileEntityPiston)
		{
			this.blockId=((TileEntityPiston) bs).c();
			this.blockData=((TileEntityPiston) bs).k();
			this.extending=((TileEntityPiston) bs).e();
			this.facing=((TileEntityPiston) bs).f();
			//this.maxProgress=((TileEntityPiston) bs).a(1f);
			this.progress=((TileEntityPiston) bs).a(0f);
		}
		else
		{
			throw new WrongBlockException(Block.PISTON_MOVING.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
		}
	}
	private void setPistonData(TileEntityPiston piston)
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setInt("x", this.x);
        nbttagcompound.setInt("y", this.y);
        nbttagcompound.setInt("z", this.z);
        nbttagcompound.setInt("blockId", this.blockId);
        nbttagcompound.setInt("blockData", this.blockData);
        nbttagcompound.setInt("facing", this.facing);
        nbttagcompound.setFloat("progress", this.progress);
        nbttagcompound.setBoolean("extending", this.extending);
		piston.a(nbttagcompound);
	}
	@Override
	public void restore() throws WrongBlockException, Exception {
		Validate.notNull(world,"World is null");
		
		if(world.getTypeId(x,y,z)!=Block.SIGN_POST.id && world.getTypeId(x,y,z)!=Block.WALL_SIGN.id)
			throw new WrongBlockException(Block.SIGN_POST.id,world.getTypeId(x,y,z),x,y,z,world.getWorld());
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

}
