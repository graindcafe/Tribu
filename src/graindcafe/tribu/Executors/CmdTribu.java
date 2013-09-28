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

import graindcafe.tribu.Package;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.Configuration.CLIReader;
import graindcafe.tribu.Configuration.Constants;
import graindcafe.tribu.Player.PlayerStats;
import graindcafe.tribu.Signs.TribuSign;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTribu implements CommandExecutor {
	// use to confirm deletion of a level
	private String deletedLevel = "";
	private Package pck = null;
	private final Tribu game;

	public CmdTribu(final Tribu instance) {
		game = instance;
	}

	// usage: /tribu ((create | load | delete) <name>) | enter | leave | package
	// (create |delete | list)
	// list | start [<name>] | stop | save | stats
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {
		if (args.length == 0)
			return usage(sender);
		args[0] = args[0].toLowerCase();

		/*
		 * Players commands
		 */

		if (args[0].equalsIgnoreCase("enter")
				|| args[0].equalsIgnoreCase("join")) {
			if (!game.config().PluginModeServerExclusive || sender.isOp())
				if (!sender.hasPermission("tribu.use.enter"))
					Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				else if (!(sender instanceof Player))
					game.LogWarning(game
							.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				else if (!game.isPlaying((Player) sender))
					game.addPlayer((Player) sender);
				else
					Tribu.messagePlayer(sender,
							game.getLocale("Message.AlreadyIn"));
			return true;
		} else if (args[0].equals("leave")) {
			if (!game.config().PluginModeServerExclusive || sender.isOp())
				if (!sender.hasPermission("tribu.use.leave"))
					Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				else if (!(sender instanceof Player))
					game.LogWarning(game
							.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				else
					game.removePlayer((Player) sender);
			// add in them to change to main world (world when they leave the
			// game);
			return true;
		} else if (args[0].equals("stats")) {
			if (!sender.hasPermission("tribu.use.stats"))
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
			else {
				final LinkedList<PlayerStats> stats = game.getSortedStats();
				Tribu.messagePlayer(sender, game.getLocale("Message.Stats"));
				final Iterator<PlayerStats> i = stats.iterator();
				String s;
				PlayerStats cur;
				while (i.hasNext()) {
					s = "";
					for (byte j = 0; i.hasNext() && j < 3; j++) {
						cur = i.next();
						s += ", " + cur.getPlayer().getDisplayName() + " ("
								+ String.valueOf(cur.getPoints()) + ")";
					}

					Tribu.messagePlayer(sender, s.substring(2));
				}
			}
			return true;
		} else if (args[0].equals("vote")) {
			if (!sender.hasPermission("tribu.use.vote"))
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
			else {
				if (!(sender instanceof Player)) {
					game.LogWarning(game
							.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
					return true;
				}

				if (args.length == 2) {
					try {
						game.getLevelSelector().castVote((Player) sender,
								Integer.parseInt(args[1]));
					} catch (final NumberFormatException e) {
						Tribu.messagePlayer(sender,
								game.getLocale("Message.InvalidVote"));
					}
					return true;
				}
			}
		} else if (args[0].equals("vote1")) {
			if (!sender.hasPermission("tribu.use.vote"))
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
			else {
				if (!(sender instanceof Player)) {
					game.LogWarning(game
							.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
					return true;
				}

				game.getLevelSelector().castVote((Player) sender, 1);
			}
			return true;

		} else if (args[0].equals("vote2")) {
			if (!sender.hasPermission("tribu.use.vote"))
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
			else {
				if (!(sender instanceof Player)) {
					game.LogWarning(game
							.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
					return true;
				}

				game.getLevelSelector().castVote((Player) sender, 2);
			}
			return true;

		}
		/*
		 * Ops commands
		 */
		/* Package management */
		else if (args[0].equals("package") || args[0].equals("pck")) {
			if (!sender.hasPermission("tribu.level.package")) {
				sender.sendMessage(game.getLocale("Message.Deny"));
				return true;
			} else if (args.length == 1)
				return usage(sender);
			if (game.getLevel() == null) {
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded"));
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded2"));
				return true;
			}
			args[1] = args[1].toLowerCase();

			if (args[1].equals("new") || args[1].equals("create")) {
				if (args.length == 2)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedName"));
				else {
					pck = new Package(args[2]);
					Tribu.messagePlayer(sender, String.format(
							game.getLocale("Message.PckCreated"), args[2]));
				}

			} else if (args[1].equals("open")) {
				if (args.length == 2)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedName"));
				else {
					pck = game.getLevel().getPackage(args[2]);
					if (pck != null)
						Tribu.messagePlayer(sender, String.format(
								game.getLocale("Message.PckOpened"), args[2]));
					else
						Tribu.messagePlayer(sender, String.format(
								game.getLocale("Message.PckNotFound"), args[2]));
				}

			} else if (args[1].equals("close") || args[1].equals("save")) {
				if (pck == null)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedOpen"));
				else {
					game.getLevel().addPackage(pck);
					game.getLevel().setChanged();
					Tribu.messagePlayer(sender, String.format(
							game.getLocale("Message.PckSaved"), pck.getName()));
					pck = null;
				}

			} else if (args[1].equals("add")) {
				boolean success = false;

				if (pck == null)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedOpen"));
				else if (args.length == 2)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedId"));
				else {
					if (args.length == 3)
						if (args[2].equalsIgnoreCase("this"))
							if (!(sender instanceof Player)) {
								game.LogWarning(game
										.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
								return true;
							} else
								success = pck.addItem(((Player) sender)
										.getItemInHand().clone());
						else
							success = pck.addItem(args[2]);
					else if (args.length == 4)
						if (args[2].equalsIgnoreCase("this"))
							if (!(sender instanceof Player)) {
								game.LogWarning(game
										.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
								return true;
							} else
								success = pck.addItem(((Player) sender)
										.getItemInHand().clone(),
										(short) TribuSign.parseInt(args[3]));
						else
							success = pck.addItem(args[2],
									(short) TribuSign.parseInt(args[3]));
					else
						success = pck.addItem(args[2],
								(short) TribuSign.parseInt(args[3]),
								(short) TribuSign.parseInt(args[4]));
					if (success)
						Tribu.messagePlayer(sender, String.format(
								game.getLocale("Message.PckItemAdded"),
								pck.getLastItemName()));
					else
						Tribu.messagePlayer(sender, String.format(
								game.getLocale("Message.PckItemAddFailed"),
								args[2]));
				}
			} else if (args[1].equals("del") || args[1].equals("delete")) {
				if (pck == null)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedOpen"));
				else if (args.length == 4
						&& pck.deleteItem(TribuSign.parseInt(args[2]),
								(short) TribuSign.parseInt(args[3])))
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckItemDeleted"));
				else if (args.length == 3) {
					if (args[2].equals("this")) {
						if (!(sender instanceof Player)) {
							game.LogWarning(game
									.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
							return true;
						} else if (pck.deleteItem(((Player) sender)
								.getItemInHand().getTypeId(), ((Player) sender)
								.getItemInHand().getData().getData())
								|| pck.deleteItem(((Player) sender)
										.getItemInHand().getTypeId()))
							Tribu.messagePlayer(sender,
									game.getLocale("Message.PckItemDeleted"));
						else
							Tribu.messagePlayer(sender,
									game.getLocale("Message.PckNeedSubId"));
					} else if (pck.deleteItem(TribuSign.parseInt(args[2])))
						Tribu.messagePlayer(sender,
								game.getLocale("Message.PckItemDeleted"));
					else
						Tribu.messagePlayer(sender,
								game.getLocale("Message.PckNeedSubId"));

				} else
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedSubId"));

			} else if (args[1].equals("remove")) {
				if (args.length < 3)
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckNeedName"));
				else {
					game.getLevel().removePackage(args[2]);
					Tribu.messagePlayer(sender,
							game.getLocale("Message.PckRemoved"));
					pck = null;
				}
			} else if (args[1].equals("list"))
				Tribu.messagePlayer(sender, String.format(game
						.getLocale("Message.PckList"), game.getLevel()
						.listPackages()));
			else if (args[1].equals("show") || args[1].equals("describe")) {
				if (game.getLevel() == null) {
					Tribu.messagePlayer(sender,
							game.getLocale("Message.NoLevelLoaded"));
					Tribu.messagePlayer(sender,
							game.getLocale("Message.NoLevelLoaded2"));
					return true;
				}
				Package p = pck;
				if (args.length > 2)
					p = game.getLevel().getPackage(args[2]);
				if (p != null)
					Tribu.messagePlayer(sender, p.toString());
				else
					Tribu.messagePlayer(sender, String.format(
							game.getLocale("Message.PckNotFound"),
							args.length > 2 ? args[2] : game
									.getLocale("Message.PckNoneOpened")));
			} else
				return usage(sender);
			return true;
		}
		/*
		 * Level management
		 */
		else if (args[0].equals("new") || args[0].equals("create")) {
			if (!sender.hasPermission("tribu.level.create")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1)
				return usage(sender);

			if (!(sender instanceof Player)) {
				game.LogWarning(game
						.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				return true;
			}
			final Player player = (Player) sender;
			if (!game.getLevelLoader().saveLevel(game.getLevel())) {
				Tribu.messagePlayer(sender,
						game.getLocale("Message.UnableToSaveCurrentLevely"));
				return true;
			}

			game.setLevel(game.getLevelLoader().newLevel(args[1],
					player.getLocation()));
			player.sendMessage(String.format(
					game.getLocale("Message.LevelCreated"), args[1]));

			return true;
		} else if (args[0].equals("delete") || args[0].equals("remove")) {
			if (!sender.hasPermission("tribu.level.delete")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1)
				return usage(sender);
			else if (!game.getLevelLoader().exists(args[1])) {
				Tribu.messagePlayer(sender, String.format(
						game.getLocale("Message.UnknownLevel"), args[1]));
				Tribu.messagePlayer(sender,
						game.getLocale("Message.MaybeNotSaved"));
				return true;
			} else if (!deletedLevel.equals(args[1])) {
				deletedLevel = args[1];
				Tribu.messagePlayer(sender, String.format(
						game.getLocale("Message.ConfirmDeletion"), args[1]));
				Tribu.messagePlayer(sender,
						game.getLocale("Message.ThisOperationIsNotCancellable"));
				return true;
			} else {
				if (!game.getLevelLoader().deleteLevel(args[1]))
					Tribu.messagePlayer(sender,
							game.getLocale("Message.UnableToDeleteLevel"));
				else
					Tribu.messagePlayer(sender,
							game.getLocale("Message.LevelDeleted"));
				return true;
			}
		} else if (args[0].equals("save") || args[0].equals("close")) {
			if (!sender.hasPermission("tribu.level.save")) {
				sender.sendMessage(game.getLocale("Message.Deny"));
				return true;
			}
			if (game.getLevel() != null)
				game.getLevel().addPackage(pck);
			if (!game.getLevelLoader().saveLevel(game.getLevel()))
				Tribu.messagePlayer(sender,
						game.getLocale("Message.UnableToSaveCurrentLevel"));
			else
				Tribu.messagePlayer(sender,
						game.getLocale("Message.LevelSaveSuccessful"));
			return true;

		} else if (args[0].equals("load") || args[0].equals("open")) {
			if (!sender.hasPermission("tribu.level.load")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1)
				return usage(sender);
			else {
				game.getLevelSelector().ChangeLevel(args[1],
						sender instanceof Player ? (Player) sender : null);
				return true;
			}
		} else if (args[0].equals("unload")) {
			if (!sender.hasPermission("tribu.level.unload")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			game.setLevel(null);
			Tribu.messagePlayer(sender, game.getLocale("Message.LevelUnloaded"));
			return true;

		} else if (args[0].equals("list")) {
			final Set<String> levels = game.getLevelLoader().getLevelList();
			String msg = "";
			for (final String level : levels)
				msg += ", " + level;
			if (msg != "")
				Tribu.messagePlayer(
						sender,
						String.format(game.getLocale("Message.Levels"),
								msg.substring(2)));
			return true;
		}
		/*
		 * Game management
		 */
		else if (args[0].equals("start")) {
			if (!sender.hasPermission("tribu.game.start")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			// if a level is given, load it before start
			if (args.length > 1 && game.getLevelLoader().exists(args[1]))
				game.getLevelSelector().ChangeLevel(args[1],
						sender instanceof Player ? (Player) sender : null);
			else if (game.getLevel() == null) {
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded"));
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded2"));
				return true;
			}
			game.getLevelSelector().cancelVote();
			game.setForceStop(false);
			if (game.startRunning())
				Tribu.messagePlayer(sender,
						game.getLocale("Message.ZombieModeEnabled"));
			else
				Tribu.messagePlayer(sender,
						game.getLocale("Message.LevelNotReady"));
			return true;
		} else if (args[0].equals("stop")) {
			if (!sender.hasPermission("tribu.game.stop")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			game.setForceStop(true);
			game.stopRunning();
			Tribu.messagePlayer(sender,
					game.getLocale("Message.ZombieModeDisabled"));
			return true;
		} else if (args[0].equals("forcestart")) {
			if (!sender.hasPermission("tribu.debug.forcestart")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}

			for (final String msg : game.whyNotStarting())
				Tribu.messagePlayer(sender, msg);
			if (game.forceStart())
				Tribu.messagePlayer(sender,
						game.getLocale("Message.ZombieModeEnabled"));
			return true;
		} else if (args[0].equals("tpfz")) {
			if (!sender.hasPermission("tribu.debug.tpfz"))
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
			else {
				final Location loc = game.getSpawner().getFirstZombieLocation();
				if (loc != null)
					if (sender instanceof Player)
						((Player) sender).teleport(loc);
					else if (args.length > 1)
						game.getPlugin().getServer().getPlayer(args[1])
								.teleport(loc);
			}
			return true;

		} else if (args[0].equals("reload")) {
			if (sender.hasPermission("tribu.plugin.reload")) {
				game.reloadConf();
				Tribu.messagePlayer(sender,
						game.getLocale("Message.ConfigFileReloaded"));
			}
			return true;
		} else if (args[0].equals("set")) {
			if (!sender.hasPermission("tribu.level.set")) {
				Tribu.messagePlayer(sender, game.getLocale("Message.Deny"));
				return true;
			}
			if (args.length < 3)
				return usage(sender);
			if (game.getLevel() == null) {
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded"));
				Tribu.messagePlayer(sender,
						game.getLocale("Message.NoLevelLoaded2"));
				return true;
			}
			game.config().load(args[1], new CLIReader(args[2]));
			try {
				game.config().save(
						new File(Constants.perLevelFolder
								+ game.getLevel().getName() + ".yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (args[0].equals("help") || args[0].equals("?")
				|| args[0].equals("aide")) {

			if (sender.isOp()) {
				Tribu.messagePlayer(
						sender,
						"There are 4 commands : /zspawn (setting zombie spawns) /ispawn (setting initial spawn) /dspawn (setting death spawn) /tribu.");
				Tribu.messagePlayer(sender,
						"This is the /tribu command detail :");
			}
			return usage(sender);

		}
		return usage(sender);

	}

	private boolean usage(final CommandSender sender) {
		if (sender.isOp()) {
			Tribu.messagePlayer(sender, ChatColor.LIGHT_PURPLE
					+ "Ops commands :");
			Tribu.messagePlayer(
					sender,
					ChatColor.YELLOW
							+ "/tribu ((create | load | delete) <name>) | save | list | set <configNode> <value>");
			Tribu.messagePlayer(sender, ChatColor.YELLOW
					+ "/tribu start [<name>] | stop");
			Tribu.messagePlayer(
					sender,
					ChatColor.YELLOW
							+ "/tribu package ((add | del)  <id>  [<subid>] [<number>]) | ((new | open | remove) <name> | close) | list");
			Tribu.messagePlayer(sender, ChatColor.YELLOW
					+ "See also /ispawn /dspawn /zspawn");
			Tribu.messagePlayer(sender, ChatColor.YELLOW + "Players commands :");

		}
		return false;

	}
}
