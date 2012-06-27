package graindcafe.tribu.Executors;

import graindcafe.tribu.Listeners.TribuPlayerListener;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;

public class TribuPlayerJoin implements EventExecutor{

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		((TribuPlayerListener) listener).getPlugin().addPlayer(((PlayerJoinEvent) event).getPlayer());
	}
}
