package com.basdado.randomwalker.controller;

import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.basdado.randomwalker.model.ReadOnlyRoadMap;
import com.basdado.randomwalker.model.ReadOnlyRoadMapNode;

public class RandomWalkHeuristic implements WalkHeuristic {

	Random rnd = new Random();
	
	@Override
	public Long decideNextNode(Long currentNodeId, ReadOnlyRoadMap map, Deque<Long> nodeHistory) {
		
		ReadOnlyRoadMapNode node = map.getNode(currentNodeId);
		Map<Long, Double> connections = node.getConnections();
		
		Set<Long> options = new HashSet<>();
		long backupOption = -1;
		int backupOptionRating = Integer.MAX_VALUE; // lower is better
		
		for (Long option : connections.keySet()) {
			int optionRating = indexOf(nodeHistory, option);
			if (optionRating >= 0) {
				if (optionRating < backupOptionRating) {
					backupOption = option;
					backupOptionRating = optionRating;
				}
			} else {
				options.add(option);
			}
		}
		
		if (!options.isEmpty()) {
			int i = 0;
			int nodeIdx = rnd.nextInt(connections.size());
			
			for (Long nodeId: connections.keySet()) {
				if (i == nodeIdx) {
					return nodeId;
				}
				i++;
			}
		} else if (backupOption != -1) {
			return backupOption;
		}
		throw new RuntimeException("Could not find any node to go to");
		
	}

	private static <T> int indexOf(Deque<T> entries, T value) {
		int i = 0;
		for (T entry : entries) {
			if (entry != null && entry.equals(value)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	
}
