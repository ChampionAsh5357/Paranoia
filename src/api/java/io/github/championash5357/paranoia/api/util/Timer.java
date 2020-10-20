package io.github.championash5357.paranoia.api.util;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class Timer implements ITickable {
	private static final Random RANDOM = new Random();
	private final int threshold, randomness;
	private final Function<Integer, Double> modifier;
	private final Consumer<ServerPlayerEntity> result;
	private int tick, currentThreshold;

	public Timer(int threshold, Consumer<ServerPlayerEntity> result) {
		this(threshold, 0, result);
	}
	
	public Timer(int threshold, int randomness, Consumer<ServerPlayerEntity> result) {
		this(threshold, randomness, (a) -> 1.0, result);
	}
	
	public Timer(int threshold, int randomness, Function<Integer, Double> modifier, Consumer<ServerPlayerEntity> result) {
		this.threshold = threshold;
		this.randomness = randomness;
		this.modifier = modifier;
		this.result = result;
		this.calculateThreshold(null);
	}

	@Override
	public void tick(ServerPlayerEntity player, ISanity sanity) {
		this.tick++;
		if(this.tick > this.currentThreshold) {
			this.tick = 0;
			this.calculateThreshold(sanity);
			this.result.accept(player);
		}
	}
	
	private void calculateThreshold(@Nullable ISanity sanity) {
		double modifier = sanity != null ? this.modifier.apply(sanity.getSanity()) : 1.0d;
		this.currentThreshold = (int) Math.max(10, modifier * (this.threshold - this.randomness + RANDOM.nextInt(this.randomness * 2)));
	}
}