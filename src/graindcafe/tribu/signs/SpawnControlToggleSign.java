package graindcafe.tribu.signs;

import graindcafe.tribu.Tribu;

import org.bukkit.Location;

public class SpawnControlToggleSign extends SpawnControlSign {
	protected boolean state = true;

	public SpawnControlToggleSign(Tribu plugin, Location pos, String[] Lines) {
		super(plugin, pos, Lines);
		state = Lines[2].equalsIgnoreCase("on") || Lines[2].equals("1");
	}

	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		lines[1] = ZombieSpawn;
		lines[2] = state ? "ON" : "OFF";
		return lines;
	}

	@Override
	public void raiseEvent() {
		if (pos.getBlock().isBlockPowered()) {
			if (state) {
				plugin.getLevel().activateZombieSpawn(ZombieSpawn);

			} else {
				plugin.getLevel().desactivateZombieSpawn(ZombieSpawn);

			}
		}
	}

}
