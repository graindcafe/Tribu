package graindcafe.tribu.listeners;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.signs.TribuSign;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.PluginManager;

public class TribuBlockListener extends BlockListener {
	private Tribu plugin;

	public TribuBlockListener(Tribu instance) {
		plugin = instance;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if ((event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) && plugin.getLevel() != null && plugin.getLevel().isSpecialSign(event.getBlock().getLocation())) {
			if (event.getPlayer().isOp()) {
				plugin.getLevel().removeSign(event.getBlock().getLocation());
			} else if (plugin.isDedicatedServer())
				event.setCancelled(true);
		} else if (plugin.isDedicatedServer() && !event.getPlayer().isOp())
			event.setCancelled(true);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {		
		if(!event.getPlayer().isOp() && (plugin.isDedicatedServer()))
			event.setCancelled(true);
		
	}

	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if(plugin.getLevel() != null)
			plugin.getLevel().updateSigns(event);

	}
	@Override
	public void onSignChange(SignChangeEvent event)
	{
		
		
		if (event.getPlayer().isOp()) {
			TribuSign sign = TribuSign.getObject(plugin,event.getBlock().getLocation(),event.getLines());
			
			if (sign != null)
				if (plugin.getLevel() != null)
				{
					plugin.getLevel().addSign(sign);
				}
				else {
					
					event.getPlayer().sendMessage(
							plugin.getLocale("Message.NoLevelLoaded"));
					event.getPlayer().sendMessage(
							plugin.getLocale("Message.NoLevelLoaded2"));
					event.setCancelled(true);
				}
			
		} 
		// Impossible case (onBlockPlace is cancelled before)
		/*else if (plugin.isDedicatedServer())
			event.setCancelled(true);*/
		
	}
	public void registerEvents(PluginManager pm) {
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Low, plugin);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Priority.Low, plugin);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this, Priority.Normal,
				plugin);
		pm.registerEvent(Event.Type.SIGN_CHANGE, this, Priority.Normal, plugin);

	}
}
