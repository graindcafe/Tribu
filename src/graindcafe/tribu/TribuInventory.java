package graindcafe.tribu;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TribuInventory {
	private ItemStack[] armor = new ItemStack[4];
	private ItemStack[] inventory = new ItemStack[36];
	private Player p;

	public TribuInventory(Player p) {
		this.p = p;
	}

	public TribuInventory(Player p, boolean captureNow) {
		this.p = p;
		if (captureNow)
			capture();
	}

	public TribuInventory(Player p, ItemStack[] items) {
		this.p = p;
		add(items);
	}

	public void add(ItemStack[] items) {
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
			for (ItemStack item : items) {
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

	public void drop(Location dropPlace) {
		for (ItemStack item : inventory)
			dropPlace.getWorld().dropItem(dropPlace, item);
		for (ItemStack item : armor)
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
		for (ItemStack item : inventory) {
			r += item.getType() + "x" + item.getAmount();
		}
		r = "Armor :\n";
		for (ItemStack item : armor) {
			r += item.getType() + "(" + item.getDurability() + ")";
		}
		return r;

	}
}
