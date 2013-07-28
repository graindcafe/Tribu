package graindcafe.tribu.Events;

import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickEvent extends PlayerInteractEvent {

	boolean player;
	boolean running;

	public SignClickEvent(PlayerInteractEvent e, boolean isPlaying,
			boolean isRunning) {
		super(e.getPlayer(), e.getAction(), e.getItem(), e.getClickedBlock(), e
				.getBlockFace());
		player = isPlaying;
	}

	public boolean isTribuPlayer() {
		return player;
	}

	public boolean isTribuRunning() {
		return running;
	}
}
