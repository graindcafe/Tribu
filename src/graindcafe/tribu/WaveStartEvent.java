package graindcafe.tribu;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WaveStartEvent extends Event {

	public WaveStartEvent(int waveNumber) {
		super();
		this.waveNumber = waveNumber;
	}

	public int waveNumber;

	public int getWaveNumber() {
		return waveNumber;
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}

}
