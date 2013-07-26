/*******************************************************************************
 * Copyright or � or Copr. Quentin Godron (2011)
 * 
 * cafe.en.grain@gmail.com
 * 
 * This software is a computer program whose purpose is to create zombie 
 * survival games on Bukkit's server. 
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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

	public Package(final int id) {
		this();
		addItem(id);
	}

	public Package(final Material m) {
		this();
		if (m != null) {
			addItem(m);
			setName(m.toString());
		}
	}

	public Package(final String name) {
		this();
		setName(name);
	}

	public boolean addItem(final int id) {
		return addItem(id, (short) 0, (short) 1);
	}

	public boolean addItem(final int id, final short subid) {
		return addItem(id, subid, (short) 1);
	}

	public boolean addItem(final int id, final short subid, final short number) {
		return addItem(id, subid, number, null);
	}

	public boolean addItem(final int id, final short subid, short number,
			final Map<Enchantment, Integer> enchantments) {
		final ItemStack is = new ItemStack(id);
		int max = is.getMaxStackSize();
		int clone = (int) Math.floor(number / max);
		number = (short) (number % max);
		if (number == 0) {
			number = (short) max;
			clone--;
		}
		is.setAmount(number);
		is.setDurability(subid);
		if (enchantments != null && !enchantments.isEmpty())
			for (final Map.Entry<Enchantment, Integer> entry : enchantments
					.entrySet())
				if (entry.getKey() != null)
					if (entry.getKey().canEnchantItem(is))
						is.addEnchantment(entry.getKey(), entry.getValue());

		return this.reallyAddItem(is, clone);
	}

	protected boolean reallyAddItem(final ItemStack item, int duplicates) {
		removeDuplicate(item);
		if (item == null || item.getAmount() == 0 || item.getTypeId() == 0)
			return false;
		else {
			ItemStack duplicata = item.clone();
			duplicata.setAmount(duplicata.getMaxStackSize());
			while (duplicates-- != 0)
				if (!this.pck.add(duplicata.clone()))
					return false;
			return pck.add(item);
		}
	}

	public boolean addItem(final ItemStack item) {
		return addItem(item, 1);
	}

	public boolean addItem(final ItemStack item, int number) {
		int max = item.getMaxStackSize();
		int clone = (int) Math.floor(number / max);
		number = (short) (number % max);
		if (number == 0) {
			number = max;
			clone--;
		}
		item.setAmount(number);
		return this.reallyAddItem(item, clone);
	}

	public boolean addItem(final Material m) {
		return addItem(m, (short) 0, (short) 1);
	}

	public boolean addItem(final Material m, final short subid) {
		return addItem(m, subid, (short) 1);
	}

	public boolean addItem(final Material m, final short subid,
			final short number) {
		return addItem(m, subid, number, null);
	}

	public boolean addItem(final Material m, final short subid,
			final short number, final Enchantment enchantment,
			final Integer enchLvl) {
		if (enchantment != null) {
			final HashMap<Enchantment, Integer> hm = new HashMap<Enchantment, Integer>();
			hm.put(enchantment, enchLvl);
			return addItem(m, subid, number, hm);
		}
		return addItem(m, subid, number, null);
	}

	public boolean addItem(final Material m, final short subid,
			final short amount, final Map<Enchantment, Integer> enchts) {
		if (m != null)
			return addItem(m.getId(), subid, amount, enchts);
		else
			return false;
	}

	public boolean addItem(final String name) {
		return addItem(name, (short) 0);
	}

	public boolean addItem(final String name, final short subid) {
		return addItem(name, subid, (short) 1);
	}

	public boolean addItem(final String name, final short subid,
			final short number) {
		try {
			return addItem(Integer.parseInt(name), subid, number);
		} catch (final NumberFormatException e) {
			return addItem(Material.getMaterial(name), subid, number);
		}
	}

	public void clear() {
		pck.clear();
		name = "";
	}

	public boolean deleteItem(final int id) {
		final ItemStack r = getItem(id);
		if (r == null)
			return false;
		else
			return pck.remove(r);
	}

	public boolean deleteItem(final int id, final short subid) {
		return pck.remove(getItem(id, subid));
	}

	public ItemStack getItem(final int id) {
		ItemStack r = null;
		for (final ItemStack i : pck)
			if (i.getTypeId() == id)
				if (r == null)
					r = i;
				else
					return null;
		return r;
	}

	public ItemStack getItem(final int id, final short subid) {
		for (final ItemStack i : pck)
			if (i.getTypeId() == id && i.getDurability() == subid)
				return i;
		return null;
	}

	public ItemStack getItem(final ItemStack item) {
		ItemStack r = null;
		for (final ItemStack i : pck)
			if (i.equals(item))
				return i;
			else if (i.getTypeId() == item.getTypeId()) {
				if (i.getDurability() == item.getDurability())
					return i;
				if (r == null)
					r = i;
				else
					return null;
			}

		return r;
	}

	public LinkedList<ItemStack> getItems(final int id) {
		final LinkedList<ItemStack> list = new LinkedList<ItemStack>();
		for (final ItemStack i : pck)
			if (i.getTypeId() == id)
				list.add(i);
		return list;
	}

	public LinkedList<ItemStack> getItemStacks() {
		return pck;
	}

	public String getLastItemName() {
		return pck.isEmpty() ? "" : pck.getLast().getData().getItemType()
				.toString();
	}

	public String getName() {
		return name;
	}

	public boolean isEmpty() {
		return pck.isEmpty();
	}

	private void removeDuplicate(final ItemStack item) {
		if (item == null)
			return;
		ItemStack r = null;
		for (final ItemStack i : pck)
			if (i.equals(item))
				r = i;
			else if (i.getTypeId() == item.getTypeId())
				if (i.getDurability() == item.getDurability())
					r = i;

		if (r != null)
			pck.remove(r);
	}

	public void setName(final String name) {
		this.name = name;
	}

	// Exemple{42:10x64,13:01x1}
	@Override
	public String toString() {
		String s = new String(name);
		s += " { ";
		for (final ItemStack i : pck)
			s += String.valueOf(i.getData().getItemType().toString()) + ':'
					+ String.valueOf(i.getDurability()) + 'x'
					+ String.valueOf(i.getAmount()) + ' ';
		s += '}';
		return s;
	}

}
