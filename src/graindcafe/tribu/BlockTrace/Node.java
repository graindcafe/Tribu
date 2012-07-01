package graindcafe.tribu.BlockTrace;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class Node {
	protected Location location;
	protected int id;
	protected byte data;
	protected boolean finalState=false;
	
	/**
	 * Create a regular node based on the block to save
	 * @param Block to save
	 */
	public Node(BlockState element)
	{
		location=element.getLocation().clone();
		id=element.getTypeId();
		data=element.getData().getData();
		finalState=true;
	}
	public Node(Block element)
	{
		location=element.getLocation().clone();
		id=element.getTypeId();
		data=element.getData();
		finalState=true;
	}
	protected Node()
	{
	}
	protected Node(Location loc, int id, byte data)
	{
		this.location=loc;
		this.id=id;
		this.data=data;
	}
	public Material getType()
	{
		return Material.getMaterial(id);
	}
	public void reverse()
	{
		this.location.getBlock().setTypeIdAndData(id, data, false);
	}
	public Location getLocation()
	{
		return location;
	}
}
