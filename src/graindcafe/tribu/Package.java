package graindcafe.tribu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Package {
	LinkedList<ItemStack> pck;
	String name;

	public Package() {
		pck = new LinkedList<ItemStack>();
	}

	public Package(String name) {
		this();
		setName(name);
	}

	public Package(Material m) {
		this();
		if (m != null) {
			addItem(m);
			setName(m.toString());
		}
	}

	public Package(int id) {
		this();
		addItem(id);
	}

	public boolean isEmpty() {
		return pck.isEmpty();
	}

	public boolean addItem(int id) {
		return addItem(id, (short) 0, (short) 1);
	}

	public boolean addItem(int id, short subid) {
		return addItem(id, subid, (short) 1);
	}

	public boolean addItem(int id, short subid, short number) {
		return addItem(id, subid, number,null);
	}

	public boolean addItem(String name) {
		return addItem(name, (short) 0);
	}

	public boolean addItem(String name, short subid) {
		return addItem(name, subid, (short) 1);
	}

	public boolean addItem(String name, short subid, short number) {
		try {
			return addItem(Integer.parseInt(name), subid, number);
		} catch (NumberFormatException e) {
			return addItem(Material.getMaterial(name), subid, number);
		}
	}

	public boolean addItem(Material m) {
		return addItem(m, (short) 0, (short) 1);
	}

	public boolean addItem(Material m, short subid) {
		return addItem(m, subid, (short) 1);
	}

	public boolean addItem(Material m, short subid, short number) {
		return addItem(m, subid, number, null);
	}

	public boolean addItem(Material m, short subid, short number, Enchantment enchantment, Integer enchLvl) {
		if (enchantment != null) {
			HashMap<Enchantment, Integer> hm = new HashMap<Enchantment, Integer>();
			hm.put(enchantment, enchLvl);
			return addItem(m, subid, number, hm);
		}
		return addItem(m, subid, number, null);
	}

	public boolean addItem(Material m, short subid, short amount, Map<Enchantment, Integer> enchts) {
		if (m != null) {
			return addItem(m.getId(), subid, amount, enchts);
		} else
			return false;
	}

	public boolean addItem(int id, short subid, short number, Map<Enchantment, Integer> enchantments) {
		ItemStack is = new ItemStack(id);
		is.setAmount(number);
		is.setDurability(subid);
		if (enchantments != null && !enchantments.isEmpty())
			for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
				if (entry.getKey() != null)
					if(entry.getKey().canEnchantItem(is))
						is.addEnchantment(entry.getKey(), entry.getValue());
			}
		return this.addItem(is);
	}

	public boolean addItem(ItemStack item, int number) {
		item.setAmount(number);
		return this.addItem(item);
	}

	public boolean addItem(ItemStack item) {
		removeDuplicate(item);
		return item == null || item.getAmount() == 0 || item.getTypeId() == 0 ? false : pck.add(item);
	}

	public ItemStack getItem(int id, short subid) {
		for (ItemStack i : pck)
			if (i.getTypeId() == id && i.getDurability() == subid)
				return i;
		return null;
	}
	public ItemStack getItem(ItemStack item)
	{
		ItemStack r = null;
		for (ItemStack i : pck)
			if(i.equals(item))
				return i;
			else if (i.getTypeId() == item.getTypeId()) {
				if(i.getDurability() == item.getDurability())
					return i;
				if (r == null)
					r = i;
				else
					return null;
			}
				
		return r;
	}
	public ItemStack getItem(int id) {
		ItemStack r = null;
		for (ItemStack i : pck)
			if (i.getTypeId() == id) {
				if (r == null)
					r = i;
				else
					return null;
			}
		return r;
	}

	public String getLastItemName() {
		return pck.isEmpty() ? "" : pck.getLast().getData().getItemType().toString();
	}

	public LinkedList<ItemStack> getItems(int id) {
		LinkedList<ItemStack> list = new LinkedList<ItemStack>();
		for (ItemStack i : pck)
			if (i.getTypeId() == id)
				list.add(i);
		return list;
	}

	public boolean deleteItem(int id, short subid) {
		return pck.remove(getItem(id, subid));
	}

	public boolean deleteItem(int id) {
		ItemStack r = getItem(id);
		if (r == null)
			return false;
		else
			return pck.remove(r);
	}
	private void removeDuplicate(ItemStack item)
	{
		if(item==null) return;
		ItemStack r = null;
		for (ItemStack i : pck)
			if(i.equals(item))
				r=i;
			else if (i.getTypeId() == item.getTypeId())
				if(i.getDurability() == item.getDurability())
					r=i;
			
		if(r!=null)
		pck.remove(r);
	}
	public LinkedList<ItemStack> getItemStacks() {
		return this.pck;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Exemple{42:10x64,13:01x1}
	public String toString() {
		String s = new String(name);
		s += " { ";
		for (ItemStack i : pck)
			s += String.valueOf(i.getData().getItemType().toString()) + ':' + String.valueOf(i.getDurability()) + 'x' + String.valueOf(i.getAmount()) + ' ';
		s += '}';
		return s;
	}

}
