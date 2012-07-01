package graindcafe.tribu;

import java.util.List;
import org.bukkit.entity.Player;

import graindcafe.tribu.Configuration.Constants;

public class WaveStarter implements Runnable {
	private Tribu plugin;
	private boolean scheduled;
	private int taskID;
	private int waveNumber;
	public WaveStarter(Tribu instance) {
		plugin = instance;
		waveNumber = 1;
		scheduled = false;
	}
	
	private int calcPolynomialFunction(int x, List<Double> coef) {
		if (coef == null || coef.size() == 0)
			return 0;
		byte i = (byte) (coef.size() - 1);
		byte j;
		float r = 0;
		int tmpX;

		for (double c : coef) {
			j = 0;
			tmpX = 1;
			while (j < i) {
				tmpX *= x;
				j++;
			}
			r += c * tmpX;
			i--;
		}
		return Math.round(r);
	}

	public void cancelWave() {
		if (scheduled) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			scheduled = false;
		}
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

	@Override
	public void run() {
		if (plugin.isRunning()) {
			if (plugin.config().WaveStartTeleportPlayers) {
				for (Player p : plugin.getPlayers()) {
					p.teleport(plugin.getLevel().getInitialSpawn());
				}
			}
			if (plugin.config().WaveStartSetTime)
				plugin.getLevel().getInitialSpawn().getWorld().setTime(plugin.config().WaveStartSetTimeTo);
			int max = calcPolynomialFunction(waveNumber, plugin.config().ZombiesQuantity);
			int health = calcPolynomialFunction(waveNumber, plugin.config().ZombiesHealth);
			int timeToSpawn = Math.round((float)Constants.TicksBySecond*((float)calcPolynomialFunction(waveNumber, plugin.config().ZombiesTimeToSpawn)/(float)max)); 
			scheduled = false;
			plugin.revivePlayers(false);
			plugin.getLevel().onWaveStart();
			plugin.getSpawnTimer().StartWave(max, health,timeToSpawn);
			plugin.messagePlayers(
							"Broadcast.StartingWave", String.valueOf(waveNumber), String.valueOf(max),
									String.valueOf(health));
			plugin.getSpawner().startingCallback();
		}
	}

	public void scheduleWave(int delay) {
		if (!scheduled && plugin.isRunning()) {
			taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
			scheduled = true;
			plugin.messagePlayers(
					"Broadcast.Wave", String.valueOf(plugin.getWaveStarter().getWaveNumber()),
							String.valueOf(delay / Constants.TicksBySecond));
		}
	}

}
