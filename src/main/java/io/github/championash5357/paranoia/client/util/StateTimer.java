package io.github.championash5357.paranoia.client.util;

import net.minecraft.block.BlockState;

public class StateTimer {

	private final BlockState state;
	private int timeRemaining;
	public boolean remove;
	
	public StateTimer(BlockState state, int timeRemaining) {
		this.state = state;
		this.timeRemaining = timeRemaining;
	}

	public BlockState getState() {
		return this.state;
	}
	
	public void tick() {
		this.timeRemaining--;
		if(this.timeRemaining <= 0) this.remove = true;
	}
}
