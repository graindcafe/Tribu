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
package graindcafe.tribu.Signs;

import graindcafe.tribu.PlayerStats;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.Package;
import graindcafe.tribu.Level.TribuLevel;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Graindcafe
 *
 */
public class ShopSign extends TribuSign {

	/**
	 * @param signLines lines of the sign 
	 * @param level	the currentLevel
	 * @return the found package or an empty one
	 */
	private static Package getItem(String[] signLines, TribuLevel level) {
		if(signLines == null  || level == null)
			return new Package();
		Package i;
		/* Try to get a package */ 
		i = level.getPackage((signLines[1] + "_" + signLines[2]));
		if (i==null || i.isEmpty())
			i = level.getPackage(signLines[1]);
		if (i==null || i.isEmpty())
			i = level.getPackage(signLines[2]);
		
		/* Try to get a single item */
		if (i==null || i.isEmpty())
			i = new Package(Material.getMaterial(signLines[1].toUpperCase() + "_" + signLines[2].toUpperCase()));
		// If the item is inexistent, let's try with
		// only the second line
		if (i.isEmpty())
			i = new Package(Material.getMaterial(signLines[1].toUpperCase()));
		// Still not ? With the third one, so
		if (i.isEmpty())
			i = new Package(Material.getMaterial(signLines[2].toUpperCase()));
		return i;
	}

	/**
	 * Cost of this sign
	 */
	private int cost = 0;

	/* private Item droppedItem = null; */

	/**
	 * Items to sell 
	 */
	private Package items = null;

	/**
	 * Default constructor (never used ?)
	 * @param plugin Tribu
	 */
	public ShopSign(Tribu plugin) {
		super(plugin);
	}

	/**
	 * 
	 * @param plugin Tribu
	 * @param pos Location of the sign
	 * @param item Material to be sold
	 * @param cost 
	 */
	public ShopSign(Tribu plugin, Location pos, Material item, int cost) {
		super(plugin, pos);
		this.items = new Package(item);
		this.cost = cost;
	}

	/**
	 * @param plugin Tribu
	 * @param pos Location of the sign
	 * @param item Package to be sold
	 * @param cost
	 */
	public ShopSign(Tribu plugin, Location pos, Package item, int cost) {
		super(plugin, pos);
		this.items = item;
		this.cost = cost;
	}

	/**
	 * @param plugin Tribu
	 * @param pos Location of the sign
	 * @param item String of the item to sell 
	 * @param cost
	 */
	public ShopSign(Tribu plugin, Location pos, String item, int cost) {
		this(plugin, pos, Material.getMaterial(item), cost);
	}

	/**
	 * @param plugin Tribu
	 * @param pos Location of the sign
	 * @param signLines Lines of this sign 
	 */
	public ShopSign(Tribu plugin, Location pos, String[] signLines) {
		this(plugin, pos, getItem(signLines, plugin.getLevel()), TribuSign.parseInt(signLines[3]));
	}

	/* Get lines specific for this sign (non-Javadoc)
	 * @see graindcafe.tribu.signs.TribuSign#getSpecificLines()
	 */
	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		lines[0] = "";
		if (items.toString().lastIndexOf('_') < 0) {
			lines[1] = items.toString();
			lines[2] = "";
		} else {
			lines[1] = items.toString().substring(0, items.toString().lastIndexOf('_'));
			lines[2] = items.toString().substring(items.toString().lastIndexOf('_') + 1);
		}
		lines[3] = String.valueOf(cost);
		return lines;
	}

	/* Init the sign : find the item to be sold (non-Javadoc)
	 * @see graindcafe.tribu.signs.TribuSign#init()
	 */
	@Override
	public void init() {
		if(pos.getBlock().getState() instanceof Sign)
			this.items = getItem(((Sign)pos.getBlock().getState() ).getLines(), plugin.getLevel());
		else
			plugin.LogWarning("Missing sign !");
		// TODO:Fix it, it should just "display" the item but make it not
		// lootable. We can loot it
		/*
		 * if (!items.getItemStacks().isEmpty() && (droppedItem==null ||
		 * droppedItem.isDead()) &&
		 * plugin.getConfig().getBoolean("Signs.ShopSign.DropItem", true)) {
		 * for(ItemStack n : items.getItemStacks()) { droppedItem =
		 * pos.getWorld().dropItem(pos, new ItemStack(item));
		 * droppedItem.setVelocity(new Vector(0, 0, 0)); } }
		 */
	}

	/* Is the passed argument is used for this sign ? (non-Javadoc)
	 * @see graindcafe.tribu.signs.TribuSign#isUsedEvent(org.bukkit.event.Event)
	 */
	@Override
	public boolean isUsedEvent(Event e) {
		return e instanceof PlayerInteractEvent && ((PlayerInteractEvent) e).getClickedBlock().getLocation().equals(pos);
	}

	/* Sell the item (non-Javadoc)
	 * @see graindcafe.tribu.signs.TribuSign#raiseEvent(org.bukkit.event.Event)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void raiseEvent(Event e) {
		Player p = ((PlayerInteractEvent) e).getPlayer();
		PlayerStats stats = plugin.getStats(p);
		if (stats.subtractmoney(cost)) {
			if (!items.isEmpty()) {
				LinkedList<ItemStack> givenItems = new LinkedList<ItemStack>();
				for (ItemStack item : items.getItemStacks()) {
					givenItems.add(item);
					HashMap<Integer, ItemStack> failed = p.getInventory().addItem(item);

					if (failed != null && failed.size() > 0) {
						// the inventory may be full
						Tribu.messagePlayer(p, (plugin.getLocale("Message.UnableToGiveYouThatItem")));
						stats.addMoney(cost);
						for (ItemStack i : givenItems)
							p.getInventory().remove(i);
						givenItems = null;
						break;
					}
				}
				p.updateInventory();
				// Alright
				Tribu.messagePlayer(p,String.format(plugin.getLocale("Message.PurchaseSuccessfulMoney"), String.valueOf(stats.getMoney())));
			} else {
				Tribu.messagePlayer(p,plugin.getLocale("Message.UnknownItem"));
				stats.addMoney(cost);
			}

		} else {
			Tribu.messagePlayer(p,plugin.getLocale("Message.YouDontHaveEnoughMoney"));
		}

	}

}
