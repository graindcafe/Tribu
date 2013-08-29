package graindcafe.tribu.Configuration;

import graindcafe.tribu.Signs.TribuSign;

import java.util.LinkedList;
import java.util.List;

public class CLIReader implements ValueReader {
	String cmd;

	public CLIReader(String command) {
		cmd = command.toLowerCase();

	}

	public boolean getBoolean(String key) {
		return cmd.contains("true");
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		if (cmd.contains("false"))
			return false;
		return (cmd.contains("true") || defaultValue);
	}

	public int getInt(String key) {
		return TribuSign.parseInt(cmd);
	}

	public double getDouble(String key) {
		return Double.parseDouble(cmd);
	}

	public List<Double> getDoubleList(String path) {
		List<Double> r = new LinkedList<Double>();
		for (String str : cmd.split(",")) {
			r.add(Double.parseDouble(str.trim()));
		}
		return r;
	}

	public String getString(String key) {
		return cmd;
	}

	public Object get(String key) {
		return cmd;
	}

}
