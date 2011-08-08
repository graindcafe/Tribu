package graindcafe.tribu.signs;

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
		if(Sign.class.isInstance(pos.getBlock().getState()))
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

		return ret;
	}

	public static boolean isIt(Tribu plugin, Block b) {
		if(Sign.class.isInstance(b.getState()))
			return isIt(plugin, ((Sign) b.getState()).getLines());
		return false;
	}

	public static boolean isIt(Tribu plugin, String[] lines) {
		return lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Buy")) || lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscoreNames"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.HighscorePoints")) || lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Spawner"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.ToggleSpawner"));
	}

	public static TribuSign getObject(Tribu plugin, Sign sign) {
		return getObject(plugin, sign.getBlock().getLocation(), sign.getLines());
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

	protected Location pos;

	protected Tribu plugin;

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

	public Location getLocation() {
		return pos;
	}

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

}
