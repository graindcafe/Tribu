package graindcafe.tribu.BlockTracer;

import graindcafe.tribu.NotFoundException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
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
	private LinkedList<BlockTraceNode> ignitedBlocks, interactedBlocks;
	private LinkedList<Location> possibleIgnitedBlocks;
	private LinkedList<Location> movingBlock;
	
	
	public BlockTrace() {
		this.log = Logger.getLogger("Minecraft");
		this.ignitedBlocks=new LinkedList<BlockTraceNode>();
		this.interactedBlocks=new LinkedList<BlockTraceNode>();
		this.possibleIgnitedBlocks=new LinkedList<Location>();
		this.movingBlock=new LinkedList<Location>();
		
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
				theTop = theTop.getPrevious();
		return false;
	}

	public BlockTraceNode getNodeAt(Location loc) {
		BlockTraceNode theHead = top;
		while (theHead != null)
			if (theHead.LocationBlockEquals(loc))
				return theHead;
			else
				theHead = theHead.getPrevious();
		return top;
	}

	public boolean isEmpty() {
		return top == null;
	}

	private void log(String s) {
		 log.info(s);
	}

	public BlockTraceNode peek() {
		return top;
	}
	public BlockTraceNode pop() {
		BlockTraceNode r = top;
		top = top.getPrevious();
		return r;
	}
	public void pushIgnitedBlock(Block b)
	{
		log("Start fire at "+locToString(b.getLocation()));
		
		byte i=0;
		for(BlockFace bf : BlockFace.values())
		{
			Block nb=b.getRelative(bf);
			if(nb.getType().equals(Material.AIR))
				possibleIgnitedBlocks.add(nb.getLocation());
			else if(i<6)
				ignitedBlocks.add(new BlockTraceNode(nb));
			i++;
		}
	}
	public void pushBurntBlock(Block b)
	{
		log("Burnt : "+b.getType().toString());
		for(BlockTraceNode btn : ignitedBlocks)
		{
			if(btn.equals(b))
				push(b,true);
		}
		/*else
			for(BlockTraceNode btn : ignitedBlocks)
				for(BlockFace bf : BlockFace.values())
					if(btn.equals(b.getRelative(bf)))
						push(b,true);*/
	}
	public void pushPlayerInteract(Block b)
	{
		log("Player interact on "+b.getType().toString());
		interactedBlocks.add(new BlockTraceNode(b));
	}
	public void pushRedstoneChanged(Block b)
	{	
		/*if(interactedBlocks.contains(b))
		{
			log("Redstone changed at "+locToString(b.getLocation()));
			push(b,true);
		}*/
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

		node.setPrevious(top);
		BlockTraceNode previous = node;
		
		log("Checking for multiple blocks");
		// Door etc...
		if (node.hasOtherBlock()) {
			// for (Location l : node.getMultipleBlocks())
			log("Detected other block");
			try {
				previous = new BlockTraceNode(node.getOtherBlock(), previous);
			} catch (NotFoundException e) {
				
			}
		}
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

			log("Start adding the block to the list");
			
			
			log("Checking for fallings blocks");
			Location loc = node.getLocation().clone().add(0,1,0);
			Queue<BlockTraceNode> fallingBlocks = new LinkedList<BlockTraceNode>();
			// if is falling calc where the block fall
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
					fallingBlocks.peek().setPrevious(previous);
					// = the block to remove
					previous = new BlockTraceNode(loc, fallingBlocks.poll());
				}
			}
			
		} else {
			log("Block placed");
			// If this block may fall, then, place it as it will be removed
			// before
			// its platform
			log("Check if the block is falling");
			if (BlockTraceNode.isSubjectedToPhysical(node.getLocation().getBlock())) {
				node.setLocation(node.getLocation().add(0, 1, 0));
				if(BlockTraceNode.isSubjectedToPhysical(node.getLocation().getBlock()))
				{
					log("Block above is falling !");
					push(node,false);
				}
				node.setLocation(node.getLocation().subtract(0, 2, 0));
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

			//node.setPrevious(top);
		}
		top = previous;
		log("Push block " + node.getType());

		if (top.getPrevious() == node && node.getPrevious() == top)
			log("WARNING top precedent = node && node.precendent() == top. YOUR SERVER WILL NEVER STOP WHEN YOU WILL STOP TRIBU");

	}

	public void push(byte id, byte data, Location loc, boolean isRemoved) {
		push(new BlockTraceNode(id, data, loc), isRemoved);
	}

	public void push(int typeId, MaterialData data, Location location, boolean isRemoved) {
		push((byte) typeId, data.getData(), location, isRemoved);

	}
	public String locToString(Location loc)
	{
		return loc.getBlock().getType().toString()+" ("+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ()+")";
	}
	public void reverse() {
		
		for(Location loc: possibleIgnitedBlocks)
		{
			//for(BlockFace bf : BlockFace.values())
			{
				//Block b=btn.getLocation().getBlock().getRelative(bf);
				Block b = loc.getBlock();
				if(b.getType().equals(Material.FIRE))
				{
					log("Stop fire at " + locToString(loc));
					b.setType(Material.AIR);
				}
			}
		}
		while (top != null) {
			log("Reverse block : " + top.getType());
			pop().reverse();
		}

	}

	public boolean isFireSpreadingOut(Location location) {
		return possibleIgnitedBlocks.remove(location);
	}
	public boolean isWaterSpreadingOut(Location location) {
		return this.movingBlock.remove(location);
	}
	
	public void pushMovingBlock(Block block) {
		this.movingBlock.add(block.getLocation());
		push(block,false);
	}
}
