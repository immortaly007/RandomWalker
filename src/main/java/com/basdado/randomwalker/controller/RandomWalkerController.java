package com.basdado.randomwalker.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.basdado.randomwalker.config.ConfigService;
import com.basdado.randomwalker.data.RandomWalkerRepo;
import com.basdado.randomwalker.model.LatLonCoordinate;
import com.basdado.randomwalker.model.RandomWalkerData;
import com.basdado.randomwalker.model.ReadOnlyRoadMap;
import com.basdado.randomwalker.model.ReadOnlyRoadMapNode;
import com.basdado.randomwalker.util.CoordinateUtil;

@Singleton
public class RandomWalkerController {
	
	private static final double NANOS_PER_SECOND = 1000000000;
	
	@Inject private Logger logger;
	
	@Inject private ConfigService configService;
	@Inject private RandomWalkerRepo randomWalkerRepo;
	@Inject private RoadMapController roadMapController;

	private WalkHeuristic heuristic;
	
	private int randomWalkerId;
	
	@PostConstruct
	private void init() {
		
		LatLonCoordinate pos = configService.getRandomWalkerConfig().getInitialPosition();
		roadMapController.loadTilesAround(pos);
		
		ReadOnlyRoadMap roadMap = roadMapController.getRoadMap();
		
		List<Long> nearbyNodes = roadMap.findNodesNear(pos, 300);
		if (nearbyNodes.isEmpty()) {
			throw new EJBException("No nodes near initial position. Change configuration!");
		}
		Long nearestNodeId = nearbyNodes.get(0);
		
		heuristic = new RandomWalkHeuristic();
		long nextNodeId = heuristic.decideNextNode(nearestNodeId, roadMap, new LinkedList<>());
		
		RandomWalkerData randomWalker = randomWalkerRepo.addRandomWalker(nearestNodeId, nextNodeId, configService.getRandomWalkerConfig().getDefaultSpeed());
		randomWalkerId = randomWalker.getId();
		
	}
	

	
	public LatLonCoordinate getCurrentPosition() {
		RandomWalkerData randomWalker = randomWalkerRepo.findById(randomWalkerId);
		updateWalker(randomWalker);
		LatLonCoordinate currentPosition = getWalkerPosition(randomWalker);
		return currentPosition;
	}
	
	private void updateWalker(RandomWalkerData randomWalker) {
		// So it can walk for approximately 292 years before we wraps around the long
		long ageNanos = randomWalker.getCreated().until(LocalDateTime.now(), ChronoUnit.NANOS);
		double currentAge = (double)ageNanos / NANOS_PER_SECOND;
		double lastUpdateAge = randomWalker.getLastUpdateAge();
		double movementTime = currentAge - lastUpdateAge;
		double movementDistance = randomWalker.getSpeed() * movementTime;
		
		ReadOnlyRoadMap roadMap = roadMapController.getRoadMap();
		
		while(movementDistance > 0) {
			
			ReadOnlyRoadMapNode previousNode = roadMap.getNode(randomWalker.getPreviousNodeId());
			
			// Previous and next node are always connected, so we can use that information
			double nextNodeDistance = (1 - randomWalker.getNodeProgress()) * previousNode.getDistanceToConnectedNode(randomWalker.getNextNodeId());
			if (nextNodeDistance < movementDistance) {
				
				long nextNodeId = heuristic.decideNextNode(randomWalker.getNextNodeId(), roadMap, randomWalker.getNodeHistory());
				randomWalker.moveToNextNode(nextNodeId, 0);
				ReadOnlyRoadMapNode nextNode = roadMap.getNode(nextNodeId);
				roadMapController.loadTilesAround(nextNode.getPosition());
								
			} else {
				double nodeProgress = randomWalker.getNodeProgress() + (movementDistance / nextNodeDistance);
				randomWalker.setNodeProgress(nodeProgress);
			}
			
			movementDistance -= nextNodeDistance;
			
		}
		
		
		randomWalker.setLastUpdateAge(currentAge);
	
	}
	
	private LatLonCoordinate getWalkerPosition(RandomWalkerData randomWalker) {
		
		ReadOnlyRoadMap roadMap = roadMapController.getRoadMap();
		
		ReadOnlyRoadMapNode previousNode = roadMap.getNode(randomWalker.getPreviousNodeId());
		ReadOnlyRoadMapNode nextNode = roadMap.getNode(randomWalker.getNextNodeId());
		
		return CoordinateUtil.interpolate(previousNode.getPosition(), nextNode.getPosition(), randomWalker.getNodeProgress());
		
	}	


}
