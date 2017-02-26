package com.basdado.randomwalker.model;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;

public class RandomWalkerData {
	
	private final int id;
	private final LocalDateTime created;
	private final int maxLastNodeLength;
	
	private Deque<Long> nodeHistory;
	private double nodeProgress;
	private long nextNodeId;
	private double speed;
	private double lastUpdateAge;
	
	public RandomWalkerData(int id, long lastNodeId, long nextNodeId, int maxLastNodeLength, double speed, LocalDateTime created) {
		this.id = id;
		this.nodeHistory = new LinkedList<>();
		this.nodeProgress = 0;
		this.nodeHistory.addLast(lastNodeId);
		this.nextNodeId = nextNodeId;
		this.maxLastNodeLength = maxLastNodeLength;
		this.speed = speed;
		this.lastUpdateAge = 0;
		this.created = created;
	}
	
	public int getId() {
		return id;
	}
	
	public long getPreviousNodeId() {
		return nodeHistory.getLast();
	}
	
	public Deque<Long> getNodeHistory() {
		return nodeHistory;
	}
	
	public long getNextNodeId() {
		return nextNodeId;
	}
	
	public double getNodeProgress() {
		return nodeProgress;
	}
	
	public void setNodeProgress(double nodeProgress) {
		this.nodeProgress = nodeProgress;
	}
	
	public LocalDateTime getCreated() {
		return created;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getLastUpdateAge() {
		return lastUpdateAge;
	}
	
	public void setLastUpdateAge(double lastUpdateAge) {
		this.lastUpdateAge = lastUpdateAge;
	}
	
	public void moveToNextNode(long nodeId, double progress) {
		nodeHistory.addLast(nextNodeId);
		if (nodeHistory.size() > maxLastNodeLength) {
			nodeHistory.removeFirst();
		}
		nextNodeId = nodeId;
		this.nodeProgress = progress;
	}
}
