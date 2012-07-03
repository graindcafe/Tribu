/*******************************************************************************
 * Copyright or © or Copr. Quentin Godron (2011)
 * 
 * cafe.en.grain@gmail.com
 * 
 * This software is a computer program whose purpose is to create zombie 
 * survival games on Bukkit's server. 
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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
