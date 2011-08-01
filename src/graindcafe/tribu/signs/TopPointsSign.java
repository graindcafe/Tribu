package graindcafe.tribu.signs;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

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
		int count = plugin.getPlayersCount();
		if (count > 0)
			s.setLine(1, String.valueOf(stats.getFirst().getPoints()));
		if (count > 1)
			s.setLine(2, String.valueOf(stats.get(1).getPoints()));
		if (count > 2)
			s.setLine(3, String.valueOf(stats.get(0).getPoints()));
		s.update();
	}
}
