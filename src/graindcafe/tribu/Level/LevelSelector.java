package graindcafe.tribu.Level;

import graindcafe.tribu.Tribu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LevelSelector implements Runnable {
	private Tribu plugin;
	private String randomLevel1;
	private String randomLevel2;
	private Random rnd;
	private int taskID;
	private HashMap<Player, Integer> votes;
	private boolean votingEnabled;

	public LevelSelector(Tribu instance) {
		plugin = instance;
		taskID = -1;
		rnd = new Random();
		votes = new HashMap<Player, Integer>();
		votingEnabled = false;
	}

	public void cancelVote() {
		if (taskID >= 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
		}
	}

	public void castVote(Player player, int v) {
		if (votingEnabled) {

			if (v > 2 || v < 1) {
				player.sendMessage(plugin.getLocale("Message.InvalidVote"));
				return;
			}

			votes.put(player, v);
			player.sendMessage(plugin.getLocale("Message.ThankyouForYourVote"));
			// if all players have voted
			if (votes.size() == plugin.getPlayersCount()) {
				cancelVote();
				run();
			}
		} else {
			player.sendMessage(plugin.getLocale("Message.YouCannotVoteAtThisTime"));
		}
	}

	public void ChangeLevel(String name, Player player) {
		if (plugin.getLevel() != null) {
			if (plugin.getLevel().getName().equalsIgnoreCase(name)) {
				Tribu.messagePlayer(player, String.format(plugin.getLocale("Message.LevelIsAlreadyTheCurrentLevel"), name),ChatColor.YELLOW);
				return;
			}
		}

		cancelVote();
		boolean restart = false;
		if (plugin.isRunning()) {
			restart = true;
		}

		plugin.stopRunning();

		TribuLevel temp = plugin.getLevelLoader().loadLevelIgnoreCase(name);

		if (!plugin.getLevelLoader().saveLevel(plugin.getLevel())) {
			if (player != null) {
				player.sendMessage(plugin.getLocale("Message.UnableToSaveLevel"));
			} else {
				plugin.LogWarning(ChatColor.stripColor(plugin.getLocale("Message.UnableToSaveLevel")));
			}
			return;
		}

		if (temp == null) {
			if (player != null) {
				player.sendMessage(plugin.getLocale("Message.UnableToLoadLevel"));
			} else {
				plugin.LogWarning(ChatColor.stripColor(plugin.getLocale("Message.UnableToLoadLevel")));
			}
			return;
		} else {
			if (player != null) {
				player.sendMessage(plugin.getLocale("Message.LevelLoadedSuccessfully"));
			} else {
				plugin.LogInfo(ChatColor.stripColor(plugin.getLocale("Message.LevelLoadedSuccessfully")));
			}
		}

		plugin.setLevel(temp);
		if (restart) {
			plugin.startRunning();
		}

	}

	@Override
	public void run() {
		taskID = -1;
		votingEnabled = false;
		int[] voteCounts = new int[2];
		Collection<Integer> nums = votes.values();
		for (int vote : nums) {
			voteCounts[vote - 1]++;
		}
		votes.clear();
		if (voteCounts[0] >= voteCounts[1]) {
			ChangeLevel(randomLevel1, null);
			plugin.getServer().broadcastMessage(String.format(plugin.getLocale("Broadcast.MapChosen"), randomLevel1));
		} else {
			ChangeLevel(randomLevel2, null);
			plugin.getServer().broadcastMessage(String.format(plugin.getLocale("Broadcast.MapChosen"), randomLevel2));
		}
		plugin.startRunning();
	}

	public void startVote(int duration) {
		String[] levels = plugin.getLevelLoader().getLevelList().toArray(new String[0]);

		if (levels.length < 2) { // Skip voting since there's only one option
			plugin.startRunning();
			return;
		}
		taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, duration);
		votingEnabled = true;

		do {
			randomLevel1 = levels[rnd.nextInt(levels.length)];
		} while (randomLevel1 == plugin.getLevel().getName());

		if (levels.length >= 3) {
			do {
				randomLevel2 = levels[rnd.nextInt(levels.length)];
			} while (randomLevel2 == plugin.getLevel().getName() || randomLevel2 == randomLevel1);
		} else {
			randomLevel2 = plugin.getLevel().getName();
		}
		plugin.getServer().broadcastMessage(plugin.getLocale("Broadcast.MapVoteStarting"));
		plugin.getServer().broadcastMessage(plugin.getLocale("Broadcast.Type"));
		plugin.getServer().broadcastMessage(String.format(plugin.getLocale("Broadcast.SlashVoteForMap"), '1', randomLevel1));
		plugin.getServer().broadcastMessage(String.format(plugin.getLocale("Broadcast.SlashVoteForMap"), '2', randomLevel2));
		plugin.getServer().broadcastMessage(String.format(plugin.getLocale("Broadcast.VoteClosingInSeconds"), String.valueOf(duration / 20)));

	}

}
