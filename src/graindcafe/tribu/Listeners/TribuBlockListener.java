package graindcafe.tribu.Listeners;

import graindcafe.tribu.Tribu;
import graindcafe.tribu.Signs.TribuSign;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class TribuBlockListener implements Listener {
	private Tribu plugin;

	public TribuBlockListener(Tribu instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (TribuSign.isIt(plugin, event.getBlock())) {
			if (event.getPlayer().hasPermission("tribu.signs.break")) {
				plugin.getLevel().removeSign(event.getBlock().getLocation());
			} else {
				if (event.getPlayer() != null)
				Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.ProtectedBlock"));
				TribuSign.update((Sign) event.getBlock().getState());
				event.setCancelled(true);
			}
		} else if (plugin.isRunning() && plugin.isPlaying(event.getPlayer()))
			plugin.getBlockTrace().push(event.getBlock().getState());
		// Else if not running or not playing we may check if it's server exclusive / world exclusive and prevent modifications
		
			
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (plugin.isRunning() && plugin.isPlaying(event.getPlayer()))
			if (event.getBlock().getType().equals(Material.FIRE))
				plugin.getBlockTrace().push(event.getBlockAgainst().getState());
			else
				plugin.getBlockTrace().push(event.getBlockReplacedState(), event.getBlockPlaced());
		// Else if not running or not playing we may check if it's server exclusive / world exclusive and prevent modifications
	}

	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		
		if (plugin.isRunning()) {
			plugin.getBlockTrace().push(event.getBlock().getState());
			if (plugin.getLevel() != null)
				plugin.getLevel().onRedstoneChange(event);
		}
	}
/*
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (plugin.getBlockTrace().isFireSpreadingOut(event.getBlock().getLocation()) || plugin.isInsideLevel(event.getBlock().getLocation()))
			plugin.getBlockTrace().pushIgnitedBlock(event.getBlock());
		
	}*/

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBurn(BlockBurnEvent event) {
		if (plugin.isRunning())
			plugin.getBlockTrace().push(event.getBlock().getState());
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (TribuSign.isIt(plugin, event.getLines())) {
			if (event.getPlayer().hasPermission("tribu.signs.place")) {
				TribuSign sign = TribuSign.getObject(plugin, event.getBlock().getLocation(), event.getLines());
				if (sign != null)
					if (plugin.getLevel() != null) {
						if (plugin.getLevel().addSign(sign))
						Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.TribuSignAdded"));
					} else {
						Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.NoLevelLoaded"));
						Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.NoLevelLoaded2"));
						event.getBlock().setTypeId(0);
						event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
						event.setCancelled(true);
					}
			} else {
				Tribu.messagePlayer(event.getPlayer(),plugin.getLocale("Message.CannotPlaceASpecialSign"));
				event.getBlock().setTypeId(0);
				event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
				event.setCancelled(true);
			}
		}
	}
	/*
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent event)
	{
		if(plugin.isInsideLevel(event.getBlock().getLocation()))
		{
			plugin.getBlockTrace().push(event.getBlock(),true);
		}
	}
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event)
	{
		if(plugin.getBlockTrace().isWaterSpreadingOut(event.getToBlock().getLocation()) || plugin.isInsideLevel(event.getBlock().getLocation()))
		{
			plugin.getBlockTrace().pushMovingBlock(event.getBlock());
		}
	}
	*/
	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}