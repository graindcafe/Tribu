package graindcafe.tribu.Configuration;

import java.util.List;

public interface ValueReader {

	public boolean getBoolean(String key);

	public boolean getBoolean(String key, boolean defaultValue);

	public int getInt(String key);

	public double getDouble(String key);

	public List<Double> getDoubleList(String path);

	public String getString(String key);

	public Object get(String key);

}
