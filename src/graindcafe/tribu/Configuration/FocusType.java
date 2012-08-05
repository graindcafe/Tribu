package graindcafe.tribu.Configuration;

public enum FocusType implements TribuEnum {
	None, NearestPlayer, RandomPlayer, InitialSpawn, DeathSpawn;

	public static FocusType fromString(final String name) {
		if (name == null)
			return FocusType.None;
		else if (name.toLowerCase().startsWith("near"))
			return FocusType.NearestPlayer;
		else if (name.toLowerCase().startsWith("rand"))
			return FocusType.RandomPlayer;
		else if (name.toLowerCase().startsWith("init"))
			return FocusType.InitialSpawn;
		else if (name.toLowerCase().startsWith("death"))
			return FocusType.DeathSpawn;
		else
			return FocusType.None;
	}

	@Override
	public String toString() {
		return name().toString();
	}
}
