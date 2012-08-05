package graindcafe.tribu.Rollback;

import org.bukkit.World;

public class WrongBlockException extends Exception {
	/**
	 * Auto generated
	 */
	private static final long	serialVersionUID	= -3215458900454049498L;
	int							expected;
	int							get;
	int							x, y, z;
	World						world;

	public WrongBlockException(final int expected, final int get, final int x, final int y, final int z, final World world) {
		super("WrongBlock : " + expected + " != " + get);
		this.expected = expected;
		this.get = get;
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public int getExpected() {
		return expected;
	}

	public int getGet() {
		return get;
	}

	public World getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

}
