package com.basdado.randomwalker.controller;

import java.util.Deque;

import com.basdado.randomwalker.model.ReadOnlyRoadMap;

public interface WalkHeuristic {
	
	/**
	 * Should decide the next node, based on the current node index, a map (locations and connection between nodes),
	 * and a set of recently visited nodes, ordered from least-recently visited to most recently visited.
	 * @param currentNodeId
	 * @param map
	 * @param nodeHistory
	 * @return
	 */
	Long decideNextNode(Long currentNodeId, ReadOnlyRoadMap map, Deque<Long> nodeHistory);
	
}
