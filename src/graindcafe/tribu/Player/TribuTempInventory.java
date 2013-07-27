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
package graindcafe.tribu.Player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TribuTempInventory {
	private ItemStack[] armor = new ItemStack[4];
	private ItemStack[] inventory = new ItemStack[36];
	private final Player p;

	public TribuTempInventory(final Player p) {
		this.p = p;
	}

	public TribuTempInventory(final Player p, final boolean captureNow) {
		this.p = p;
		if (captureNow)
			capture();
	}

	public TribuTempInventory(final Player p, final ItemStack[] items) {
		this.p = p;
		add(items);
	}

	public void add(final ItemStack[] items) {
		if (items.length > 36) {
			// We have a big problem...
			// TODO:
			byte i = 0;
			while (i < 36) {
				inventory[i] = items[i];
				i++;
			}
		} else {
			byte i = 0;
			for (final ItemStack item : items) {
				inventory[i] = item;
				i++;
			}

		}
	}

	public void capture() {
		inventory = p.getInventory().getContents();
		armor = p.getInventory().getArmorContents();
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}

	public void drop(final Location dropPlace) {
		for (final ItemStack item : inventory)
			dropPlace.getWorld().dropItem(dropPlace, item);
		for (final ItemStack item : armor)
			dropPlace.getWorld().dropItem(dropPlace, item);
	}

	public void restore() {

		// clear the inventory
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		// add items
		p.getInventory().setContents(inventory);
		p.getInventory().setArmorContents(armor);
	}

	@Override
	public String toString() {
		String r;
		r = "Inventory :\n";
		for (final ItemStack item : inventory)
			r += item.getType() + "x" + item.getAmount();
		r = "Armor :\n";
		for (final ItemStack item : armor)
			r += item.getType() + "(" + item.getDurability() + ")";
		return r;

	}
}
