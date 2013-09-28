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
package graindcafe.tribu;

import graindcafe.tribu.Configuration.Constants;

import java.util.List;

import org.bukkit.entity.Player;
import org.mcstats.Metrics;

public class WaveStarter implements Runnable {
	private final Tribu game;
	private boolean scheduled;
	private int taskID;
	private int waveNumber;
	private float zombieDamage;
	private float health;
	private Metrics.Graph survivorGraph = null;
	private Metrics.Graph zombieHealthGraph;
	private Metrics.Graph zombieDamageGraph;

	public WaveStarter(final Tribu instance) {
		game = instance;
		waveNumber = 1;
		scheduled = false;
		if (game.getMetrics() != null) {
			survivorGraph = game.getMetrics().createGraph(
					"Percentage of survivors");
			zombieHealthGraph = game.getMetrics().createGraph("Zombie Health");
			zombieDamageGraph = game.getMetrics().createGraph("Zombie Damage");

		}
	}

	private float calcPolynomialFunction(final double x, final List<Double> coef) {
		if (coef == null || coef.size() == 0)
			return 0;
		byte i = 0;
		float r = 0;

		for (final double c : coef) {
			r += c * Math.pow(x, i);
			i++;
		}
		r = Math.round(r * 10) / 10;
		if (r <= 0)
			return 0.1f;
		return r;
	}

	public void cancelWave() {
		if (scheduled) {
			game.getPlugin().getServer().getScheduler().cancelTask(taskID);
			scheduled = false;
		}
	}

	public float getCurrentDamage() {
		return zombieDamage;
	}

	public float getCurrentHealth() {
		return health;
	}

	public int getWaveNumber() {
		return waveNumber;
	}

	public void incrementWave() {
		waveNumber++;
	}

	public void resetWave() {
		waveNumber = 1;
	}

	public boolean hasStarted() {
		return !scheduled;
	}

	private void stat() {
		if (waveNumber == 0)
			return;
		if (survivorGraph != null)
			survivorGraph.addPlotter(new Metrics.Plotter("Wave " + waveNumber) {
				@Override
				public int getValue() {
					return game.getAliveCount();
				}
			});
		if (zombieHealthGraph != null)
			zombieHealthGraph.addPlotter(new Metrics.Plotter("Wave "
					+ waveNumber) {
				@Override
				public int getValue() {
					return Math.round(health);
				}
			});
		if (zombieDamageGraph != null)
			zombieDamageGraph.addPlotter(new Metrics.Plotter("Wave "
					+ waveNumber) {
				@Override
				public int getValue() {
					return Math.round(zombieDamage);
				}
			});
	}

	public void run() {
		if (game.isRunning()) {
			if (game.config().WaveStartTeleportPlayers)
				for (final Player p : game.getPlayers())
					p.teleport(game.getLevel().getInitialSpawn());
			if (game.config().WaveStartSetTime)
				game.getLevel().getInitialSpawn().getWorld()
						.setTime(game.config().WaveStartSetTimeTo);
			final int max = (int) Math.ceil(calcPolynomialFunction(waveNumber,
					game.config().ZombiesQuantity));
			health = calcPolynomialFunction(waveNumber,
					game.config().ZombiesHealth);
			zombieDamage = calcPolynomialFunction(waveNumber,
					game.config().ZombiesDamage);
			final int timeToSpawn = Math.round(Constants.TicksBySecond
					* (calcPolynomialFunction(waveNumber,
							game.config().ZombiesTimeToSpawn) / max));

			scheduled = false;
			stat();
			game.revivePlayers(false);
			game.getLevel().onWaveStart(waveNumber);
			game.getSpawnTimer().StartWave(max, health, timeToSpawn);
			game.messagePlayers("Broadcast.StartingWave",
					String.valueOf(waveNumber), String.valueOf(max),
					String.valueOf(health));
			game.getSpawner().startingCallback();
		}
	}

	public void scheduleWave(final int delay) {
		if (!scheduled && game.isRunning()) {
			taskID = game.getPlugin().getServer().getScheduler()
					.scheduleSyncDelayedTask(game.getPlugin(), this, delay);
			scheduled = true;
			game.messagePlayers("Broadcast.Wave",
					String.valueOf(game.getWaveStarter().getWaveNumber()),
					String.valueOf(delay / Constants.TicksBySecond));
		}
	}

}
