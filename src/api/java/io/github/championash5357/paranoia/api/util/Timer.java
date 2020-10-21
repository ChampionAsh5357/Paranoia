/*
 * Paranoia
 * Copyright (C) 2020 ChampionAsh5357
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation version 3.0 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.championash5357.paranoia.api.util;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * A basic implementation of {@link ITickable}.
 * Executes the inputted result once the threshold
 * has been met. Repeats until removed.
 */
public class Timer implements ITickable {
	private static final Random RANDOM = new Random();
	private final int threshold, randomness;
	private final Function<Integer, Double> modifier;
	private final Consumer<ServerPlayerEntity> result;
	private int tick, currentThreshold;

	/**
	 * A timer constructor.
	 * 
	 * @param threshold The number of ticks before the result is executed.
	 * @param result A consumer of a {@link ServerPlayerEntity} that executes once the threshold is reached.
	 */
	public Timer(int threshold, Consumer<ServerPlayerEntity> result) {
		this(threshold, 0, result);
	}
	
	/**
	 * A timer constructor.
	 * 
	 * @param threshold The number of ticks before the result is executed.
	 * @param randomness The number of ticks that can add to or remove from the current threshold.
	 * @param result A consumer of a {@link ServerPlayerEntity} that executes once the threshold is reached.
	 */
	public Timer(int threshold, int randomness, Consumer<ServerPlayerEntity> result) {
		this(threshold, randomness, (a) -> 1.0, result);
	}
	
	/**
	 * A timer constructor.
	 * 
	 * @param threshold The number of ticks before the result is executed.
	 * @param randomness The number of ticks that can add to or remove from the current threshold.
	 * @param modifier A modifier to multiply to the calculated threshold with randomness. Takes in the current sanity level.
	 * @param result A consumer of a {@link ServerPlayerEntity} that executes once the threshold is reached.
	 */
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