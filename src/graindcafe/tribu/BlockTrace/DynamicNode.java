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
