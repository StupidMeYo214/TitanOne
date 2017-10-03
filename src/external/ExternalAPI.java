package external;

import java.util.List;

import entity.Item;

public interface ExternalAPI {
	/**
	 * @param latitude of interested event area
	 * @param longitude of interested event ares
	 * @param keywords
	 * @return JSONArray Object
	 * */
	public List<Item> search(double lat, double lon, String term);
}
