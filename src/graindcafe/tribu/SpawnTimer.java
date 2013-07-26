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

public class SpawnTimer implements Runnable {

	private final Tribu plugin;
	private int taskID;

	// ?? maxSpawn ?
	// private int totalSpawn;

	SpawnTimer(final Tribu instance) {
		plugin = instance;
		taskID = -1;
	}

	public void getState() {
		if (plugin.isRunning() && plugin.getAliveCount() > 0
				&& !plugin.getSpawner().isWaveCompleted())
			plugin.LogInfo("Should spawn zombie");
		else
			plugin.LogInfo("Should NOT spawn zombie");
		if (taskID > 0)
			plugin.LogInfo("is started ");
		else
			plugin.LogInfo("is stopped !");
		if (plugin.getServer().getScheduler().isCurrentlyRunning(taskID))
			plugin.LogInfo("is currently running");
		else if (plugin.getServer().getScheduler().isQueued(taskID))
			plugin.LogInfo("is queued");
		else
			plugin.LogInfo("is NOT queued and is NOT running !");

	}

	public void run() {
		if (plugin.isRunning() && plugin.getAliveCount() > 0
				&& !plugin.getSpawner().isWaveCompleted()) {
			if (!plugin.getSpawner().spawnZombie())
				plugin.getSpawner().checkZombies();
		} else {
			plugin.getSpawner().finishCallback();
			if (plugin.getSpawner().tryStartNextWave())
				stop();
		}

	}

	public void StartWave(final int max, final float health,
			final int timeToSpawn) {
		if (plugin.isRunning()) {
			plugin.getSpawner().setMaxSpawn(max);
			plugin.getSpawner().resetTotal();
			plugin.getSpawner().setHealth(health);
			taskID = plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, this, 0, timeToSpawn);
		}
	}/*
	 * public void StartWave(int total, int max, int health) { if
	 * (plugin.isRunning()) { totalSpawn = total;
	 * plugin.getSpawner().setMaxSpawn(max); plugin.getSpawner().resetTotal();
	 * plugin.getSpawner().setHealth(health); Start(); } }
	 */

	public void stop() {
		if (taskID > 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			taskID = -1;
		}
	}

}
