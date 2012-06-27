package graindcafe.tribu.Signs;

import graindcafe.tribu.Tribu;

import org.bukkit.Location;
import org.bukkit.event.Event;

public abstract class HighscoreSign extends TribuSign {

	public HighscoreSign(Tribu plugin) {
		super(plugin);
	}

	public HighscoreSign(Tribu plugin, Location pos) {
		super(plugin, pos);

	}

	@Override
	public void init() {
		raiseEvent();
	}

	@Override
	public boolean isUsedEvent(Event e) {
		return true;
	}

	public abstract void raiseEvent();

	@Override
	public void raiseEvent(Event e) {
		raiseEvent();
	}
}
