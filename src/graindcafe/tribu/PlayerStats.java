package graindcafe.tribu;

import org.bukkit.entity.Player;

public class PlayerStats implements Comparable<PlayerStats>{
	private Player player;
	private int money;
	private int points;
	private boolean alive;
	
	
	public PlayerStats(Player player) {
		this.player = player;
		alive = false;
	}

	public void addMoney(int amount) {
		money += amount;
	}

	public void addPoints(int amount) {
		points += amount;
	}

	public int compareTo(PlayerStats o) {
		if(o.getPoints() == points)
			return 0;
			else if(o.getPoints()> points)
				return -1;
			else
				return 1;
	}

	public int getMoney() {
		return money;
	}

	public Player getPlayer()
	{
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
		player.sendMessage(String.format(Constants.MessageMoneyPoints,String.valueOf(money),String.valueOf(points)));
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

	public boolean subtractmoney(int amount) {
		if (money >= amount) {
			money -= amount;
			return true;
		}
		return false;
	}

	public void subtractPoints(int val) {
		points -= val;
		if (points < 0) {
			points = 0;
		}
	}

}

