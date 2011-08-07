package graindcafe.tribu.signs;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class TopPointsSign extends HighscoreSign {

	public TopPointsSign(Tribu plugin) {
		super(plugin);
	}

	public TopPointsSign(Tribu plugin, Location pos) {
		super(plugin, pos);

	}

	@Override
	public void raiseEvent() {
		Sign s = ((Sign) pos.getBlock().getState());
		s.setLine(0, plugin.getLocale("Sign.HighscorePoints"));
		s.setLine(1, "");
		s.setLine(2, "");
		s.setLine(3, "");
		LinkedList<PlayerStats> stats = plugin.getSortedStats();
		Iterator<PlayerStats> i = stats.iterator();
		int count = plugin.getPlayersCount();
		if (count > 0)
			s.setLine(1, String.valueOf(i.next().getPoints()));
		if (count > 1)
			s.setLine(2, String.valueOf(i.next().getPoints()));
		if (count > 2)
			s.setLine(3, String.valueOf(i.next().getPoints()));
		s.update();
	}
}
