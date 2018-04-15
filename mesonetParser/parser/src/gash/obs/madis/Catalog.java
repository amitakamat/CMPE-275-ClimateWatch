package gash.obs.madis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Catalog {
	private HashMap<String, Station> map = new HashMap<String, Station>();

	public boolean validateID(String id) {
		return (map.containsKey(id));
	}

	public boolean addStation(Station station) {
		if (station == null || station.getId() == null)
			return false;

		if (map.containsKey(station.getId()))
			return true;

		map.put(station.getId(), station);
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("# id,name,mesonet,lat,lon,elevation,agl,cit,state,country,active\n");
		for (Station s : map.values()) {
			sb.append(s.id).append(",");
			sb.append(s.name).append(",");
			sb.append(s.mesonet).append(",");
			sb.append(s.lat).append(",");
			sb.append(s.lon).append(",");
			sb.append(s.elevation).append(",");
			sb.append(s.agl).append(",");
			sb.append(s.city).append(",");
			sb.append(s.state).append(",");
			sb.append(s.country).append(",");
			sb.append(s.active).append("\n");
		}

		return sb.toString();
	}

	public boolean load(File catFile) {
		if (catFile == null || !catFile.exists())
			return false;

		// TODO open file, read contents

		return false;
	}

	public boolean save(File catFile) {
		if (catFile == null)
			return false;

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(catFile));
			w.write(this.toString());
			w.close();
		} catch (IOException e) {
			// TODO add appropriate error handling
			e.printStackTrace();
		}

		return false;
	}

}
