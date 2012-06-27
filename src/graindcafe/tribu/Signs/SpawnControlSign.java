package graindcafe.tribu.Signs;

import graindcafe.tribu.Tribu;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockRedstoneEvent;

public class SpawnControlSign extends TribuSign {

	String ZombieSpawn;

	public SpawnControlSign(Tribu plugin) {
		super(plugin);
	}

	public SpawnControlSign(Tribu plugin, Location pos, String[] Lines) {
		super(plugin, pos);
		ZombieSpawn = Lines[1];
	}

	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		lines[0]=lines[1]=lines[2]=lines[3]="";
		lines[1] = ZombieSpawn;
		return lines;
	}

	@Override
	public void init() {
		raiseEvent();
	}

	@Override
	public boolean isUsedEvent(Event e) {
		return e instanceof BlockRedstoneEvent;
	}

	public void raiseEvent() {
		if (pos.getBlock().isBlockPowered()) {
			plugin.getLevel().activateZombieSpawn(ZombieSpawn);

		} else {
			plugin.getLevel().deactivateZombieSpawn(ZombieSpawn);

		}
	}

	@Override
	public void raiseEvent(Event e) {
		raiseEvent();
	}
}
