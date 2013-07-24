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
package graindcafe.tribu.Executors;

import graindcafe.tribu.Tribu;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdZspawn implements CommandExecutor {
	private final Tribu plugin;

	public CmdZspawn(final Tribu instance) {
		plugin = instance;
	}

	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if (!sender.hasPermission("tribu.level.zspawn")) {
			Tribu.messagePlayer(sender, plugin.getLocale("Message.Deny"));
			return true;
		}

		if (!(sender instanceof Player)) {
			plugin.LogWarning(plugin
					.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
			return true;
		}
		final Player player = (Player) sender;

		// Make sure a level is loaded
		if (plugin.getLevel() == null) {
			Tribu.messagePlayer(player,
					plugin.getLocale("Message.NoLevelLoaded"));
			Tribu.messagePlayer(player,
					plugin.getLocale("Message.NoLevelLoaded2"));
			return true;
		}

		if (args.length == 2) {
			// args[0]=args[0].toLowerCase();
			if (args[0].equalsIgnoreCase("set")) {

				plugin.getLevel().addZombieSpawn(player.getLocation(), args[1]);
				Tribu.messagePlayer(player,
						plugin.getLocale("Message.SpawnpointAdded"));
				return true;

			} else if (args[0].equalsIgnoreCase("remove")) {

				plugin.getLevel().removeZombieSpawn(args[1]);
				Tribu.messagePlayer(player,
						plugin.getLocale("Message.SpawnpointRemoved"));
				return true;

			} else if (args[0].equalsIgnoreCase("jump")) {

				final Location zspawn = plugin.getLevel().getZombieSpawn(
						args[1]);
				if (zspawn != null) {
					player.teleport(zspawn);
					Tribu.messagePlayer(
							player,
							String.format(
									plugin.getLocale("Message.TeleportedToZombieSpawn"),
									args[1]));
				} else
					Tribu.messagePlayer(player,
							plugin.getLocale("Message.InvalidSpawnName"));
				return true;

			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

			plugin.getLevel().listZombieSpawns(player);
			return true;
		}

		return false;
	}

}
