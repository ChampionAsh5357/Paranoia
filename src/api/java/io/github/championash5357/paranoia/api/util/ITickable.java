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

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * An interface that's called every tick. Grabs the player
 * and current sanity interface. Must be registered using
 * {@link ISanity#addTemporaryTickable(net.minecraft.util.ResourceLocation, ITickable)}.
 * Tickables can only be removed via {@link ISanity#removeTemporaryTickable(net.minecraft.util.ResourceLocation)}.
 */
@FunctionalInterface
public interface ITickable {

	/**
	 * A tick method.
	 * 
	 * @param player The server player
	 * @param sanity The sanity instance
	 */
	void tick(ServerPlayerEntity player, ISanity sanity);
}
