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

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LeaveSign extends TribuSign {

	public LeaveSign(final Tribu plugin, final Location pos,
			final String[] lines) {
		super(plugin, pos);
	}

	@Override
	public void finish() {

	}

	@Override
	protected String[] getSpecificLines() {
		final String[] lines = new String[4];
		lines[0] = "";
		lines[1] = "";
		lines[2] = "";
		lines[3] = "";
		return lines;
	}

	@Override
	public void init() {

	}

	@Override
	public boolean isUsedEvent(final Event e) {
		return e instanceof PlayerInteractEvent
				&& ((PlayerInteractEvent) e).getAction().equals(
						Action.RIGHT_CLICK_BLOCK);
	}

	@Override
	public void raiseEvent(final Event ev) {
		final PlayerInteractEvent e = (PlayerInteractEvent) ev;
		plugin.removePlayer(e.getPlayer());
	}
}
