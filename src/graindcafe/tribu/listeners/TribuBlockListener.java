package graindcafe.tribu.listeners;


import graindcafe.tribu.MyBlock;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.signs.TribuSign;

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

		if (TribuSign.isIt(plugin, event.getBlock())) {
			if (event.getPlayer().isOp()) {
				plugin.getLevel().removeSign(event.getBlock().getLocation());
			} else
				event.setCancelled(true);
		} else if (plugin.isDedicatedServer() && plugin.isRunning())
			plugin.pushBlock(new MyBlock(event.getBlock().getTypeId(), event.getBlock().getLocation()));
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (plugin.isDedicatedServer() && plugin.isRunning())
			plugin.pushBlock(new MyBlock(event.getBlockReplacedState().getTypeId(),event.getBlock().getLocation()));
	}

	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (plugin.getLevel() != null)
			plugin.getLevel().updateSigns(event);

	}

	@Override
	public void onSignChange(SignChangeEvent event) {

		if (TribuSign.isIt(plugin, event.getLines()) && event.getPlayer().isOp()) {
			TribuSign sign = TribuSign.getObject(plugin, event.getBlock().getLocation(), event.getLines());

			if (sign != null)
				if (plugin.getLevel() != null) {
					plugin.getLevel().addSign(sign);
				} else {
					event.getPlayer().sendMessage(plugin.getLocale("Message.NoLevelLoaded"));
					event.getPlayer().sendMessage(plugin.getLocale("Message.NoLevelLoaded2"));
					event.setCancelled(true);
				}

		}

	}

	public void registerEvents(PluginManager pm) {
		pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Low, plugin);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Priority.Low, plugin);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.SIGN_CHANGE, this, Priority.Normal, plugin);

	}
}
