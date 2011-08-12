package graindcafe.tribu;

import java.util.List;

import org.bukkit.entity.Player;

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
		int r = 0;
		int tmpX;

		for (double c : coef) {
			j = 0;
			tmpX = 1;
			while (j < i) {
				tmpX *= x;
				j++;
			}
			r += Math.round(c * tmpX);
			i--;
		}
		return r;
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
			if (plugin.getConfiguration().getBoolean("WaveStart.TeleportPlayers", false)) {
				for (Player p : plugin.getPlayers()) {
					p.teleport(plugin.getLevel().getInitialSpawn());
				}
			}
			if (plugin.getConfiguration().getBoolean("WaveStart.SetTime", true))
				plugin.getLevel().getInitialSpawn().getWorld().setTime(plugin.getConfiguration().getInt("WaveStart.SetTimeTo", 37000));
			scheduled = false;
			plugin.revivePlayers(false);
			plugin.getLevel().onWaveStart();
			int max = calcPolynomialFunction(waveNumber, plugin.getConfiguration().getDoubleList("Zombies.Quantity", null));
			int health = calcPolynomialFunction(waveNumber, plugin.getConfiguration().getDoubleList("Zombies.Health", null));
			plugin.getSpawnTimer().StartWave(max, health);
			plugin.getServer()
					.broadcastMessage(
							String.format(plugin.getLocale("Broadcast.StartingWave"), String.valueOf(waveNumber), String.valueOf(max),
									String.valueOf(health)));
			plugin.getSpawner().startingCallback();
		}
	}

	public void scheduleWave(int delay) {
		if (!scheduled && plugin.isRunning()) {
			taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
			scheduled = true;
			plugin.getServer().broadcastMessage(
					String.format(plugin.getLocale("Broadcast.Wave"), String.valueOf(plugin.getWaveStarter().getWaveNumber()),
							String.valueOf(delay / 20)));
		}
	}

}
