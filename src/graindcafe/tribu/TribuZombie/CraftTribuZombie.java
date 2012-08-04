/*
 * Thanks to xXKeyleXx (plugin MyWolf) for the inspiration
 */

package graindcafe.tribu.TribuZombie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import graindcafe.tribu.Tribu;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;


public class CraftTribuZombie extends CraftZombie implements Zombie {
	private HashMap<Player, Integer> playerDamage;
	private Integer maxAccrued,total,maxDamage;
	private Player bestAttacker;
    public CraftTribuZombie(CraftServer server, EntityTribuZombie entity) {
        super(server, entity);
        playerDamage=new HashMap<Player,Integer>();
        maxAccrued=0;
        total=0;
        maxDamage=0;
    }
    

    @Override
    public EntityTribuZombie getHandle() {
        return (EntityTribuZombie) entity;
    }

    @Override
    public String toString() {
        return "CraftTribuZombie";
    }
    public void setNoAttacker()
    {
    	playerDamage.clear();
    	bestAttacker=null;
    	this.setTarget(null);
    }
    @Override
    public void setTarget(LivingEntity target)
    {
    	Logger.getLogger("Minecraft").info("Fuck you, you and your "+target);
    }
    
    public void setBestAttacker(Player p)
    {
    	bestAttacker=p;
    }
    public void setMaxAttack(int m)
    {
    	this.maxAccrued=m;
    }
    public void setTotalAttack(int t)
    {
    	this.total=t;
    }
    public void setMaxAccruedAttack(int accrued)
    {
    	this.maxAccrued=accrued;
    }
    public void addAttack(Player p, int damage)
    {
    	if(maxDamage<damage)
    		maxDamage=damage;
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
    	if(maxAccrued<i)
    	{
    		maxAccrued=i;
    		bestAttacker=p;
    	}
    	total+=damage;
    }
    public Player getFirstAttacker()
    {
    	if(this.playerDamage.isEmpty())
    		return null;
    	return this.playerDamage.keySet().iterator().next();
    }
    public Player getLastAttacker()
    {
    	if(this.playerDamage.isEmpty())
    		return null;
    	Iterator<Player> i=this.playerDamage.keySet().iterator();
    	
    	Player beforeLast=i.next();
    	Player p;
    	do
    		p=i.next();
    	while(p!=null);
    	return beforeLast;
    	
    }
    public Map<Player,Float> getAttackersPercentage()
    {
    	HashMap<Player,Float> r=new HashMap<Player,Float>();
    	
    	for(Entry<Player,Integer> e : playerDamage.entrySet())
    	{
    		r.put(e.getKey(),  ( (float)e.getValue())/ ((float)total));
    	}
    	
    	return r;
    }
    public Player getBestAttacker()
    {
    	/*
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	list.addAll(playerDamage.values());
    	Collections.sort(list, Collections.reverseOrder());
    	Player p=null;
    	Integer max=list.get(0);
    	for(Entry<Player,Integer> e : playerDamage.entrySet())
    	{
    		if(e.getValue().equals(max))
    		{
    			p=e.getKey();
    			break;
    		}
    	}
    	return p;*/
    	return bestAttacker;
    }
    public EntityType getType() {
        return EntityType.ZOMBIE;
    }
    public static Entity spawn(Tribu plugin, Location pos) throws CannotSpawnException {
    	if(!pos.getChunk().isLoaded())
    		pos.getChunk().load();
    			
    	EntityTribuZombie tz=EntityTribuZombie.spawn(plugin, (WorldServer) ((CraftWorld)pos.getWorld()).getHandle(), pos.getX(), pos.getY(), pos.getZ());
    	if(tz==null)
    		return null;
    	else
    		return tz.getBukkitEntity();
	}
}
