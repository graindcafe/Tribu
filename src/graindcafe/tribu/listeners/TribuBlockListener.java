package graindcafe.tribu.listeners;
import graindcafe.tribu.Tribu;
import graindcafe.tribu.signs.TribuSign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
			if (event.getPlayer().isOp()) {
				plugin.getLevel().removeSign(event.getBlock().getLocation());
			} else {
				if (event.getPlayer() != null)
					event.getPlayer().sendMessage(plugin.getLocale("Message.ProtectedBlock"));
				TribuSign.update((Sign) event.getBlock().getState());
				event.setCancelled(true);

			}
		} else if (plugin.isRunning() && plugin.isPlaying(event.getPlayer()))
			plugin.getBlockTrace().push(event.getBlock(), true);
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (plugin.isRunning() && plugin.isPlaying(event.getPlayer()))
			plugin.getBlockTrace().push(event.getBlockReplacedState().getTypeId(), event.getBlockReplacedState().getData(),
					event.getBlock().getLocation(), false);
	}
	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (plugin.getLevel() != null && plugin.isRunning())
			plugin.getLevel().onRedstoneChange(event);
	}
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (TribuSign.isIt(plugin, event.getLines())) {
			if (event.getPlayer().isOp()) {
				TribuSign sign = TribuSign.getObject(plugin, event.getBlock().getLocation(), event.getLines());
				if (sign != null)
					if (plugin.getLevel() != null) {
						if (plugin.getLevel().addSign(sign))
							event.getPlayer().sendMessage(plugin.getLocale("Message.TribuSignAdded"));
					} else {
						event.getPlayer().sendMessage(plugin.getLocale("Message.NoLevelLoaded"));
						event.getPlayer().sendMessage(plugin.getLocale("Message.NoLevelLoaded2"));
						event.getBlock().setTypeId(0);
						event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
						event.setCancelled(true);
					}
			} else {
				event.getPlayer().sendMessage(plugin.getLocale("Message.CannotPlaceASpecialSign"));
				event.getBlock().setTypeId(0);
				event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
				event.setCancelled(true);
			}
		}
	}
	public void registerEvents(PluginManager pm) {
		pm.registerEvents(this, plugin);
	}
}