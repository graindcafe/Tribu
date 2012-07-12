package graindcafe.tribu.Rollback;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import net.minecraft.server.TileEntityMobSpawner;
import net.minecraft.server.TileEntityNote;
import net.minecraft.server.TileEntityRecordPlayer;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkMemory implements Runnable {
	private boolean restoring = false;
	private boolean capturing = false;
	private boolean busy = false;
	HashSet<Chunk> chunkMemory;
	HashSet<ChunkSnapshot> snapMemory;
	Iterator<ChunkSnapshot> iterator;
	int taskId;
	private HashSet<EntryBlockState> tileEntityMemory;
	private HashSet<EntryBlock> failedRestore;

	/**
	 * Try restoring blocks which failed last time
	 * @return
	 */
	protected boolean tryRestoreFails() {
		if (!failedRestore.isEmpty()) {
			Iterator<EntryBlock> fails = failedRestore.iterator();
			debugMsg(failedRestore.size() + " fails");
			while (fails.hasNext()) {
				// If succeed remove it else keep it
				if(fails.next().restore())
					fails.remove();
			}
		}
		return failedRestore.isEmpty();
	}

	/**
	 * Restore a chunk 
	 * @param snap
	 */
	public void restore(ChunkSnapshot snap) {
		busy = true;
		int baseX = snap.getX(), baseZ = snap.getZ(), y;

		Chunk currentChunk = Bukkit.getWorld(snap.getWorldName()).getChunkAt(baseX, baseZ);

		WorldServer world = ((CraftWorld) currentChunk.getWorld()).getHandle();
		int maxHeight = currentChunk.getWorld().getMaxHeight();
		// suppress physics to stop falling lava and fire etc...
		world.suppressPhysics = true;
		if (!currentChunk.isLoaded())
			while (currentChunk.load(false))
				;
		for (Entity e : currentChunk.getEntities()) {
			if (e instanceof Item)
				((Item) e).remove();
		}
		tryRestoreFails();
		debugMsg("Restoring : " + baseX + "," + baseZ);
		Block currentB;
		int snapId;
		byte snapData;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				y = 0;
				while (y < maxHeight) {
					currentB = currentChunk.getBlock(x, y, z);
					snapId = snap.getBlockTypeId(x, y, z);
					snapData = (byte) snap.getBlockData(x, y, z);
					currentB.setTypeIdAndData(snapId, (byte) snapData, false);
					if (currentB.getTypeId() != snapId || currentB.getData() != snapData)
						failedRestore.add(new EntryBlock(baseX + x, y, baseZ + z, snapId, snapData, world));

					y++;
				}
			}
		}

		busy = false;
	}

	/**
	 * Init memories
	 */
	public ChunkMemory() {
		snapMemory = new HashSet<ChunkSnapshot>();
		chunkMemory = new HashSet<Chunk>();
		failedRestore = new HashSet<EntryBlock>();
		tileEntityMemory = new HashSet<EntryBlockState>();
	}

	/**
	 * Restore everything as quick as possible. 
	 * Warning ! This use a lot of resource in one time. 
	 */
	public void restoreAll() {
		if (restoring) {
			Bukkit.getScheduler().cancelTask(taskId);
			restoring = false;
			try {
				while (busy)
					Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		} else
			iterator = snapMemory.iterator();

		while (iterator.hasNext())
			restore(iterator.next());

		stopRestoring();
	}

	/**
	 * Start the delayed restoring of chunks
	 * @param plugin a Java Plugin (for scheluding) 
	 * @param speed The speed of restoring
	 */
	public void startRestoring(JavaPlugin plugin, int speed) {
		if (!restoring) {
			stopCapturing();
			debugMsg("Start restoring : " + snapMemory.size());
			restoring = true;
			speed = 21 - speed / 5;
			iterator = snapMemory.iterator();
			if (chunkMemory.size() > 0)
				((CraftWorld) chunkMemory.iterator().next().getWorld()).getHandle().suppressPhysics = true;
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, speed);
		}
	}

	/**
	 * Send a message for debug purpose
	 * @param msg
	 */
	protected static void debugMsg(String msg) {
		Logger.getLogger("Minecraft").info(msg);
	}

	/**
	 * Finish restoring
	 */
	protected void stopRestoring() {
		// Triple try
		if (!tryRestoreFails() && !tryRestoreFails() && !tryRestoreFails())
		{
			debugMsg(failedRestore.size() + " failed");
			failedRestore.clear();
		}
		else
			debugMsg("All blocks has been restored");
		
		debugMsg("Restoring tile entities");
		Iterator<EntryBlockState> iterator = tileEntityMemory.iterator();
		while (iterator.hasNext())
			try {
				iterator.next().restore();
				iterator.remove();
			} catch (WrongBlockException e) {
				debugMsg(e.getMessage());
				// Try placing the desired block, we'll try again later (2nd loop)
				e.getWorld().getBlockAt(e.getX(), e.getY(), e.getZ()).setTypeId(e.getExpected());
			} catch (Exception e) {
				e.printStackTrace();
			}
		// Try again failed restores
		while (iterator.hasNext())
			try {
				iterator.next().restore();
			} catch (Exception e) {
			} finally {
				iterator.remove();
			}
		debugMsg("Stop restoring tile entities");
		// Reset suppres physics 
		if (chunkMemory.size() > 0)
			((CraftWorld) chunkMemory.iterator().next().getWorld()).getHandle().suppressPhysics = false;
		// Restoring is over
		restoring = false;
		// Clear memories
		snapMemory.clear();
		chunkMemory.clear();
		// End task if there is a task
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		debugMsg("Stop restoring !");
	}

	/**
	 * Enabling capturing
	 */
	public void startCapturing() {
		capturing = true;
	}

	/**
	 * Disabling capturing
	 */
	public void stopCapturing() {
		capturing = false;
	}

	/**
	 * Really add a chunk
	 * @param chunk
	 */
	private void addNoRecursion(Chunk chunk) {
		if (capturing && chunk.isLoaded() && chunkMemory.add(chunk)) {
			debugMsg("Adding : " + chunk.getX() + "," + chunk.getZ());
			for (BlockState bs : chunk.getTileEntities()) {
				try {
					if (bs instanceof Sign) {
						tileEntityMemory.add(new EntrySign((Sign) bs));
					} else if (bs instanceof InventoryHolder) {
						tileEntityMemory.add(new EntryInventory(bs));
					}
					else if(bs instanceof TileEntityRecordPlayer)
					{
						debugMsg("Not yet implemented");
					}
					else if(bs instanceof TileEntityNote)
					{
						debugMsg("Not yet implemented");
					}
					else if(bs instanceof TileEntityMobSpawner)
					{
						tileEntityMemory.add(new EntrySpawner(bs));
					}
				} catch (Exception e) {
					debugMsg("Exception : " + e.getMessage());
					e.printStackTrace();
				}

			}
			snapMemory.add(chunk.getChunkSnapshot(false, false, false));
		}
	}

	/**
	 * Finish or start restoring quickly 
	 */
	public void getReady() {
		if (capturing || restoring) {
			capturing = false;
			restoreAll();
			restoring = false;
		}

	}

	/**
	 * Add a chunk (and nearly chunks)
	 * @param chunk
	 */
	public void add(Chunk chunk) {
		int baseX = chunk.getX(), baseZ = chunk.getZ();
		addNoRecursion(chunk);
		addNoRecursion(chunk.getWorld().getChunkAt(baseX + 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ + 1));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX - 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ - 1));
	}

	/** Run run run ! (call restoring of a chunk)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (restoring && !busy)
			if (iterator.hasNext()) {
				restore(iterator.next());
				iterator.remove();
			} else {
				stopRestoring();
			}

	}

}
