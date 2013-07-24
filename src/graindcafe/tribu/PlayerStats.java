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
package graindcafe.tribu;

import graindcafe.tribu.Configuration.Constants;

import org.bukkit.entity.Player;

public class PlayerStats implements Comparable<PlayerStats> {
	private boolean alive;
	private int money;
	private final Player player;
	private int points;

	public PlayerStats(final Player player) {
		this.player = player;
		alive = false;
	}

	public void addMoney(final int amount) {
		money += amount;
	}

	public void addPoints(final int amount) {
		points += amount;
	}

	// Order reversed to sort list desc
	public int compareTo(final PlayerStats o) {
		if (o.getPoints() == points)
			return 0;
		else if (o.getPoints() > points)
			return 1;
		else
			return -1;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof PlayerStats))
			return false;
		final PlayerStats ps = (PlayerStats) o;
		return ps.getPlayer().equals(player) && ps.getMoney() == money
				&& ps.getPoints() == points;
	}

	public int getMoney() {
		return money;
	}

	public Player getPlayer() {
		return player;
	}

	public int getPoints() {
		return points;
	}

	public boolean isalive() {
		return alive;
	}

	public void kill() {
		alive = false;
	}

	public void msgStats() {
		Tribu.messagePlayer(
				player,
				String.format(Constants.MessageMoneyPoints,
						String.valueOf(money), String.valueOf(points)));
	}

	public void resetMoney() {
		money = 0;
	}

	public void resetPoints() {
		points = 0;
	}

	public void revive() {
		alive = true;
	}

	public boolean subtractmoney(final int amount) {
		if (money >= amount) {
			money -= amount;
			return true;
		}
		return false;
	}

	public void subtractPoints(final int val) {
		points -= val;
		if (points < 0)
			points = 0;
	}

}
