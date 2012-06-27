package graindcafe.tribu.Signs;

import graindcafe.tribu.Tribu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;

public abstract class TribuSign {

	public static TribuSign getObject(Tribu plugin, Location pos) {
		if (Sign.class.isInstance(pos.getBlock().getState()))
			return getObject(plugin, pos, ((Sign) pos.getBlock().getState()).getLines());
		else
			return null;

	}

	public static TribuSign getObject(Tribu plugin, Location pos, String[] lines) {

		TribuSign ret = null;
		if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Buy")))
			ret = new ShopSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscoreNames")))
			ret = new TopNamesSign(plugin, pos);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscorePoints")))
			ret = new TopPointsSign(plugin, pos);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Spawner")))
			ret = new SpawnControlSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.ToggleSpawner")))
			ret = new SpawnControlToggleSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.TollSign")))
			ret = new TollSign(plugin, pos, lines);

		return ret;
	}

	public static TribuSign getObject(Tribu plugin, Sign sign) {
		return getObject(plugin, sign.getBlock().getLocation(), sign.getLines());
	}

	public static boolean isIt(Tribu plugin, Block b) {
		if (Sign.class.isInstance(b.getState()))
			return isIt(plugin, ((Sign) b.getState()).getLines());
		return false;
	}

	public static boolean isIt(Tribu plugin, String[] lines) {
		return lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Buy")) || lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscoreNames"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscorePoints")) || lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Spawner"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.ToggleSpawner")) || lines[0].equalsIgnoreCase(plugin.getLocale("Sign.TollSign"));
	}

	public static TribuSign LoadFromStream(Tribu plugin, World world, DataInputStream stream) {
		try {
			Location pos = new Location(world, stream.readDouble(), stream.readDouble(), stream.readDouble());
			return getObject(plugin, pos);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int parseInt(String s) {
		int num = 0;
		for (char c : s.toCharArray()) {

			switch (c) {
			case '0':
				num = num * 10 + 0;
				break;
			case '1':
				num = num * 10 + 1;
				break;
			case '2':
				num = num * 10 + 2;
				break;
			case '3':
				num = num * 10 + 3;
				break;
			case '4':
				num = num * 10 + 4;
				break;
			case '5':
				num = num * 10 + 5;
				break;
			case '6':
				num = num * 10 + 6;
				break;
			case '7':
				num = num * 10 + 7;
				break;
			case '8':
				num = num * 10 + 8;
				break;
			case '9':
				num = num * 10 + 9;
				break;
			default:
				break;
			}

		}
		return num;
	}

	public static void update(Sign s) {
		String[] lines = s.getLines();
		for (byte i = 0; i < lines.length; i++)
			s.setLine(i, lines[i]);
		s.update();

	}

	protected Tribu plugin;

	protected Location pos;

	public TribuSign(Tribu plugin) {
		this.plugin = plugin;
	}

	public TribuSign(Tribu plugin, Location pos) {
		this.plugin = plugin;
		this.pos = pos;

	}

	public TribuSign(Tribu plugin, Location pos, String[] lines) {
		this.plugin = plugin;
		this.pos = pos;
	}

	public String[] getLines() {
		String[] lines = getSpecificLines();
		if (this instanceof ShopSign)
			lines[0] = plugin.getLocale("Sign.Buy");
		else if (this instanceof TopNamesSign)
			lines[0] = plugin.getLocale("Sign.HighscoreNames");
		else if (this instanceof TopPointsSign)
			lines[0] = plugin.getLocale("Sign.HighscorePoints");
		else if (this instanceof SpawnControlSign)
			lines[0] = plugin.getLocale("Sign.Spawner");
		else if (this instanceof SpawnControlToggleSign)
			lines[0] = plugin.getLocale("Sign.ToggleSpawner");
		else if (this instanceof TollSign)
			lines[0] = plugin.getLocale("Sign.TollSign");

		return lines;
	}

	public Location getLocation() {
		return pos;
	}

	protected abstract String[] getSpecificLines();

	public abstract void init();

	public boolean isHere(Location position) {
		return pos.equals(position);
	}

	public abstract boolean isUsedEvent(Event e);

	public abstract void raiseEvent(Event e);

	public void SaveToStream(DataOutputStream stream) {
		try {

			stream.writeDouble(pos.getX());
			stream.writeDouble(pos.getY());
			stream.writeDouble(pos.getZ());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void update() {
		Sign s = ((Sign) pos.getBlock().getState());
		String[] lines = getLines();
		for (byte i = 0; i < 4; i++)
			s.setLine(i, lines[i]);
		s.update();
	}

}
