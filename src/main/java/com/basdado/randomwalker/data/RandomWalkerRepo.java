package com.basdado.randomwalker.data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.basdado.randomwalker.model.RandomWalkerData;

@Singleton
public class RandomWalkerRepo {

	private Map<Integer, RandomWalkerData> randomWalkers;
	private AtomicInteger nextWalkerIdx;
	
	public RandomWalkerRepo() {
		randomWalkers = new ConcurrentHashMap<>();
		this.nextWalkerIdx = new AtomicInteger(0);
	}
	
	@Lock(LockType.READ)
	public RandomWalkerData addRandomWalker(long lastNodeId, long nextNodeId, double speed) {
		
		RandomWalkerData walker = new RandomWalkerData(nextWalkerIdx.getAndIncrement(), lastNodeId, nextNodeId, 1000, speed, LocalDateTime.now());
		randomWalkers.put(walker.getId(), walker);
		
		return walker;
	}
	
	@Lock(LockType.READ)
	public RandomWalkerData findById(int id) {
		return randomWalkers.get(id);
	}
}
