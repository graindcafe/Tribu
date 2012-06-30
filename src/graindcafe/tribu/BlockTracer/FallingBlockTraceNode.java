package graindcafe.tribu.BlockTracer;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class FallingBlockTraceNode extends BlockTraceNode {

	Block fallingBlock;
	//Location lastLoc;
	public FallingBlockTraceNode(Block element,BlockTraceNode node) {
		super(node.blockId,node.blockData,node.blockLocation,node.previous);
		fallingBlock=element;
		//lastLoc=fallingBlock.getLocation();
	}
	public boolean stillFalling()
	{
		return isFalling(blockLocation);
	}
	public static boolean isFalling(Location blockLoc)
	{
		return BlockTraceNode.isSolid(blockLoc.subtract(0,1,0).getBlock()) || (BlockTraceNode.isSubjectedToPhysical(blockLoc.subtract(0,1,0).getBlock()) && isFalling(blockLoc.subtract(0,1,0)));
	}
	public boolean isValid()
	{
		return fallingBlock.getLocation().equals(blockLocation);
	}
	/*public boolean hasMoved()
	{
		boolean r=!lastLoc.equals(fallingBlock.getLocation());
		lastLoc=fallingBlock.getLocation();
		return r;
	}*/
	public void update()
	{
		this.blockLocation=fallingBlock.getLocation();
	}
}
