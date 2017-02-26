package com.basdado.randomwalker.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration2.Configuration;

public class OpenStreetMapConfiguration {
	
	private final double tileSize;
	private final String overpassURL;
	private final Set<String> highwayTagValues;
	
	public OpenStreetMapConfiguration(Configuration config) {
		tileSize = config.getDouble("OpenStreetMap.TileSize");
		overpassURL = config.getString("OpenStreetMap.OverpassURL");
		highwayTagValues = Collections.unmodifiableSet(new HashSet<>(config.getList(String.class, "OpenStreetMap.HighwayTagValues.Value")));
	}
	
	public double getTileSize() {
		return tileSize;
	}
	
	public String getOverpassURL() {
		return overpassURL;
	}
	
	public Set<String> getHighwayTagValues() {
		return highwayTagValues;
	}

}
