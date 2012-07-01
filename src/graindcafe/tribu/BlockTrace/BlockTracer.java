package graindcafe.tribu.BlockTrace;

import graindcafe.tribu.Tribu;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.minecraft.server.Material;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockTracer extends Thread {
	protected HashMap<Location, Node> memory;
	protected LinkedList<DynamicNode> dynamicNodes;
	protected int taskId=-1;
	protected Tribu plugin;
	/**
	 * Initialize a tracer
	 */
	public BlockTracer(Tribu plugin) {
		memory = new HashMap<Location, Node>();
		dynamicNodes = new LinkedList<DynamicNode>();
		this.plugin=plugin;
	}
	
	/**
	 * Push a new modification
	 * 
	 * @param before
	 * @param after
	 */
	public void push(Block before, Block after) {
		push(before.getState(),after);
	}
	public void push(BlockState before, Block after) {
		if (!DynamicNode.isDynamic(after.getState())) {
			uncheckedPush(new Node(before));
		} else {
			dynamicNodes.add(DynamicNode.init(before, after));
			if(taskId==-1)
			{
				taskId=plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, 5);
			}
		}
	}
	/*public void push(Block before, Block after) {
		if(after==null)
			push(before.getState());
		else
			push(before.getState(),after.getState());
	}*/
	/**
	 * Push a block remove, a block ignite or any modification without a new block
	 * 
	 * @param before
	 * @param after
	 */
	public void push(BlockState before) {
		debugMsg(before.getType().toString() +" > "+Material.AIR.toString());
		{
			uncheckedPush(new Node(before));
		}
	}

	/**
	 * Reverse saved modifications
	 */
	public void reverse() {
		synchronized(this) {
			this.run();
			for (Node node : memory.values()) {
				debugMsg(" < "+node.getType().toString());
				node.reverse();
			}
			memory.clear();
		}
	}

	protected void uncheckedPush(final Node node) {
		if (!memory.containsKey(node.getLocation()))
		{
			debugMsg(node.getType().toString() +" > ");
			memory.put(node.getLocation(), node);
		}
		// Else there is already a memory for this slot
	}
	protected static void debugMsg(String msg)
	{
		Logger.getLogger("Minecraft").info(msg);
	}
	/*
	 * (non-Javadoc) Update dynamic blocks
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		debugMsg("run run run");
		Collection<DynamicNode> becameStatic = new LinkedList<DynamicNode>();
		for (DynamicNode node : dynamicNodes) {
			node.update();
			if (node.isStable())
				becameStatic.add(node);
		}
		for (DynamicNode node : becameStatic) {
			uncheckedPush(node.getFinalNode());
		}
		dynamicNodes.removeAll(becameStatic);
		// probably useless : becameStatic.clear();
		if(dynamicNodes.isEmpty())
		{
			Bukkit.getScheduler().cancelTask(taskId);
			taskId=-1;
		}
	}
}
