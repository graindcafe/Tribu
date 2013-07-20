/*
 * 
 */
package graindcafe.tribu.Rollback;

import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.server.v1_6_R2.TileEntityMobSpawner;
import net.minecraft.server.v1_6_R2.TileEntityNote;
import net.minecraft.server.v1_6_R2.TileEntityPiston;
import net.minecraft.server.v1_6_R2.TileEntityRecordPlayer;
import net.minecraft.server.v1_6_R2.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Directional;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkMemory implements Runnable {
	/**
	 * Send a message for debug purpose
	 * 
	 * @param msg
	 */
	protected static void debugMsg(final String msg) {
		// Logger.getLogger("Minecraft").info(msg);
	}

	private boolean							restoring	= false;
	private boolean							capturing	= false;
	private boolean							busy		= false;
	HashSet<Chunk>							chunkMemory;
	HashSet<ChunkSnapshot>					snapMemory;
	HashSet<CraftWorld>						worlds;
	Iterator<ChunkSnapshot>					iterator;
	int										taskId;
	private final HashSet<EntryBlockState>	tileEntityMemory;

	private final HashSet<EntryBlock>		failedRestore;

	/**
	 * Init memories
	 */
	public ChunkMemory() {
		snapMemory = new HashSet<ChunkSnapshot>();
		chunkMemory = new HashSet<Chunk>();
		failedRestore = new HashSet<EntryBlock>();
		tileEntityMemory = new HashSet<EntryBlockState>();
		worlds = new HashSet<CraftWorld>();
	}

	/**
	 * Add a chunk (and nearly chunks)
	 * 
	 * @param chunk
	 */
	public void add(final Chunk chunk) {
		final int baseX = chunk.getX(), baseZ = chunk.getZ();
		addNoRecursion(chunk);
		addNoRecursion(chunk.getWorld().getChunkAt(baseX + 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ + 1));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX - 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ - 1));
	}

	/**
	 * Really add a chunk
	 * 
	 * @param chunk
	 */
	private void addNoRecursion(final Chunk chunk) {
		if (capturing && chunk.isLoaded() && chunkMemory.add(chunk)) {
			debugMsg("Adding : " + chunk.getX() + "," + chunk.getZ());
			for (final BlockState bs : chunk.getTileEntities())
				try {
					if (bs instanceof Sign)
						tileEntityMemory.add(new EntrySign(bs));
					else if (bs instanceof InventoryHolder)
						tileEntityMemory.add(new EntryInventory(bs));
					else if (bs instanceof TileEntityRecordPlayer)
						debugMsg("Not yet implemented");
					else if (bs instanceof TileEntityNote)
						debugMsg("Not yet implemented");
					else if (bs instanceof TileEntityMobSpawner)
						tileEntityMemory.add(new EntrySpawner(bs));
					else if (bs instanceof TileEntityPiston)
						tileEntityMemory.add(new EntryPiston(bs));
					else if (bs.getData() instanceof Directional) tileEntityMemory.add(new EntryDirectional(bs));
				} catch (final Exception e) {
					debugMsg("Exception : " + e.getMessage());
					e.printStackTrace();
				}
			// if(!worlds.contains((CraftWorld)chunk.getWorld()))
			worlds.add((CraftWorld) chunk.getWorld());
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
	 * Restore a chunk
	 * 
	 * @param snap
	 */
	public void restore(final ChunkSnapshot snap) {
		busy = true;
		final int baseX = snap.getX(), baseZ = snap.getZ();
		int y;

		final Chunk currentChunk = Bukkit.getWorld(snap.getWorldName()).getChunkAt(baseX, baseZ);

		final WorldServer world = ((CraftWorld) currentChunk.getWorld()).getHandle();
		final int maxHeight = currentChunk.getWorld().getMaxHeight();
		// suppress physics to stop falling lava and fire etc...
		world.isStatic = true;
		if (!currentChunk.isLoaded()) while (currentChunk.load(false))
			;
		for (final Entity e : currentChunk.getEntities())
			if (e instanceof Item) ((Item) e).remove();
		tryRestoreFails();
		debugMsg("Restoring : " + baseX + "," + baseZ);
		Block currentB;
		int snapId;
		byte snapData;
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				y = 0;
				while (y < maxHeight) {
					currentB = currentChunk.getBlock(x, y, z);
					snapId = snap.getBlockTypeId(x, y, z);
					snapData = (byte) snap.getBlockData(x, y, z);
					currentB.setTypeIdAndData(snapId, snapData, false);
					if (currentB.getTypeId() != snapId || currentB.getData() != snapData) failedRestore.add(new EntryBlock(baseX + x, y, baseZ + z, snapId, snapData, world));

					y++;
				}
			}

		busy = false;
	}

	/**
	 * Restore everything as quick as possible. Warning ! This use a lot of
	 * resource in one time.
	 */
	public void restoreAll() {
		if (restoring) {
			Bukkit.getScheduler().cancelTask(taskId);
			restoring = false;
			try {
				while (busy)
					Thread.sleep(200);
			} catch (final InterruptedException e) {
			}
		} else
			iterator = snapMemory.iterator();
		final Iterator<CraftWorld> wIterator = worlds.iterator();
		while (wIterator.hasNext())
			wIterator.next().getHandle().isStatic = true;
		while (iterator.hasNext())
			restore(iterator.next());

		stopRestoring();
	}

	/**
	 * Run run run ! (call restoring of a chunk)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (restoring && !busy) if (iterator.hasNext()) {
			restore(iterator.next());
			iterator.remove();
		} else
			stopRestoring();

	}

	/**
	 * Enabling capturing
	 */
	public void startCapturing() {
		capturing = true;
	}

	/**
	 * Start the delayed restoring of chunks
	 * 
	 * @param plugin
	 *            a Java Plugin (for scheluding)
	 * @param speed
	 *            The speed of restoring
	 */
	public void startRestoring(final JavaPlugin plugin, int speed) {
		if (!restoring) {
			stopCapturing();
			if (snapMemory.isEmpty()) return;
			debugMsg("Start restoring : " + snapMemory.size());
			restoring = true;
			speed = 21 - speed / 5;
			// Init the iterator
			iterator = snapMemory.iterator();
			final Iterator<CraftWorld> wIterator = worlds.iterator();
			while (wIterator.hasNext())
				wIterator.next().getHandle().isStatic = true;
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, speed);
		}
	}

	/**
	 * Disabling capturing
	 */
	public void stopCapturing() {
		capturing = false;
	}

	/**
	 * Finish restoring
	 */
	protected void stopRestoring() {
		// Triple try
		if (!tryRestoreFails() && !tryRestoreFails() && !tryRestoreFails()) {
			debugMsg(failedRestore.size() + " failed");
			failedRestore.clear();
		} else
			debugMsg("All blocks has been restored");

		debugMsg("Restoring tile entities");
		final Iterator<EntryBlockState> iterator = tileEntityMemory.iterator();
		while (iterator.hasNext())
			try {
				iterator.next().restore();
				iterator.remove();
			} catch (final WrongBlockException e) {
				debugMsg(e.getMessage());
				// Try placing the desired block, we'll try again later (2nd
				// loop)
				e.getWorld().getBlockAt(e.getX(), e.getY(), e.getZ()).setTypeId(e.getExpected());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		// Try again failed restores
		while (iterator.hasNext())
			try {
				iterator.next().restore();
			} catch (final Exception e) {
			} finally {
				iterator.remove();
			}
		debugMsg("Stop restoring tile entities");
		// Reset suppress physics
		final Iterator<CraftWorld> wIterator = worlds.iterator();
		while (wIterator.hasNext())
			wIterator.next().getHandle().isStatic = false;
		// Restoring is over
		restoring = false;
		// Clear memories
		snapMemory.clear();
		chunkMemory.clear();
		worlds.clear();
		// End task if there is a task
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		debugMsg("Stop restoring !");
	}

	/**
	 * Try restoring blocks which failed last time
	 * 
	 * @return
	 */
	protected boolean tryRestoreFails() {
		if (!failedRestore.isEmpty()) {
			final Iterator<EntryBlock> fails = failedRestore.iterator();
			debugMsg(failedRestore.size() + " fails");
			while (fails.hasNext())
				// If succeed remove it else keep it
				if (fails.next().restore()) fails.remove();
		}
		return failedRestore.isEmpty();
	}

}
