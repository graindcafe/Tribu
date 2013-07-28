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
package graindcafe.tribu.Signs;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.Player.PlayerStats;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TollSign extends TribuSign {

	private final int cost;
	// private boolean clicked = false;
	private Block linkedButton;
	private final LinkedList<Player> allowedPlayer;
	private Player lastPlayerTry;
	private boolean preventSpam = false;
	private boolean eachTime = false;

	public TollSign(final Tribu plugin, final Location pos, final String[] lines) {
		super(plugin, pos);
		cost = TribuSign.parseInt(lines[1]);
		eachTime = lines[2].toLowerCase().contains(
				plugin.getLocale("Sign.Each").toLowerCase());
		allowedPlayer = new LinkedList<Player>();
	}

	@Override
	public void finish() {
		if (allowedPlayer != null)
			allowedPlayer.clear();
	}

	public LinkedList<Player> getAllowedPlayer() {
		return allowedPlayer;
	}

	@Override
	protected String[] getSpecificLines() {
		final String[] lines = new String[4];
		lines[0] = "";
		lines[1] = String.valueOf(cost);
		if (eachTime)
			lines[2] = plugin.getLocale("Sign.Each");
		else
			lines[2] = "";
		lines[3] = "";
		return lines;
	}

	@Override
	public void init() {
		Block current;
		final BlockFace[] firstFaces = new BlockFace[] { BlockFace.SELF,
				BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
				BlockFace.EAST, BlockFace.WEST };
		final BlockFace[] secondFaces = new BlockFace[] { BlockFace.SELF,
				BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
				BlockFace.WEST };
		for (final BlockFace bf : firstFaces) {
			current = pos.getBlock().getRelative(bf);
			for (final BlockFace bf2 : secondFaces)
				// if it's a clickable block
				if (current.getRelative(bf2).getType() == Material.LEVER
						|| current.getRelative(bf2).getType() == Material.STONE_BUTTON
						|| current.getRelative(bf2).getType() == Material.STONE_PLATE
						|| current.getRelative(bf2).getType() == Material.WOOD_PLATE
						|| current.getRelative(bf2).getType() == Material.FENCE_GATE
						|| current.getRelative(bf2).getType() == Material.WOODEN_DOOR
						|| current.getRelative(bf2).getType() == Material.TRAP_DOOR) {
					linkedButton = current.getRelative(bf2);
					preventSpam = linkedButton.getType() == Material.STONE_PLATE
							|| linkedButton.getType() == Material.WOOD_PLATE;
					return;
				}
		}

	}

	@Override
	public boolean isUsedEvent(final Event e) {
		return e instanceof PlayerInteractEvent
				&& linkedButton != null
				&& ((linkedButton.getType() != Material.WOODEN_DOOR && linkedButton
						.getType() != Material.FENCE_GATE) || ((PlayerInteractEvent) e)
						.getAction().equals(Action.RIGHT_CLICK_BLOCK));
	}

	private boolean isLinked(Block block) {
		return block.equals(linkedButton)
				|| (linkedButton.getType().equals(Material.WOODEN_DOOR) //
				&& ((linkedButton.getRelative(BlockFace.UP).getType()
						.equals(Material.WOODEN_DOOR) //
				&& block.equals(linkedButton.getRelative(BlockFace.UP))) //
				|| (linkedButton.getRelative(BlockFace.DOWN).getType()
						.equals(Material.WOODEN_DOOR) //
				&& block.equals(linkedButton.getRelative(BlockFace.DOWN)))));
	}

	@Override
	public void raiseEvent(final Event ev) {
		final PlayerInteractEvent e = (PlayerInteractEvent) ev;
		// Wait for the second event of a button

		if (isLinked(e.getClickedBlock())) {
			final Player p = e.getPlayer();
			if (!preventSpam || !lastPlayerTry.equals(p)) {
				final PlayerStats stats = plugin.getStats(p);
				if ((eachTime || !allowedPlayer.contains(p))
						&& !stats.subtractmoney(cost)) {

					Tribu.messagePlayer(p,
							plugin.getLocale("Message.YouDontHaveEnoughMoney"));
					e.setCancelled(true);
				} else if (!allowedPlayer.contains(p)) {
					if (!eachTime)
						allowedPlayer.add(p);
					Tribu.messagePlayer(
							p,
							String.format(
									plugin.getLocale("Message.PurchaseSuccessfulMoney"),
									String.valueOf(stats.getMoney())));
				}
			}
			lastPlayerTry = p;
		}

	}
}
