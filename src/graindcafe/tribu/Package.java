package graindcafe.tribu;

import java.util.LinkedList;

import org.bukkit.inventory.ItemStack;

public class Package {
	LinkedList<ItemStack> pck;
	String name;
	public Package()
	{
		pck = new LinkedList<ItemStack>();
	}
	
	public boolean addItem(int id)
	{
		return addItem(id,1,(short) 1);
	}
	public boolean addItem(int id,int subid)
	{
		return addItem(id,subid,(short) 1);
	}
	public boolean addItem(int id, int subid, short number)
	{
		return pck.add(new ItemStack(id,subid,number));
	}
	public ItemStack getItem(int id,int subid)
	{
		for(ItemStack i : pck)
			if(i.getTypeId()==id && i.getData().getData()==((byte)subid))
				return i;
		return null;
	}
	public LinkedList<ItemStack> getItems(int id)
	{
		LinkedList<ItemStack> list=new LinkedList<ItemStack>();
		for(ItemStack i : pck)
			if(i.getTypeId()==id)
				list.add(i);
		return list;
	}
	public boolean deleteItem(int id, int subid)
	{
		return pck.remove(getItem(id,subid));
	}
	public void setName(String name)
	{
		this.name=name;
	}
	// Exemple{42:10x64,13:01x1}
	public String toString()
	{
		String s= new String(name);
		s+='{';
		for(ItemStack i : pck)
			s=String.valueOf(i.getTypeId())+':'+String.valueOf(i.getData().getData())+'x'+String.valueOf(i.getAmount())+',';
		s+='}';
		return s;
	}
}