package graindcafe.tribu.BlockTrace;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class GravitySubject extends DynamicNode {
	Block falling;
	boolean isStable=false;
	
	protected GravitySubject(Block falling)
	{
		this.falling=falling;
		this.location=falling.getLocation();
	}
	@Override
	public void update() {
		if(!isStable)
		{
			this.location=location.subtract(0,1,0);
			this.id=this.location.getBlock().getTypeId();
			this.data=this.location.getBlock().getData();
		}
		
	}

	@Override
	public boolean isStable() {
		byte y=0;
		Location below=location.clone();
		BlockTracer.debugMsg("Is it stable ?");
		do
		{
			y++;
			below=below.subtract(0,y,0);
			if(!isSolid(below.getBlock().getState()))
			{
				BlockTracer.debugMsg("It is falling throw "+below.getBlock().getType().toString()+ " ! (after "+y+" blocks)");
				return false;
			}
		}
		while(below.getBlockY()>0 && DynamicNode.isSubjectToGravity(below.getBlock().getState()));
		isStable=true;
		BlockTracer.debugMsg("It is stable ! (after "+y+" blocks)");
		return true;
	}

	@Override
	public Node getFinalNode() {
		return new Node(location.clone(),id,data);
	}

}
