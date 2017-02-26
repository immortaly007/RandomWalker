package com.basdado.randomwalker.model;

import java.util.Map;

public interface ReadOnlyRoadMapNode {
	public LatLonCoordinate getPosition();
	public Map<Long, Double> getConnections();
	public boolean isReachable(long nodeId);
	public boolean hasConnections();
	public double getDistanceToConnectedNode(long nodeId);
}