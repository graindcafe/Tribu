package graindcafe.tribu.Signs;

import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public class TopNamesSign extends HighscoreSign {

	public TopNamesSign(Tribu plugin) {
		super(plugin);
	}

	public TopNamesSign(Tribu plugin, Location pos) {
		super(plugin, pos);

	}

	@Override
	protected String[] getSpecificLines() {
		String[] lines = new String[4];
		lines[0]=lines[1]=lines[2]=lines[3]="";
		LinkedList<PlayerStats> stats = plugin.getSortedStats();
		Iterator<PlayerStats> i = stats.iterator();
		int count = 3;
		for (byte j = 1; j <= count; j++)
			lines[j] = String.valueOf(i.next().getPlayer().getDisplayName());
		return lines;
	}

	@Override
	public void raiseEvent() {
		Sign s = ((Sign) pos.getBlock().getState());
		String[] lines = getSpecificLines();
		// s.setLine(0, plugin.getLocale("Sign.HighscoreNames"));
		s.setLine(1, lines[1]);
		s.setLine(2, lines[2]);
		s.setLine(3, lines[3]);
		s.update();
	}

}
