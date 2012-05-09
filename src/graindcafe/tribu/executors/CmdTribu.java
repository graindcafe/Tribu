package graindcafe.tribu.executors;

import graindcafe.tribu.Package;
import graindcafe.tribu.PlayerStats;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.signs.TribuSign;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdTribu implements CommandExecutor {
	// use to confirm deletion of a level
	private String deletedLevel = "";
	private Package pck = null;
	private Tribu plugin;

	public CmdTribu(Tribu instance) {
		plugin = instance;
	}

	// usage: /tribu ((create | load | delete) <name>) | enter | leave | package
	// (create |delete | list)
	// list | start [<name>] | stop | save | stats
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return usage(sender);
		}
		args[0] = args[0].toLowerCase();

		/*
		 * Players commands
		 */

		if (args[0].equalsIgnoreCase("enter") || args[0].equalsIgnoreCase("join")) {
			if (!plugin.isDedicatedServer() || sender.isOp())
				if (!sender.hasPermission("tribu.use.enter"))
					sender.sendMessage(plugin.getLocale("Message.Deny"));
				else if (!(sender instanceof Player)) {
					plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));

				} else {
					sender.sendMessage(plugin.getLocale("Message.YouJoined"));
					plugin.addPlayer((Player) sender);
				}
			return true;
		} else if (args[0].equals("leave")) {
			if (!plugin.isDedicatedServer() || sender.isOp())
				if (!sender.hasPermission("tribu.use.leave"))
					sender.sendMessage(plugin.getLocale("Message.Deny"));
				else if (!(sender instanceof Player)) {
					plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));

				} else {
					sender.sendMessage(plugin.getLocale("Message.YouLeft"));
					plugin.removePlayer((Player) sender);
				}
			return true;
		} else if (args[0].equals("stats")) {
			if (!sender.hasPermission("tribu.use.stats"))
				sender.sendMessage(plugin.getLocale("Message.Deny"));
			else {
				LinkedList<PlayerStats> stats = plugin.getSortedStats();
				plugin.Message(sender, plugin.getLocale("Message.Stats"));
				Iterator<PlayerStats> i = stats.iterator();
				String s;
				PlayerStats cur;
				while (i.hasNext()) {
					s = "";
					for (byte j = 0; i.hasNext() && j < 3; j++) {
						cur = i.next();
						s += ", " + cur.getPlayer().getDisplayName() + " (" + String.valueOf(cur.getPoints()) + ")";
					}

					plugin.Message(sender, s.substring(2));
				}
			}
			return true;
		} else if (args[0].equals("vote")) {
			if (!sender.hasPermission("tribu.use.vote"))
				sender.sendMessage(plugin.getLocale("Message.Deny"));
			else {
				if (!(sender instanceof Player)) {
					plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
					return true;
				}

				if (args.length == 2) {
					try {
						plugin.getLevelSelector().castVote((Player) sender, Integer.parseInt(args[1]));
					} catch (NumberFormatException e) {
						sender.sendMessage(plugin.getLocale("Message.InvalidVote"));
					}
					return true;
				}
			}
		} else if (args[0].equals("vote1")) {
			if(!sender.hasPermission("tribu.use.vote"))
				sender.sendMessage(plugin.getLocale("Message.Deny"));
			else
			{
			if (!(sender instanceof Player)) {
				plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				return true;
			}

			plugin.getLevelSelector().castVote((Player) sender, 1);
			}
			return true;

		} else if (args[0].equals("vote2")) {
			if(!sender.hasPermission("tribu.use.vote"))
				sender.sendMessage(plugin.getLocale("Message.Deny"));
			else
			{
			if (!(sender instanceof Player)) {
				plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				return true;
			}

			plugin.getLevelSelector().castVote((Player) sender, 2);
			}
			return true;

		}
		/*
		 * Ops commands
		 */
		/* Package management */
		else if (args[0].equals("package") || args[0].equals("pck")) {
			if(!sender.hasPermission("tribu.level.package"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			else
			if (args.length == 1) {
				return usage(sender);
			}
			if (plugin.getLevel() == null) {
				plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded"));
				plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded2"));
				return true;
			}
			args[1] = args[1].toLowerCase();

			if (args[1].equals("new") || args[1].equals("create")) {
				if (args.length == 2) {
					plugin.Message(sender, plugin.getLocale("Message.PckNeedName"));
				} else {
					pck = new Package(args[2]);
					plugin.Message(sender, String.format(plugin.getLocale("Message.PckCreated"), args[2]));
				}

			} else if (args[1].equals("open")) {
				if (args.length == 2) {
					plugin.Message(sender, plugin.getLocale("Message.PckNeedName"));
				} else {
					pck = plugin.getLevel().getPackage(args[2]);
					if (pck != null)
						plugin.Message(sender, String.format(plugin.getLocale("Message.PckOpened"), args[2]));
					else
						plugin.Message(sender, String.format(plugin.getLocale("Message.PckNotFound"), args[2]));
				}

			} else if (args[1].equals("close") || args[1].equals("save")) {
				if (pck == null) {
					plugin.Message(sender, plugin.getLocale("Message.PckNeedOpen"));
				} else {
					plugin.getLevel().addPackage(pck);
					plugin.getLevel().setChanged();
					plugin.Message(sender, String.format(plugin.getLocale("Message.PckSaved"), pck.getName()));
					pck = null;
				}

			} else if (args[1].equals("add")) {
				boolean success = false;

				if (pck == null)
					plugin.Message(sender, plugin.getLocale("Message.PckNeedOpen"));
				else if (args.length == 2)
					plugin.Message(sender, plugin.getLocale("Message.PckNeedId"));
				else {
					if (args.length == 3)
						if (args[2].equalsIgnoreCase("this"))
							if (!(sender instanceof Player)) {
								plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
								return true;
							} else
								success = pck.addItem(((Player) sender).getItemInHand().clone());
						else
							success = pck.addItem(args[2]);
					else if (args.length == 4)
						if (args[2].equalsIgnoreCase("this"))
							if (!(sender instanceof Player)) {
								plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
								return true;
							} else
								success = pck.addItem(((Player) sender).getItemInHand().clone(), (short) TribuSign.parseInt(args[3]));
						else
							success = pck.addItem(args[2], (short) TribuSign.parseInt(args[3]));
					else
						success = pck.addItem(args[2], (short) TribuSign.parseInt(args[3]), (short) TribuSign.parseInt(args[4]));
					if (success)
						plugin.Message(sender, String.format(plugin.getLocale("Message.PckItemAdded"), pck.getLastItemName()));
					else
						plugin.Message(sender, String.format(plugin.getLocale("Message.PckItemAddFailed"), args[2]));
				}
			} else if (args[1].equals("del") || args[1].equals("delete")) {

				if (pck == null)
					plugin.Message(sender, plugin.getLocale("Message.PckNeedOpen"));
				else if (args.length == 4) {
					pck.deleteItem(TribuSign.parseInt(args[2]), (short) TribuSign.parseInt(args[3]));
					plugin.Message(sender, plugin.getLocale("Message.PckItemDeleted"));
				} else if (pck.deleteItem(TribuSign.parseInt(args[2]), (short) TribuSign.parseInt(args[3])))
					plugin.Message(sender, plugin.getLocale("Message.PckItemDeleted"));
				else
					plugin.Message(sender, plugin.getLocale("Message.PckNeedSubId"));

			} else if (args[1].equals("remove")) {
				if (args.length == 3)
					plugin.Message(sender, plugin.getLocale("Message.PckNeedName"));
				else {
					plugin.getLevel().removePackage(args[2]);
					plugin.Message(sender, plugin.getLocale("Message.PckRemoved"));
				}
			} else if (args[1].equals("list")) {
				plugin.Message(sender, String.format(plugin.getLocale("Message.PckList"), plugin.getLevel().listPackages()));
			} else if (args[1].equals("show") || args[1].equals("describe")) {
				if (plugin.getLevel() == null) {
					plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded"));
					plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded2"));
					return true;
				}
				Package p = pck;
				if (args.length > 2)
					p = plugin.getLevel().getPackage(args[2]);
				if (p != null)
					plugin.Message(sender, p.toString());
				else
					plugin.Message(
							sender,
							String.format(plugin.getLocale("Message.PckNotFound"),
									args.length > 2 ? args[2] : plugin.getLocale("Message.PckNoneOpened")));
			} else {
				return usage(sender);
			}
			return true;
		}
		/*
		 * Level management
		 */
		else if (args[0].equals("new") || args[0].equals("create")) {
			if(!sender.hasPermission("tribu.level.create"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1) {
				return usage(sender);
			}

			if (!(sender instanceof Player)) {
				plugin.LogWarning(plugin.getLocale("Warning.ThisCommandCannotBeUsedFromTheConsole"));
				return true;
			}
			Player player = (Player) sender;
			if (!plugin.getLevelLoader().saveLevel(plugin.getLevel())) {
				player.sendMessage(plugin.getLocale("Message.UnableToSaveCurrentLevel"));
				return true;
			}

			plugin.setLevel(plugin.getLevelLoader().newLevel(args[1], player.getLocation()));
			player.sendMessage(String.format(plugin.getLocale("Message.LevelCreated"), args[1]));

			return true;
		} else if (args[0].equals("delete") || args[0].equals("remove")) {
			if(!sender.hasPermission("tribu.level.delete"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1) {
				return usage(sender);
			} else if (!plugin.getLevelLoader().exists(args[1])) {
				sender.sendMessage(String.format(plugin.getLocale("Message.UnknownLevel"), args[1]));
				sender.sendMessage(plugin.getLocale("Message.MaybeNotSaved"));
				return true;
			} else if (!deletedLevel.equals(args[1])) {
				deletedLevel = args[1];
				plugin.Message(sender, String.format(plugin.getLocale("Message.ConfirmDeletion"), args[1]));
				plugin.Message(sender, plugin.getLocale("Message.ThisOperationIsNotCancellable"));
				return true;
			} else {
				if (!plugin.getLevelLoader().deleteLevel(args[1])) {
					plugin.Message(sender, plugin.getLocale("Message.UnableToDeleteLevel"));
				} else {
					plugin.Message(sender, plugin.getLocale("Message.LevelDeleted"));
				}
				return true;
			}
		} else if (args[0].equals("save") || args[0].equals("close")) {
			if(!sender.hasPermission("tribu.level.save"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			if (plugin.getLevel() != null)
				plugin.getLevel().addPackage(pck);
			if (!plugin.getLevelLoader().saveLevel(plugin.getLevel())) {
				plugin.Message(sender, plugin.getLocale("Message.UnableToSaveCurrentLevel"));
			} else {
				plugin.Message(sender, plugin.getLocale("Message.LevelSaveSuccessful"));
			}
			return true;

		} else if (args[0].equals("load") || args[0].equals("open")) {
			if(!sender.hasPermission("tribu.level.load"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			if (args.length == 1) {
				return usage(sender);
			} else {
				plugin.getLevelSelector().ChangeLevel(args[1], sender instanceof Player ? (Player) sender : null);
				return true;
			}
		} else if (args[0].equals("unload")) {
			if(!sender.hasPermission("tribu.level.unload"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			plugin.setLevel(null);
			plugin.Message(sender, plugin.getLocale("Message.LevelUnloaded"));
			return true;

		} else if (args[0].equals("list")) {
			Set<String> levels = plugin.getLevelLoader().getLevelList();
			String msg = "";
			for (String level : levels) {
				msg += ", " + level;
			}
			if (msg != "")
				plugin.Message(sender, String.format(plugin.getLocale("Message.Levels"), msg.substring(2)));
			return true;
		}
		/*
		 * Game management
		 */
		else if (args[0].equals("start")) {
			if(!sender.hasPermission("tribu.game.start"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			// if a level is given, load it before start
			if (args.length > 1 && plugin.getLevelLoader().exists(args[1])) {
				plugin.getLevelSelector().ChangeLevel(args[1], sender instanceof Player ? (Player) sender : null);
			} else if (plugin.getLevel() == null) {
				plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded"));
				plugin.Message(sender, plugin.getLocale("Message.NoLevelLoaded2"));
				return true;
			}
			plugin.getLevelSelector().cancelVote();
			if (plugin.startRunning())
				plugin.Message(sender, plugin.getLocale("Message.ZombieModeEnabled"));
			else
				plugin.Message(sender, plugin.getLocale("Message.LevelNotReady"));
			return true;
		} else if (args[0].equals("stop")) {
			if(!sender.hasPermission("tribu.game.stop"))
			{
				sender.sendMessage(plugin.getLocale("Message.Deny"));
				return true;
			}
			plugin.stopRunning();
			plugin.Message(sender, plugin.getLocale("Message.ZombieModeDisabled"));
			return true;
		} else if (args[0].equals("tpfz")) {
			Location loc = plugin.getSpawner().getFirstZombieLocation();
			if (loc != null)
				if (sender instanceof Player)
					((Player) sender).teleport(loc);
				else if (args.length > 1)
					plugin.getServer().getPlayer(args[1]).teleport(loc);
			return true;

		}else if (args[0].equals("reload")) {
			if(sender.hasPermission("tribu.plugin.reload"))
				plugin.reloadConf();
			return true;

		} else if (args[0].equals("help") || args[0].equals("?") || args[0].equals("aide")) {

			if (sender.isOp()) {
				sender.sendMessage("There are 4 commands : /zspawn (setting zombie spawns) /ispawn (setting initial spawn) /dspawn (setting death spawn) /tribu.");
				sender.sendMessage("This is the /tribu command detail :");
			}
			return usage(sender);

		}
		return usage(sender);

	}

	private boolean usage(CommandSender sender) {
		if (sender.isOp()) {
			sender.sendMessage("Ops commands :");
			sender.sendMessage("/tribu ((create | load | delete) <name>) | save | list | start [<name>] | stop");
			sender.sendMessage("/tribu package ((add | del)  <id>  [<subid>] [<number>]) | ((new | open | remove) <name> | close) | list");
			sender.sendMessage("See also /ispawn /dspawn /zspawn");
			sender.sendMessage("Players commands :");
		}
		return false;

	}
}
