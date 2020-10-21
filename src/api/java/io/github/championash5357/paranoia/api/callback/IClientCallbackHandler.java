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

package io.github.championash5357.paranoia.api.callback;

import io.github.championash5357.paranoia.api.callback.ICallback.Phase;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks.CallbackType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * An interface used to handle when should something be sent
 * to the client. Only the id set in {@link IClientCallbackHandler#getId()}
 * is sent along with the current sanity level. Anything else
 * should be handled on the logical server here or in a different
 * callback.
 * 
 * @param <T> A generic that extends {@link INBT}
 */
public interface IClientCallbackHandler<T extends INBT> extends INBTSerializable<T> {
	
	/**
	 * If this interface should send the id to the client. Should be
	 * logically handled to include any bounds checking on sanity and
	 * only return true when needed to save on resources.
	 * 
	 * @param player The server player.
	 * @param sanity The current sanity level.
	 * @param prevSanity The previous sanity level.
	 * @param phase The current phase of the callback.
	 * @return If the id should be sent to the client.
	 */
	boolean test(ServerPlayerEntity player, int sanity, int prevSanity, Phase phase);
	
	/**
	 * The id to send to the client.
	 * 
	 * @return The current id.
	 */
	ResourceLocation getId();
	
	/**
	 * The callback type. Used to
	 * check if the callback should
	 * be singly handled or multiply
	 * handled. For example, shaders
	 * can only be applied one at a
	 * time so they are singly handled.
	 * 
	 * @return The callback type.
	 */
	CallbackType getType();
}
