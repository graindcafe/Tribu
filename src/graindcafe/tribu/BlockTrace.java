package graindcafe.tribu;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

/**
 * All blocks pushed in this stack can be reversed You need to push broken
 * blocks and replaced (by placing a block over it) blocks Example : You place
 * wood on water -> push water . You break the wood -> push wood. The reversing
 * will first place the wood then place the water
 * 
 * @author Graindcafe
 * 
 */
public class BlockTrace {
	Logger log;
	private BlockTraceNode top = null;

	public BlockTrace(Logger log) {
		this.log = log;
	}

	public void clear() {
		top = null;
	}

	public boolean contains(Location loc) {
		BlockTraceNode theTop = top;
		while (theTop != null)
			if (theTop.getLocation().equals(loc))
				return true;
			else
				theTop = theTop.getPrecedent();
		return false;
	}

	public BlockTraceNode getNodeAt(Location loc) {
		BlockTraceNode theHead = top;
		while (theHead != null)
			if (theHead.LocationBlockEquals(loc))
				return theHead;
			else
				theHead = theHead.getPrecedent();
		return top;
	}

	public boolean isEmpty() {
		return top == null;
	}

	private void log(String s) {
		//log.info(s);
	}

	public BlockTraceNode peek() {
		return top;
	}

	public BlockTraceNode pop() {
		BlockTraceNode r = top;
		top = top.getPrecedent();
		return r;
	}

	public void push(Block b, boolean isRemoved) {
		push(new BlockTraceNode(b), isRemoved);
	}

	/*
	 * private void pushBefore(BlockTraceNode nodeToPlaceBefore, BlockTraceNode
	 * nodeToPlaceAfter) {
	 * 
	 * nodeToPlaceBefore.setPrecedent(nodeToPlaceAfter.getPrecedent());
	 * nodeToPlaceAfter.setPrecedent(nodeToPlaceBefore); }
	 * 
	 * private void pushBefore(Block blockToPlaceBefore, BlockTraceNode
	 * nodeToPlaceAfter) { pushBefore(new BlockTraceNode(blockToPlaceBefore),
	 * nodeToPlaceAfter); }
	 */

	public void push(BlockTraceNode node, boolean isRemoved) {

		if (isRemoved) {
			log("Block removed");
			log("Checking for bound blocks");
			// if this block is a block that may hold something, place first
			// items
			// its holds
			if (!node.isBound())
				for (BlockFace bf : BlockFace.values())
					if (BlockTraceNode.isBound(node.getLocation().getBlock().getRelative(bf))) {
						log("Detected bound block" + node.getLocation().getBlock().getRelative(bf).getType());
						push(node.getLocation().getBlock().getRelative(bf), true);
					}

			log("Checking for fallings blocks");
			node.setPrecedent(top);
			BlockTraceNode precedent = node;
			Location loc = node.getLocation().clone();
			Queue<BlockTraceNode> fallingBlocks = new LinkedList<BlockTraceNode>();
			loc = loc.add(0, 1, 0);
			// if is falling calc where the clock fall
			while (BlockTraceNode.isSubjectedToPhysical(loc.getBlock())) {
				log("Detected falling block : " + loc.getBlock().getType());
				fallingBlocks.add(new BlockTraceNode(loc));
				loc = loc.add(0, 1, 0);
			}

			// if some blocks may fall by removing this one
			if (!fallingBlocks.isEmpty()) {
				loc = node.getLocation().clone();
				// calc where they will be
				do
					loc = loc.subtract(0, 1, 0);
				while (!BlockTraceNode.isSolid(loc.getBlock()));
				log("Platform will be " + loc.getBlock().getType() + ", falling for " + (node.getLocation().getBlockY() - loc.getBlockY())
						+ " blocks");
				while (!fallingBlocks.isEmpty()) {
					loc.add(0, 1, 0);
					// = the block to replace
					fallingBlocks.peek().setPrecedent(precedent);
					// = the block to remove
					precedent = new BlockTraceNode(loc, fallingBlocks.poll());
				}
			}
			top = precedent;

		} else {
			log("Block placed");
			// If this block may fall, then, place it as it will be removed
			// before
			// its platform
			log("Check if the block is falling");
			if (BlockTraceNode.isSubjectedToPhysical(node.getLocation().getBlock())) {
				log("Falling block");
				do
					node.setLocation(node.getLocation().subtract(0, 1, 0));
				while (!BlockTraceNode.isSolid(node.getLocation().getBlock()));
				node.setLocation(node.getLocation().add(0, 1, 0));

			}
			/*
			 * if (node.isSubjectedToPhysical()) {
			 * 
			 * log("Block : " + node.getLocation().getBlock().getType() +
			 * " is subjected to physical"); log("at : " +
			 * node.getLocation().getBlockX() + "," +
			 * node.getLocation().getBlockY() + "," +
			 * node.getLocation().getBlockZ());
			 * 
			 * Location loc = node.getLocation().clone(); // if is falling calc
			 * where the clock fall do loc=loc.subtract(0, 1, 0); while
			 * (!BlockTraceNode.isSolid(loc.getBlock()));
			 * 
			 * 
			 * node.setLocation(loc); loc = loc.clone(); // while
			 * (BlockTraceNode.isSubjectedToPhysical(loc.getBlock()))
			 * loc=loc.subtract(0, 1, 0);
			 * 
			 * log("Platform : " + loc.getBlock().getType()); log("at : " +
			 * loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
			 * BlockTraceNode b = getNodeAt(loc); if (b != top) {
			 * log("is in the trace  "); } else { log("is not in the trace  ");
			 * } pushBefore(node,b); }
			 */

			node.setPrecedent(top);
			top = node;

		}
		log("Push block " + node.getType());

		if (top.getPrecedent() == node && node.getPrecedent() == top)
			log("WARNING top precedent = node && node.precendent() == top. YOUR SERVER WILL NEVER STOP WHEN YOU WILL STOP TRIBU");

	}

	public void push(byte id, byte data, Location loc, boolean isRemoved) {
		push(new BlockTraceNode(id, data, loc), isRemoved);
	}

	public void push(int typeId, MaterialData data, Location location, boolean isRemoved) {
		push((byte) typeId, data.getData(), location, isRemoved);

	}

	public void reverse() {
		while (top != null) {
			log("Reverse block : " + top.getType());
			pop().reverse();
		}

	}
}
