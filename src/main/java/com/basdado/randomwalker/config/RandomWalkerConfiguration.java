package com.basdado.randomwalker.config;

import org.apache.commons.configuration2.Configuration;

import com.basdado.randomwalker.model.LatLonCoordinate;

public class RandomWalkerConfiguration {

	private final LatLonCoordinate initialPosition;
	private final double defaultSpeed;
	
	public RandomWalkerConfiguration(Configuration config) {
		
		this.initialPosition = new LatLonCoordinate(
				config.getDouble("InitialPosition.Latitude"),
				config.getDouble("InitialPosition.Longitude")
				);
		this.defaultSpeed = config.getDouble("DefaultSpeed");
		
	}
	
	public LatLonCoordinate getInitialPosition() {
		return initialPosition;
	}
	
	public double getDefaultSpeed() {
		return defaultSpeed;
	}
	
}
