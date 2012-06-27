package graindcafe.tribu;

public class SpawnTimer implements Runnable {

	private Tribu plugin;
	private int taskID;

	// ?? maxSpawn ?
	// private int totalSpawn;

	SpawnTimer(Tribu instance) {
		plugin = instance;
		taskID = -1;
	}

	public void getState() {
		if (plugin.isRunning() && plugin.getAliveCount() > 0 && !plugin.getSpawner().isWaveCompleted())
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

	@Override
	public void run() {
		if (plugin.isRunning() && plugin.getAliveCount() > 0 && !plugin.getSpawner().isWaveCompleted()) {
			if (plugin.getSpawner().haveZombieToSpawn())
				plugin.getSpawner().SpawnZombie();
			else {
				plugin.getSpawner().finishCallback();
				plugin.getSpawner().checkZombies();
			}
		} else {
			plugin.getSpawner().finishCallback();
			if (plugin.getSpawner().tryStartNextWave())
				Stop();
		}

	}

	public void Start(int timeToSpawn) {
		taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, timeToSpawn);
	}

	public void StartWave(int max, int health,int timeToSpawn) {
		if (plugin.isRunning()) {
			plugin.getSpawner().setMaxSpawn(max);
			plugin.getSpawner().resetTotal();
			plugin.getSpawner().setHealth(health);
			Start(timeToSpawn);
		}
	}/*
	 * public void StartWave(int total, int max, int health) { if
	 * (plugin.isRunning()) { totalSpawn = total;
	 * plugin.getSpawner().setMaxSpawn(max); plugin.getSpawner().resetTotal();
	 * plugin.getSpawner().setHealth(health); Start(); } }
	 */

	public void Stop() {
		if (taskID > 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			taskID = -1;
		}
	}

}
