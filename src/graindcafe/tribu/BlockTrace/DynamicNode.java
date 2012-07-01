package graindcafe.tribu.BlockTrace;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public abstract class DynamicNode extends Node{

	public static boolean isDynamic(BlockState element)
	{
		return isSubjectToGravity(element) || canSpreadOut(element);
	}
	public static boolean isSubjectToGravity(BlockState elm)
	{
		return (elm.getType()==Material.GRAVEL ||
				elm.getType()==Material.SAND);
	}
	public static boolean canSpreadOut(BlockState elm)
	{
		switch(elm.getType())
		{
		case WATER:
		case LAVA:
			return true;
		default:
			return false;
		}
	}
	public static boolean isSolid(BlockState elm)
	{
		switch(elm.getType())
		{
		case AIR:
		case WATER:
		case LAVA:
		case STATIONARY_WATER:
		case STATIONARY_LAVA:
		case FIRE:
			return false;
		default:
			return true;
		}
	}
	
	/**
	 * Is held by another block. 
	 * Torch, redstone etc.. are held by another block
	 * @param id
	 * @return
	 */
	public static boolean isHeld(int id) {
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
	public static DynamicNode init(BlockState before, Block after)
	{
		if(isSubjectToGravity(after.getState()))
		{
			return new GravitySubject(after);
		}
		return null;
	}
	public abstract void update();
	public abstract boolean isStable();
	public abstract Node getFinalNode();
	

}
