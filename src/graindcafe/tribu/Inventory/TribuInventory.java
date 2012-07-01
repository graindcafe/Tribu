package graindcafe.tribu.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.inventory.PlayerInventory;

public class TribuInventory 
{
	protected HashMap<Player, List<ItemStack>> inventories;
	protected HashMap<Player, List<ItemStack>> armors;
	
	public TribuInventory() 
	{
		inventories = new HashMap<Player, List<ItemStack>>();
		armors= new HashMap<Player,List<ItemStack>>();
	}
	
	public void addInventory(Player p) 
	{
		PlayerInventory pInv = p.getInventory();
		
		inventories.put(p, Arrays.asList(pInv.getContents().clone()));
		armors.put(p, Arrays.asList(pInv.getArmorContents().clone()));
	}
	public void addInventories(Set<Player> players)
	{
		for(Player p: players)
		{
			addInventory(p);
		}
	}
	public void restoreInventories()
	{
		Set<Player> players=inventories.keySet();
		for(Player p: players)
		{
			uncheckedRestoreInventory(p);
		}
		players=armors.keySet();
		for(Player p: players)
		{
			uncheckedRestoreArmor(p);
		}
	}
	protected void uncheckedRestoreInventory(Player p)
	{
		p.getInventory().setContents((ItemStack[]) inventories.remove(p).toArray());
	}
	protected void uncheckedRestoreArmor(Player p)
	{
		p.getInventory().setArmorContents((ItemStack[]) armors.remove(p).toArray());
	}
	public void restoreInventory(Player p) 
	{
		if (inventories.containsKey(p)) 
		{
			uncheckedRestoreInventory(p);
		}
		if (armors.containsKey(p)) 
		{
			uncheckedRestoreArmor(p);
		}
		
	}
}
