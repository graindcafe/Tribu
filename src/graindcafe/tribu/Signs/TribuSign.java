/*******************************************************************************
 * Copyright or � or Copr. Quentin Godron (2011)
 * 
 * cafe.en.grain@gmail.com
 * 
 * This software is a computer program whose purpose is to create zombie 
 * survival games on Bukkit's server. 
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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

	public static TribuSign getObject(final Tribu plugin, final Location pos) {
		if (Sign.class.isInstance(pos.getBlock().getState()))
			return getObject(plugin, pos,
					((Sign) pos.getBlock().getState()).getLines());
		else
			return null;

	}

	public static TribuSign getObject(final Tribu plugin, final Location pos,
			final String[] lines) {

		TribuSign ret = null;
		if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Buy")))
			ret = new ShopSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin
				.getLocale("Sign.HighscoreNames")))
			ret = new TopNamesSign(plugin, pos);
		else if (lines[0].equalsIgnoreCase(plugin
				.getLocale("Sign.HighscorePoints")))
			ret = new TopPointsSign(plugin, pos);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Spawner")))
			ret = new SpawnControlSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin
				.getLocale("Sign.ToggleSpawner")))
			ret = new SpawnControlToggleSign(plugin, pos, lines);
		else if (lines[0].equalsIgnoreCase(plugin.getLocale("Sign.TollSign")))
			ret = new TollSign(plugin, pos, lines);

		return ret;
	}

	public static TribuSign getObject(final Tribu plugin, final Sign sign) {
		return getObject(plugin, sign.getBlock().getLocation(), sign.getLines());
	}

	public static boolean isIt(final Tribu plugin, final Block b) {
		if (Sign.class.isInstance(b.getState()))
			return isIt(plugin, ((Sign) b.getState()).getLines());
		return false;
	}

	public static boolean isIt(final Tribu plugin, final String[] lines) {
		return lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Buy"))
				|| lines[0].equalsIgnoreCase(plugin
						.getLocale("Sign.HighscoreNames"))
				|| lines[0].equalsIgnoreCase(plugin
						.getLocale("Sign.HighscorePoints"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.Spawner"))
				|| lines[0].equalsIgnoreCase(plugin
						.getLocale("Sign.ToggleSpawner"))
				|| lines[0].equalsIgnoreCase(plugin.getLocale("Sign.TollSign"));
	}

	public static TribuSign LoadFromStream(final Tribu plugin,
			final World world, final DataInputStream stream) {
		try {
			final Location pos = new Location(world, stream.readDouble(),
					stream.readDouble(), stream.readDouble());
			return getObject(plugin, pos);

		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int parseInt(final String s) {
		int num = 0;
		for (final char c : s.toCharArray())
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
		return num;
	}

	public static void update(final Sign s) {
		final String[] lines = s.getLines();
		for (byte i = 0; i < lines.length; i++)
			s.setLine(i, lines[i]);
		s.update();

	}

	protected Tribu plugin;

	protected Location pos;

	public TribuSign(final Tribu plugin) {
		this.plugin = plugin;
	}

	public TribuSign(final Tribu plugin, final Location pos) {
		this.plugin = plugin;
		this.pos = pos;

	}

	public TribuSign(final Tribu plugin, final Location pos,
			final String[] lines) {
		this.plugin = plugin;
		this.pos = pos;
	}

	public abstract void finish();

	public String[] getLines() {
		final String[] lines = getSpecificLines();
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

	public boolean isHere(final Location position) {
		return pos.equals(position);
	}

	public abstract boolean isUsedEvent(Event e);

	public abstract void raiseEvent(Event e);

	public void SaveToStream(final DataOutputStream stream) {
		try {

			stream.writeDouble(pos.getX());
			stream.writeDouble(pos.getY());
			stream.writeDouble(pos.getZ());
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void update() {
		final Sign s = ((Sign) pos.getBlock().getState());
		final String[] lines = getLines();
		for (byte i = 0; i < 4; i++)
			s.setLine(i, lines[i]);
		s.update();
	}

}
