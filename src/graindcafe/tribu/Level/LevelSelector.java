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
package graindcafe.tribu.Level;

import graindcafe.tribu.Tribu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LevelSelector implements Runnable {
	private final Tribu game;
	private String randomLevel1;
	private String randomLevel2;
	private final Random rnd;
	private int taskID;
	private final HashMap<Player, Integer> votes;
	private boolean votingEnabled;

	public LevelSelector(final Tribu instance) {
		game = instance;
		taskID = -1;
		rnd = new Random();
		votes = new HashMap<Player, Integer>();
		votingEnabled = false;
	}

	public void cancelVote() {
		if (taskID >= 0)
			game.getPlugin().getServer().getScheduler().cancelTask(taskID);
		votingEnabled = false;
	}

	public void castVote(final Player player, final int v) {
		if (votingEnabled && game.isPlaying(player)) {

			if (v > 2 || v < 1) {
				Tribu.messagePlayer(player,
						game.getLocale("Message.InvalidVote"));
				return;
			}

			votes.put(player, v);
			Tribu.messagePlayer(player,
					game.getLocale("Message.ThankyouForYourVote"));
			// if all players have voted
			if (votes.size() == game.getPlayersCount()) {
				cancelVote();
				run();
			}
		} else
			Tribu.messagePlayer(player,
					game.getLocale("Message.YouCannotVoteAtThisTime"));
	}

	public void ChangeLevel(final String name, final Player player) {
		if (game.getLevel() != null)
			if (game.getLevel().getName().equalsIgnoreCase(name)) {
				Tribu.messagePlayer(
						player,
						String.format(
								game.getLocale("Message.LevelIsAlreadyTheCurrentLevel"),
								name));
				return;
			}

		cancelVote();
		boolean restart = false;
		if (game.isRunning()) {
			restart = true;
			game.stopRunning(true);
		}

		final TribuLevel temp = game.getLevelLoader().loadLevelIgnoreCase(name);

		if (!game.getLevelLoader().saveLevel(game.getLevel())) {
			if (player != null)
				Tribu.messagePlayer(player,
						game.getLocale("Message.UnableToSaveLevel"));
			else
				game.LogWarning(ChatColor.stripColor(game
						.getLocale("Message.UnableToSaveLevel")));
			return;
		}

		if (temp == null) {
			if (player != null)
				Tribu.messagePlayer(player,
						game.getLocale("Message.UnableToLoadLevel"));
			else
				game.LogWarning(ChatColor.stripColor(game
						.getLocale("Message.UnableToLoadLevel")));
			return;
		} else if (player != null)
			Tribu.messagePlayer(player,
					game.getLocale("Message.LevelLoadedSuccessfully"));
		else
			game.LogInfo(ChatColor.stripColor(game
					.getLocale("Message.LevelLoadedSuccessfully")));

		game.setLevel(temp);
		if (restart)
			game.startRunning();

	}

	public void removeVote(final Player p) {
		if (votingEnabled) {
			votes.remove(p);
			if (votes.size() == game.getPlayersCount()) {
				cancelVote();
				run();
			}
		}
	}

	public void run() {
		taskID = -1;
		votingEnabled = false;
		final int[] voteCounts = new int[2];
		final Collection<Integer> nums = votes.values();
		for (final int vote : nums)
			voteCounts[vote - 1]++;
		votes.clear();
		if (voteCounts[0] >= voteCounts[1]) {
			ChangeLevel(randomLevel1, null);
			game.messagePlayers(String.format(
					game.getLocale("Broadcast.MapChosen"), randomLevel1));
		} else {
			ChangeLevel(randomLevel2, null);
			game.messagePlayers(String.format(
					game.getLocale("Broadcast.MapChosen"), randomLevel2));
		}
		game.startRunning();
	}

	public void startVote(final int duration) {
		final String[] levels = game.getLevelLoader().getLevelList()
				.toArray(new String[0]);

		if (levels.length < 2) { // Skip voting since there's only one option
			game.startRunning();
			return;
		}
		taskID = game.getPlugin().getServer().getScheduler()
				.scheduleSyncDelayedTask(game.getPlugin(), this, duration);
		votingEnabled = true;

		do
			randomLevel1 = levels[rnd.nextInt(levels.length)];
		while (randomLevel1 == game.getLevel().getName());

		if (levels.length >= 3)
			do
				randomLevel2 = levels[rnd.nextInt(levels.length)];
			while (randomLevel2 == game.getLevel().getName()
					|| randomLevel2 == randomLevel1);
		else
			randomLevel2 = game.getLevel().getName();
		game.messagePlayers(game.getLocale("Broadcast.MapVoteStarting"));
		game.messagePlayers(game.getLocale("Broadcast.Type"));
		game.messagePlayers(String.format(
				game.getLocale("Broadcast.SlashVoteForMap"), '1', randomLevel1));
		game.messagePlayers(String.format(
				game.getLocale("Broadcast.SlashVoteForMap"), '2', randomLevel2));
		game.messagePlayers(String.format(
				game.getLocale("Broadcast.VoteClosingInSeconds"),
				String.valueOf(duration / 20)));
	}

}
