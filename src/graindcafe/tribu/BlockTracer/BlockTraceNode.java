package graindcafe.tribu.BlockTracer;

import graindcafe.tribu.NotFoundException;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.Door;
import org.bukkit.material.Bed;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockTraceNode implements Cloneable {

	public static boolean hasOtherBlock(Block b) {
		return hasOtherBlock(b.getTypeId());
	}

	public static boolean hasOtherBlock(int id) {
		switch (id) {
		case 64:
		case 71:
		case 26:
			return true;
		default:
			return false;
		}
	}

	public Location getOtherBlock() throws NotFoundException {
		LinkedList<Location> r = new LinkedList<Location>();
		// Door
		if (this.blockId == 71 || this.blockId == 64) {
			// if(isSolid(this.blockLocation.getBlock().getRelative(BlockFace.DOWN)))
			if (((Door) this.blockLocation.getBlock().getState().getData()).isTopHalf())
				r.add(this.blockLocation.clone().add(0, -1, 0));
			else
				return (this.blockLocation.clone().add(0, 1, 0));
		}
		// Bed
		else if (this.blockId == 26) {
			Bed bed = ((Bed) this.blockLocation.getBlock().getState().getData());
			byte isHead = (byte) ((bed.isHeadOfBed()) ? 1 : -1);

			if (bed.getFacing().equals(BlockFace.EAST))
				return (this.blockLocation.clone().add(1 * isHead, 0, 0));
			else if (bed.getFacing().equals(BlockFace.WEST))
				return (this.blockLocation.clone().add(-1 * isHead, 0, 0));
			else if (bed.getFacing().equals(BlockFace.NORTH))
				return (this.blockLocation.clone().add(0, 0, 1 * isHead));
			else if (bed.getFacing().equals(BlockFace.SOUTH))
				return (this.blockLocation.clone().add(0, 0, -1 * isHead));
		}
		throw new NotFoundException();
		
	}

	public static boolean isBound(Block b) {
		return isBound(b.getTypeId());
	}

	public static boolean isBound(int id) {
		switch (id) {
		case 6:
			// case 12:
			// case 13:
		case 18:
		case 26:
		case 27:
		case 28:
		case 30:
		case 31:
		case 32:
		case 37:
		case 38:
		case 39:
		case 40:
		case 50:
		case 55:
		case 59:
		case 63:
		case 64:
		case 65:
		case 66:
		case 68:
		case 71:
		case 72:
		case 75:
		case 76:
		case 77:
		case 83:
		case 85:
		case 90:
		case 92:
		case 93:
		case 96:
			return true;
		default:
			return false;
		}
	}

	public static boolean isSolid(Block b) {
		return isSolid(b.getTypeId());
	}

	public static boolean isSolid(int id) {
		switch (id) {
		case 0: // AIR
		case 8: // Water
		case 9: // Water
		case 10: // Lava
		case 11: // Lava
		case 51: // Fire
			return false;
		default:
			return true;
		}
	}

	public static boolean isSubjectedToPhysical(Block b) {
		return isSubjectedToPhysical(b.getTypeId());
	}

	public static boolean isSubjectedToPhysical(int id) {
		return id == 12 || id == 13;
	}

	public static boolean LocationBlockEquals(Location loc1, Location loc2) {
		// loc1 = loc 2 = null || loc1.world,x,y,z == loc2.world,x,y,z
		return (loc1 == null && loc2 == null) || (loc1 != null && loc2 != null) && loc1.getWorld().equals(loc2.getWorld())
				&& loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
	}

	private byte blockData;
	private byte blockId;

	private Location blockLocation;

	private BlockTraceNode previous = null;

	public BlockTraceNode(Block element) {
		this((byte) element.getTypeId(), element.getData(), element.getLocation());
	}

	public BlockTraceNode(Block element, BlockTraceNode previous) {
		this(element);
		this.previous = previous;
	}

	public BlockTraceNode(byte id, byte data, Location loc) {
		this.blockId = id;
		this.blockData = data;
		this.blockLocation = loc;
	}

	public BlockTraceNode(byte id, byte data, Location loc, BlockTraceNode previous) {
		this(id, data, loc);
		this.previous = previous;
	}

	public BlockTraceNode(Location loc) {
		this(loc.getBlock());
	}

	public BlockTraceNode(Location loc, BlockTraceNode previous) {
		this(loc.getBlock(), previous);
	}

	@Override
	public BlockTraceNode clone() {
		return new BlockTraceNode(blockId, blockData, blockLocation, previous);
	}

	public BlockTraceNode clone(BlockTraceNode previous) {
		return new BlockTraceNode(blockId, blockData, blockLocation, previous);
	}

	public Location getLocation() {
		return blockLocation;
	}

	public BlockTraceNode getPrevious() {
		return previous;
	}

	public Material getType() {
		return Material.getMaterial(blockId);
	}

	public int getTypeId() {
		return blockId;
	}
	public byte getData()
	{
		return blockData;
	}

	public boolean isBound() {
		return isBound(blockId);
	}

	public boolean isSolid() {
		return isSolid(blockId);
	}

	public boolean isSubjectedToPhysical() {
		return isSubjectedToPhysical(blockId);
	}

	public boolean hasOtherBlock() {
		return hasOtherBlock(blockId);
	}

	public boolean LocationBlockEquals(BlockTraceNode node) {
		return LocationBlockEquals(blockLocation, node.getLocation());
	}

	public boolean LocationBlockEquals(Location loc) {
		return LocationBlockEquals(blockLocation, loc);
	}

	public void reverse() {
		blockLocation.getBlock().setTypeIdAndData(blockId, blockData, false);
	}

	public void setLocation(Location loc) {
		blockLocation = loc;
	}

	public void setPrevious(BlockTraceNode previous) {
		this.previous = previous;
	}
	public boolean equals(Object obj)
	{
		if(obj instanceof BlockTraceNode)
		{
			
			BlockTraceNode btn=(BlockTraceNode) obj;
			return btn.getTypeId()==this.blockId && btn.getData() == blockData && btn.getLocation().equals(this.blockLocation);
		}
		else if(obj instanceof Block)
		{
			
			Block b=(Block) obj;
			return b.getTypeId()==this.blockId && b.getData() == blockData && b.getLocation().equals(this.blockLocation);
		}
		
			
		return false;
	}
}
