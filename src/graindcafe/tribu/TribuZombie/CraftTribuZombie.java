/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import graindcafe.tribu.Tribu;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;


public class CraftTribuZombie extends CraftZombie implements Zombie {
	private HashMap<Player, Integer> playerDamage;
    public CraftTribuZombie(CraftServer server, EntityTribuZombie entity) {
        super(server, entity);
        playerDamage=new HashMap<Player,Integer>();
    }

    @Override
    public EntityTribuZombie getHandle() {
        return (EntityTribuZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftTribuZombie";
    }
    public void addAttack(Player p, int damage)
    {
    	Integer i;
    	if(this.playerDamage.containsKey(p))
    	{
    		
    		i=this.playerDamage.get(p);
    		i+=damage;
    	}
    	else
    	{
    		i=new Integer(damage);
    		this.playerDamage.put(p, i);
    	}
    }
    public Player getBestAttacker()
    {
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	list.addAll(playerDamage.values());
    	Collections.sort(list, Collections.reverseOrder());
    	Player p=null;
    	for(Entry<Player,Integer> e : playerDamage.entrySet())
    	{
    		if(e.getValue().equals(list.get(0)))
    		{
    			p=e.getKey();
    			break;
    		}
    	}
    	return p;
    }
    public EntityType getType() {
        return EntityType.ZOMBIE;
    }
    public static Entity spawn(Tribu plugin, Location pos) {
    	if(!pos.getChunk().isLoaded())
    		pos.getChunk().load();
    			
    	EntityTribuZombie tz=EntityTribuZombie.spawn(plugin, (WorldServer) ((CraftWorld)pos.getWorld()).getHandle(), pos.getX(), pos.getY(), pos.getZ());
    	if(tz==null)
    		return null;
    	else
    		return tz.getBukkitEntity();
	}
}
