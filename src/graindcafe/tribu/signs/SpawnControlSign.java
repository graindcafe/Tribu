package graindcafe.tribu.signs;

import graindcafe.tribu.Tribu;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockRedstoneEvent;

public class SpawnControlSign extends TribuSign {

	String ZombieSpawn;
	public SpawnControlSign(Tribu plugin)
	{
		super(plugin);
	}
	public SpawnControlSign(Tribu plugin, Location pos, String[] Lines) {
		super(plugin, pos);
		ZombieSpawn = Lines[1];
	}

	@Override
	public boolean isUsedEvent(Event e) {
		return e instanceof BlockRedstoneEvent;
	}

	@Override
	public void raiseEvent(Event e) {
		raiseEvent();
	}
	public void raiseEvent()
	{
		if(pos.getBlock().isBlockPowered())
		{
			plugin.getLevel().activateZombieSpawn(ZombieSpawn);
			
		} else{
			plugin.getLevel().desactivateZombieSpawn(ZombieSpawn);
			
		}
	}
}
