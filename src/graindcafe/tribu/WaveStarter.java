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

public class WaveStarter implements Runnable {
	private final Tribu plugin;
	private boolean scheduled;
	private int taskID;
	private int waveNumber;
	private float zombieDamage;
	private float health;

	public WaveStarter(final Tribu instance) {
		plugin = instance;
		waveNumber = 1;
		scheduled = false;
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
			plugin.getServer().getScheduler().cancelTask(taskID);
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

	public void run() {
		if (plugin.isRunning()) {
			if (plugin.config().WaveStartTeleportPlayers)
				for (final Player p : plugin.getPlayers())
					p.teleport(plugin.getLevel().getInitialSpawn());
			if (plugin.config().WaveStartSetTime)
				plugin.getLevel().getInitialSpawn().getWorld()
						.setTime(plugin.config().WaveStartSetTimeTo);
			final int max = (int) Math.ceil(calcPolynomialFunction(waveNumber,
					plugin.config().ZombiesQuantity));
			health = calcPolynomialFunction(waveNumber,
					plugin.config().ZombiesHealth);
			zombieDamage = calcPolynomialFunction(waveNumber,
					plugin.config().ZombiesDamage);
			final int timeToSpawn = Math.round(Constants.TicksBySecond
					* (calcPolynomialFunction(waveNumber,
							plugin.config().ZombiesTimeToSpawn) / max));

			scheduled = false;
			plugin.revivePlayers(false);
			plugin.getLevel().onWaveStart(waveNumber);
			plugin.getSpawnTimer().StartWave(max, health, timeToSpawn);
			plugin.messagePlayers("Broadcast.StartingWave",
					String.valueOf(waveNumber), String.valueOf(max),
					String.valueOf(health));
			plugin.getSpawner().startingCallback();
		}
	}

	public void scheduleWave(final int delay) {
		if (!scheduled && plugin.isRunning()) {
			taskID = plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, this, delay);
			scheduled = true;
			plugin.messagePlayers("Broadcast.Wave",
					String.valueOf(plugin.getWaveStarter().getWaveNumber()),
					String.valueOf(delay / Constants.TicksBySecond));
		}
	}

}
