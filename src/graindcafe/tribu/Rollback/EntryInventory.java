package graindcafe.tribu.Rollback;

import net.minecraft.server.v1_6_R2.Block;
import net.minecraft.server.v1_6_R2.IInventory;
import net.minecraft.server.v1_6_R2.TileEntity;
import net.minecraft.server.v1_6_R2.TileEntityChest;
import net.minecraft.server.v1_6_R2.TileEntityDispenser;
import net.minecraft.server.v1_6_R2.TileEntityFurnace;

import org.apache.commons.lang.Validate;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.inventory.InventoryHolder;

public class EntryInventory extends EntryBlockState {
	CraftItemStack[]	items;

	public EntryInventory(final BlockState inventoryHolder) throws WrongBlockException {
		super(inventoryHolder);

		if (inventoryHolder instanceof InventoryHolder) {
			final InventoryHolder inventory = (InventoryHolder) inventoryHolder;
			final org.bukkit.inventory.ItemStack[] bItems = inventory.getInventory().getContents();
			items = new CraftItemStack[bItems.length];
			for (int i = 0; i < bItems.length; i++)

				if (bItems[i] != null) items[i] = CraftItemStack.asCraftCopy(bItems[i]);
		} else
			throw new WrongBlockException(Block.CHEST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
	}

	private void fillContainer(final IInventory inventory) {
		int max;
		if (inventory.getSize() < items.length) {
			ChunkMemory.debugMsg("Container is too short !");
			max = inventory.getSize();
		} else
			max = items.length;
		for (int i = 0; i < max; i++)
			if (items[i] != null) inventory.setItem(i, CraftItemStack.asNMSCopy(items[i]));
	}

	@Override
	public void restore() throws WrongBlockException {
		Validate.notNull(world, "World is null");
		Validate.notNull(items, "Items are null");
		IInventory inventory = ((IInventory) world.getTileEntity(x, y, z));
		if (inventory == null) {
			ChunkMemory.debugMsg("Null inventory tile entity");
			final int typeId = world.getTypeId(x, y, z);
			if (typeId == Block.CHEST.id)
				inventory = new TileEntityChest();
			else if (typeId == Block.FURNACE.id)
				inventory = new TileEntityFurnace();
			else if (typeId == Block.DISPENSER.id)
				inventory = new TileEntityDispenser();
			else
				throw new WrongBlockException(Block.CHEST.id, world.getTypeId(x, y, z), x, y, z, world.getWorld());
			if (typeId == Block.CHEST.id) ((TileEntityChest) inventory).i();
			fillContainer(inventory);
			world.setTileEntity(x, y, z, (TileEntity) inventory);
		} else
			fillContainer(inventory);
		world.notify(x, y, z);
	}

}
