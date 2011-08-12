package graindcafe.tribu;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockTraceNode implements Cloneable {
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
		case 0:
		case 9:
		case 10:
		case 11:
		case 12:
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
	private byte elementData;
	private byte elementId;

	private Location elementLocation;

	private BlockTraceNode precedent = null;

	public BlockTraceNode(Block element) {
		this((byte) element.getTypeId(), element.getData(), element.getLocation());
	}

	public BlockTraceNode(Block element, BlockTraceNode precedent) {
		this(element);
		this.precedent = precedent;
	}

	public BlockTraceNode(byte id, byte data, Location loc) {
		this.elementId = id;
		this.elementData = data;
		this.elementLocation = loc;
	}

	public BlockTraceNode(byte id, byte data, Location loc, BlockTraceNode precedent) {
		this(id, data, loc);
		this.precedent = precedent;
	}

	public BlockTraceNode(Location loc) {
		this(loc.getBlock());
	}

	public BlockTraceNode(Location loc, BlockTraceNode precedent) {
		this(loc.getBlock(), precedent);
	}

	@Override
	public BlockTraceNode clone() {
		return new BlockTraceNode(elementId, elementData, elementLocation, precedent);
	}

	public BlockTraceNode clone(BlockTraceNode precedent) {
		return new BlockTraceNode(elementId, elementData, elementLocation, precedent);
	}

	public Location getLocation() {
		return elementLocation;
	}

	public BlockTraceNode getPrecedent() {
		return precedent;
	}

	public Material getType() {
		return Material.getMaterial(elementId);
	}

	public boolean isBound() {
		return isBound(elementId);
	}

	public boolean isSolid() {
		return isSolid(elementId);
	}

	public boolean isSubjectedToPhysical() {
		return isSubjectedToPhysical(elementId);
	}

	public boolean LocationBlockEquals(BlockTraceNode node) {
		return LocationBlockEquals(elementLocation, node.getLocation());
	}

	public boolean LocationBlockEquals(Location loc) {
		return LocationBlockEquals(elementLocation, loc);
	}

	public void reverse() {
		elementLocation.getBlock().setTypeIdAndData(elementId, elementData, false);
	}

	public void setLocation(Location loc) {
		elementLocation = loc;
	}

	public void setPrecedent(BlockTraceNode precedent) {
		this.precedent = precedent;
	}
}
