/*******************************************************************************
 * Copyright or � or Copr. Quentin Godron (2011)
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
package graindcafe.tribu.Inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TribuInventory {
	protected HashMap<Player, List<ItemStack>> inventories;
	protected HashMap<Player, List<ItemStack>> armors;

	public TribuInventory() {
		inventories = new HashMap<Player, List<ItemStack>>();
		armors = new HashMap<Player, List<ItemStack>>();
	}

	public void addInventories(final Set<Player> players) {
		for (final Player p : players)
			addInventory(p);
	}

	public void addInventory(final Player p) {
		final PlayerInventory pInv = p.getInventory();

		inventories.put(p, Arrays.asList(pInv.getContents().clone()));
		armors.put(p, Arrays.asList(pInv.getArmorContents().clone()));
	}

	public void restoreInventories() {
		Set<Player> players = inventories.keySet();
		for (final Player p : players)
			uncheckedRestoreInventory(p);
		players = armors.keySet();
		for (final Player p : players)
			uncheckedRestoreArmor(p);
	}

	public void restoreInventory(final Player p) {
		if (inventories.containsKey(p))
			uncheckedRestoreInventory(p);
		if (armors.containsKey(p))
			uncheckedRestoreArmor(p);

	}

	protected void uncheckedRestoreArmor(final Player p) {
		p.getInventory().setArmorContents(
				(ItemStack[]) armors.remove(p).toArray());
	}

	protected void uncheckedRestoreInventory(final Player p) {
		p.getInventory().setContents(
				(ItemStack[]) inventories.remove(p).toArray());
	}
}
