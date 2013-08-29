package graindcafe.tribu.Player;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BeforeGamePlayerState {
	Player p;
	double health;
	double maxHealth;
	int foodLevel;
	float exp;
	int level;
	Location point;
	Location bed;
	List<ItemStack> inventories = null;
	List<ItemStack> armors = null;
	GameMode mode;

	public BeforeGamePlayerState(Player p, boolean inventory,
			boolean gameModeAdventure) {
		this.p = p;
		health = p.getHealth();
		maxHealth = p.getMaxHealth();
		foodLevel = p.getFoodLevel();
		exp = p.getExp();
		level = p.getLevel();
		point = p.getLocation();
		bed = p.getBedSpawnLocation();
		mode = p.getGameMode();
		if (inventory)
			addInventory();
		clear(inventory, gameModeAdventure);
	}

	public void clear(boolean inventory, boolean gameModeAdventure) {
		p.setLevel(0);
		p.setExp(0);
		p.setFoodLevel(20);
		p.setHealth(p.getMaxHealth());
		p.setGameMode(gameModeAdventure ? GameMode.ADVENTURE
				: GameMode.SURVIVAL);
		if (inventory)
			p.getInventory().clear();
	}

	public void restore() {
		p.setMaxHealth(maxHealth);
		p.setHealth(health);
		p.setFoodLevel(foodLevel);
		p.setLevel(level);
		p.setExp(exp);
		p.teleport(point);
		p.setBedSpawnLocation(bed, true);
		p.setGameMode(mode);
		restoreInventory();
	}

	public void addInventory() {
		final PlayerInventory pInv = p.getInventory();

		inventories = Arrays.asList(pInv.getContents().clone());
		armors = Arrays.asList(pInv.getArmorContents().clone());
	}

	@SuppressWarnings("deprecation")
	public void restoreInventory() {
		if (inventories != null)
			uncheckedRestoreInventory();
		if (armors != null)
			uncheckedRestoreArmor();
		p.updateInventory();
	}

	protected void uncheckedRestoreArmor() {
		p.getInventory().setArmorContents((ItemStack[]) armors.toArray());
	}

	protected void uncheckedRestoreInventory() {
		p.getInventory().setContents((ItemStack[]) inventories.toArray());
	}
}
