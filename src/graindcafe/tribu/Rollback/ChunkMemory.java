package graindcafe.tribu.Rollback;

import graindcafe.tribu.Tribu;

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

public class ChunkMemory implements Runnable {
	private boolean restoring = false;
	private boolean capturing = false;
	private boolean busy = false;
	HashSet<Chunk> chunkMemory;
	HashSet<ChunkSnapshot> snapMemory;
	Iterator<ChunkSnapshot> iterator;
	int taskId;
	private HashSet<EntryBlockState> tileEntityMemory;
	private static HashSet<EntryBlock> failedRestore;

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

	public ChunkMemory() {
		snapMemory = new HashSet<ChunkSnapshot>();
		chunkMemory = new HashSet<Chunk>();
		failedRestore = new HashSet<EntryBlock>();
		tileEntityMemory = new HashSet<EntryBlockState>();
	}

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

	public void startRestoring(Tribu plugin, int speed) {
		if (!restoring) {
			stopCapturing();
			debugMsg("Start restoring : " + snapMemory.size());
			restoring = true;
			speed = 33 - speed / 3;
			iterator = snapMemory.iterator();
			((CraftWorld) plugin.getLevel().getInitialSpawn().getWorld()).getHandle().suppressPhysics = true;
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, speed);
		}
	}

	protected static void debugMsg(String msg) {
		Logger.getLogger("Minecraft").info(msg);
	}

	public void stopRestoring() {
		// Triple try
		if (!tryRestoreFails() && !tryRestoreFails() && !tryRestoreFails())
			debugMsg(failedRestore.size() + " failed");
		else
			debugMsg("All blocks has been restored");

		if (chunkMemory.size() > 0)
			((CraftWorld) chunkMemory.iterator().next().getWorld()).getHandle().suppressPhysics = false;
		debugMsg("Restoring tile entities");
		Iterator<EntryBlockState> iterator = tileEntityMemory.iterator();
		while (iterator.hasNext())
			try {
				iterator.next().restore();
				iterator.remove();
			} catch (WrongBlockException e) {
				debugMsg(e.getMessage());
				e.getWorld().getBlockAt(e.getX(), e.getY(), e.getZ()).setTypeId(e.getExpected());
			} catch (Exception e) {
				e.printStackTrace();
			}
		while (iterator.hasNext())
			try {
				iterator.next().restore();
			} catch (Exception e) {
			} finally {
				iterator.remove();
			}
		debugMsg("Stop restoring tile entities");
		restoring = false;
		snapMemory.clear();
		chunkMemory.clear();
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		debugMsg("Stop restoring !");
	}

	public void startCapturing() {
		capturing = true;
	}

	public void stopCapturing() {
		capturing = false;
	}

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

	public void getReady() {
		if (capturing || restoring) {
			capturing = false;
			restoreAll();
			restoring = false;
		}

	}

	public void add(Chunk chunk) {
		int baseX = chunk.getX(), baseZ = chunk.getZ();
		addNoRecursion(chunk);
		addNoRecursion(chunk.getWorld().getChunkAt(baseX + 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ + 1));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX - 1, baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX, baseZ - 1));
	}

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
