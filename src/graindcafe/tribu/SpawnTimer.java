package graindcafe.tribu;

public class SpawnTimer implements Runnable {

	private Tribu plugin;
	private int taskID;
	// ?? maxSpawn ?
	//private int totalSpawn;

	SpawnTimer(Tribu instance) {
		plugin = instance;
		taskID = -1;
	}

	public void getState()
	{
		if (plugin.isRunning() && plugin.getSpawner().haveZombieToSpawn()
				&& plugin.getAliveCount() > 0)
			plugin.LogInfo("Should spawn zombie");
		else
			plugin.LogInfo("Should NOT spawn zombie");
		if(taskID>0)
			plugin.LogInfo("Should run");
		else
			plugin.LogInfo("Should NOT run");
		if(plugin.getServer().getScheduler().isCurrentlyRunning(taskID))
			plugin.LogInfo("is currently running");
		else
			plugin.LogInfo("is NOT currently running");
		if(plugin.getServer().getScheduler().isQueued(taskID))
			plugin.LogInfo("is queued");
		else
			plugin.LogInfo("is NOT queued");

	}
	@Override
	public void run() {
		if (plugin.isRunning() && plugin.getSpawner().haveZombieToSpawn()
				&& plugin.getAliveCount() > 0) {
			plugin.getSpawner().SpawnZombie();
		} else {
			
			Stop();
		}

	}
	public void Start() {
		taskID = plugin
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(plugin, this, Constants.SpawnDelay,
						Constants.SpawnDelay);
		
	}

	public void StartWave(int max, int health) {
		if (plugin.isRunning()) {
			plugin.getSpawner().setMaxSpawn(max);
			plugin.getSpawner().resetTotal();
			plugin.getSpawner().setHealth(health);
			Start();
		}
	}/*
	public void StartWave(int total, int max, int health) {
		if (plugin.isRunning()) {
			totalSpawn = total;
			plugin.getSpawner().setMaxSpawn(max);
			plugin.getSpawner().resetTotal();
			plugin.getSpawner().setHealth(health);
			Start();
		}
	}*/

	public void Stop() {
		if (taskID > 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			plugin.getSpawner().enableFinishCallback();
			taskID = -1;
		}
	}

}
