package com.basdado.randomwalker.model;

import java.util.List;

public interface ReadOnlyRoadMap {

	ReadOnlyRoadMapNode getNode(Long id);
	
	/**
	 * Returns all nodes that are within maxDistance of the given position.
	 * @param pos Coordinate
	 * @param maxDistance The maximum distance nodes may have.
	 * @return
	 */
	List<Long> findNodesNear(LatLonCoordinate pos, double maxDistance);
	
	/**
	 * @return The longest distance between two neighboring nodes on the railway map.
	 */
	double getLongestNodeDist();
	

}
