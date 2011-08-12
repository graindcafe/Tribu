package graindcafe.tribu.signs;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShopSign extends TribuSign {

	private static Material getItem(String[] signLines) {
		Material i;
		i = Material.getMaterial(signLines[1].toUpperCase() + "_" + signLines[2].toUpperCase());
		// If the item is inexistent, let's try with
		// only the second line
		if (i == null)
			i = Material.getMaterial(signLines[1].toUpperCase());
		// Still no ? With the third one, so
		if (i == null)
			i = Material.getMaterial(signLines[2].toUpperCase());
		return i;
	}
	private int cost = 0;
	private boolean initialized = false;

	private Material item = null;

	public ShopSign(Tribu plugin) {
		super(plugin);
	}

	public ShopSign(Tribu plugin, Location pos, Material item, int cost) {
		super(plugin, pos);
		this.item = item;
		this.cost = cost;

	}

	public ShopSign(Tribu plugin, Location pos, String item, int cost) {
		this(plugin, pos, Material.getMaterial(item), cost);
	}

	public ShopSign(Tribu plugin, Location pos, String[] signLines) {
		this(plugin, pos, getItem(signLines), TribuSign.parseInt(signLines[3]));
	}

	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		if (item.toString().lastIndexOf('_') < 0)
			lines[1] = item.toString();
		else {
			lines[1] = item.toString().substring(0, item.toString().lastIndexOf('_'));
			lines[2] = item.toString().substring(item.toString().lastIndexOf('_') + 1);
		}
		lines[3] = String.valueOf(cost);
		return lines;
	}

	@Override
	public void init() {
		if (!initialized) {
			initialized = true;
			pos.getWorld().dropItem(pos, new ItemStack(item));
		}
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
			if (item != null) {
				HashMap<Integer, ItemStack> failed = p.getInventory().addItem(new ItemStack(item, 1));
				p.updateInventory();
				if (failed.size() > 0) {
					// maybe the inventory is full
					p.sendMessage(plugin.getLocale("Message.UnableToGiveYouThatItem"));
					stats.addMoney(cost);
				} else {
					// Alright
					p.sendMessage(String.format(plugin.getLocale("Message.PurchaseSuccessfulMoney"), String.valueOf(stats.getMoney())));
				}
			} else {
				p.sendMessage(plugin.getLocale("Message.UnknownItem"));
				stats.addMoney(cost);
			}

		} else {
			p.sendMessage(plugin.getLocale("Message.YouDontHaveEnoughMoney"));
		}

	}

}
