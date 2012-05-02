package graindcafe.tribu.signs;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.Package;
import graindcafe.tribu.TribuLevel;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopSign extends TribuSign {

	private static Package getItem(String[] signLines, TribuLevel level) {
		if(signLines == null  || level == null)
			return new Package(31);
		Package i;
		/* Try to get a package */ 
		i = level.getPackage((signLines[1] + "_" + signLines[2]));
		if (i==null || i.isEmpty())
			i = level.getPackage(signLines[1]);
		if (i==null || i.isEmpty())
			i = level.getPackage(signLines[2]);
		
		/* Try to get a single item */
		if (i==null || i.isEmpty())
			i = new Package(Material.getMaterial(signLines[1] + "_" + signLines[2]));
		// If the item is inexistent, let's try with
		// only the second line
		if (i.isEmpty())
			i = new Package(Material.getMaterial(signLines[1].toUpperCase()));
		// Still not ? With the third one, so
		if (i.isEmpty())
			i = new Package(Material.getMaterial(signLines[2].toUpperCase()));
		return i;
	}

	private int cost = 0;

	/* private Item droppedItem = null; */

	private Package items = null;

	public ShopSign(Tribu plugin) {
		super(plugin);
	}

	public ShopSign(Tribu plugin, Location pos, Material item, int cost) {
		super(plugin, pos);
		this.items = new Package(item);
		this.cost = cost;
	}

	public ShopSign(Tribu plugin, Location pos, Package item, int cost) {
		super(plugin, pos);
		this.items = item;
		this.cost = cost;
	}

	public ShopSign(Tribu plugin, Location pos, String item, int cost) {
		this(plugin, pos, Material.getMaterial(item), cost);
	}

	public ShopSign(Tribu plugin, Location pos, String[] signLines) {
		this(plugin, pos, getItem(signLines, plugin.getLevel()), TribuSign.parseInt(signLines[3]));
	}

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

	@Override
	public boolean isUsedEvent(Event e) {
		return e instanceof PlayerInteractEvent && ((PlayerInteractEvent) e).getClickedBlock().getLocation().equals(pos);
	}

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
						// maybe the inventory is full
						p.sendMessage(plugin.getLocale("Message.UnableToGiveYouThatItem"));
						stats.addMoney(cost);
						for (ItemStack i : givenItems)
							p.getInventory().remove(i);
						givenItems = null;
						break;
					}
				}
				
				p.updateInventory();
				// Alright
				p.sendMessage(String.format(plugin.getLocale("Message.PurchaseSuccessfulMoney"), String.valueOf(stats.getMoney())));
			} else {
				p.sendMessage(plugin.getLocale("Message.UnknownItem"));
				stats.addMoney(cost);
			}

		} else {
			p.sendMessage(plugin.getLocale("Message.YouDontHaveEnoughMoney"));
		}

	}

}
