package graindcafe.tribu;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Block;

public class ChunkMemory implements Runnable {
	private boolean restoring=false;
	private boolean capturing=false;
	private boolean busy=false;
	HashSet<Chunk> chunkMemory;
	HashSet<ChunkSnapshot> snapMemory;
	Iterator<ChunkSnapshot> iterator;
	int taskId;
	

	public void restore(ChunkSnapshot snap) {
		busy=true;
		
		
		
		int baseX=snap.getX(),baseZ= snap.getZ(),y,yMax;
		debugMsg("Restoring : "+baseX+","+baseZ);
		
		Block currentB;
		int snapId;byte snapData;
		Chunk currentChunk=Bukkit.getWorld(snap.getWorldName()).getChunkAt(baseX, baseZ);
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				yMax=snap.getHighestBlockYAt(x, z);
				y=0;
				while(y<yMax)
				{
					currentB=currentChunk.getBlock(x, y, z);
					snapId=snap.getBlockTypeId(x, y, z);
					snapData=(byte) snap.getBlockData(x, y, z);
					if(currentB.getTypeId() != snapId || currentB.getData() != snapData)
					{
						currentB.setTypeIdAndData(snapId, (byte) snapData, false);
					}
					y++;
				}
				while(y<255)
				{
					currentB=currentChunk.getBlock(x, y, z);
					if(currentB.getTypeId()!=0)
						currentB.setTypeId(0);
					y++;
				}
			}
		}
		busy=false;
	}
	public ChunkMemory()
	{
		snapMemory=new HashSet<ChunkSnapshot>();
		chunkMemory=new HashSet<Chunk>();
	}
	public void restoreAll() {
		restoring=false;
		try {
			while(busy)
				Thread.sleep(200);
		} catch (InterruptedException e) {
			
		}
		for (ChunkSnapshot cs : snapMemory) {
			restore(cs);
		}
		snapMemory.clear();
		chunkMemory.clear();
	}

	public void startRestoring(Tribu plugin,int speed) {
		if(!restoring)
		{
			stopCapturing();
			debugMsg("Start restoring : "+snapMemory.size());
			restoring = true;
			speed = 33 - speed/3;
			iterator=snapMemory.iterator();
			taskId=Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, speed);
		}
	}
	private static void debugMsg(String msg)
	{
		Logger.getLogger("Minecraft").info(msg);
	}
	public void stopRestoring()
	{
		debugMsg("Stop restoring !");
		restoring=false;
		snapMemory.clear();
		chunkMemory.clear();
		Bukkit.getScheduler().cancelTask(taskId);
		taskId=-1;
	}
	public void startCapturing() {
		capturing = true;
	}
	public void stopCapturing()
	{
		capturing=false;
	}
	private void addNoRecursion(Chunk chunk) {
		if (capturing)
			if(chunkMemory.add(chunk))
			{
				debugMsg("Adding : "+chunk.getX()+","+chunk.getZ());
				snapMemory.add(chunk.getChunkSnapshot(true,false,false));
				
			}
	}
	public void add(Chunk chunk) {
		int baseX=chunk.getX(),baseZ=chunk.getZ();
		addNoRecursion(chunk);
		addNoRecursion(chunk.getWorld().getChunkAt(baseX+1,baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX,baseZ+1));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX-1,baseZ));
		addNoRecursion(chunk.getWorld().getChunkAt(baseX,baseZ-1));
	}

	public void run() {
		if (restoring && !busy)
			if(iterator.hasNext())
			{
				restore(iterator.next());
				iterator.remove();
			}
			else
			{
				stopRestoring();
			}
			
	}

}