package graindcafe.tribu.Signs;

import java.util.LinkedList;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;

public class TollSign extends TribuSign {

	private int cost;
	// private boolean clicked = false;
	private Block linkedButton;
	private static LinkedList<Player> allowedPlayer;

	public TollSign(Tribu plugin, Location pos, String[] lines) {
		super(plugin, pos);
		cost = TribuSign.parseInt(lines[1]);
		setAllowedPlayer(new LinkedList<Player>());
	}

	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		lines[0] = "";
		lines[1] = String.valueOf(cost);
		lines[2] = "";
		lines[3] = "";
		return lines;
	}

	@Override
	public void init() {
		Block current;
		// plugin.LogInfo("Toll Sign at " + pos.getBlockX() + ", " +
		// pos.getBlockY() + ", " + pos.getBlockZ());
		BlockFace[] firstFaces = new BlockFace[] { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN };
		BlockFace[] secondFaces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
		for (BlockFace bf : firstFaces) {
			current = pos.getBlock().getRelative(bf);
			for (BlockFace bf2 : secondFaces) {
				/*
				 * plugin.LogInfo(bf2.name() + " face to " + bf.name() +
				 * " face at " +
				 * current.getRelative(bf2).getLocation().getBlockX() + ", " +
				 * current.getRelative(bf2).getLocation().getBlockY() + ", " +
				 * current.getRelative(bf2).getLocation().getBlockZ() + " is " +
				 * current.getRelative(bf2).getType());
				 */

				if (current.getRelative(bf2).getType() == Material.LEVER || current.getRelative(bf2).getType() == Material.STONE_BUTTON
						|| current.getRelative(bf2).getType() == Material.STONE_PLATE || current.getRelative(bf2).getType() == Material.WOOD_PLATE
						|| current.getRelative(bf2).getType() == Material.WOOD_DOOR || current.getRelative(bf2).getType() == Material.TRAP_DOOR) {
					linkedButton = current.getRelative(bf2);
					return;
				}
			}
		}

	}

	@Override
	public boolean isUsedEvent(Event e) {
		return e instanceof PlayerInteractEvent;
	}

	@Override
	public void raiseEvent(Event ev) {
		PlayerInteractEvent e = (PlayerInteractEvent) ev;
		// Wait for the second event of a button

		if (linkedButton != null) {

			if (e.getClickedBlock().equals(linkedButton)) {
				Player p = e.getPlayer();
				
				PlayerStats stats = plugin.getStats(p);
				if (!getAllowedPlayer().contains(p) && !stats.subtractmoney(cost)) {
					
					p.sendMessage(plugin.getLocale("Message.YouDontHaveEnoughMoney"));
					e.setCancelled(true);
					if(linkedButton instanceof Door)
						if(linkedButton.getState().getType() != Material.IRON_DOOR)
							((Door)linkedButton).setOpen(false);
				} else
				{
					getAllowedPlayer().add(p);
					p.sendMessage(String.format(plugin.getLocale("Message.PurchaseSuccessfulMoney"), String.valueOf(stats.getMoney())));
				}
			}
		}

	}

	public static LinkedList<Player> getAllowedPlayer() {
		return allowedPlayer;
	}

	public static void setAllowedPlayer(LinkedList<Player> allowedPlayer) {
		TollSign.allowedPlayer = allowedPlayer;
	}

}
