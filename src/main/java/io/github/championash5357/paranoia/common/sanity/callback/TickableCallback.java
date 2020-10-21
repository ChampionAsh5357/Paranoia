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

package io.github.championash5357.paranoia.common.sanity.callback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import io.github.championash5357.paranoia.api.callback.ICallback;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.util.ITickable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class TickableCallback implements ICallback {

	private final Map<ResourceLocation, Pair<Integer, ITickable>> tickables = new HashMap<>();
	private final Map<Integer, Set<ResourceLocation>> active = new HashMap<>();

	public TickableCallback() {
		SanityCallbacks.getTickables().forEach(this.tickables::put);
	}

	@Override
	public void call(ServerPlayerEntity player, ISanity inst, int sanity, int prevSanity, Phase phase) {
		if(phase == Phase.STOP) this.active.values().stream().flatMap(Set<ResourceLocation>::stream).forEach(inst::removeTemporaryTickable);
		else if(phase == Phase.START) {
			this.tickables.entrySet().stream().filter(entry -> entry.getValue().getLeft() >= sanity).forEach(entry -> {
				inst.addTemporaryTickable(entry.getKey(), entry.getValue().getRight());
				this.active.computeIfAbsent(entry.getValue().getLeft(), (a) -> new HashSet<>()).add(entry.getKey());
			});
		} else {
			if(prevSanity > sanity) {
				this.tickables.entrySet().stream().filter(entry -> entry.getValue().getLeft() >= sanity && entry.getValue().getLeft() < prevSanity).forEach(entry -> {
					inst.addTemporaryTickable(entry.getKey(), entry.getValue().getRight());
					this.active.computeIfAbsent(entry.getValue().getLeft(), (a) -> new HashSet<>()).add(entry.getKey());
				});
			} else {
				for(int i = prevSanity; i < sanity; i++) {
					this.active.getOrDefault(i, new HashSet<>()).forEach(inst::removeTemporaryTickable);
					this.active.remove(i);
				}
			}
		}
	}

	@Override
	public boolean restartOnReload() {
		return true;
	}
}
