package graindcafe.tribu;

import org.bukkit.Location;

public class MyBlock {
	int id;
	Location location;
	public MyBlock(int id,Location loc)
	{
		this.id=id;
		this.location=loc;
	}
	public MyBlock(Location loc)
	{
		this.location=loc;
		this.id=loc.getBlock().getTypeId();
	}
	public boolean Reverse()
	{
		return location.getBlock().setTypeId(id);
	}
}
