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

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A callback that is handled whenever the sanity level changes.
 * All callbacks that want to be handled by the sanity level must
 * implement this or be attached to some other callback. All callbacks
 * with extra data are stored via {@link CompoundNBT}.
 */
@FunctionalInterface
public interface ICallback extends INBTSerializable<CompoundNBT> {

	/**
	 * Handles all logical a callback might have when the current sanity
	 * level changes.
	 * 
	 * @param player The server player.
	 * @param inst The sanity instance.
	 * @param sanity The current sanity level.
	 * @param prevSanity The previous sanity level.
	 * @param phase The current phase of the callback.
	 */
	void call(ServerPlayerEntity player, ISanity inst, int sanity, int prevSanity, Phase phase);
	
	/**
	 * Returns if the callback should be restarted
	 * on player login. Use to sync information
	 * to the client as the sanity handler
	 * holds no information on the client.
	 * 
	 * @return If the callback should be started again.
	 */
	default boolean restartOnReload() {
		return false;
	}
	
	/**
	 * Returns if the callback holds any data.
	 * Used to lessen the amount of information
	 * stored on the particular player and allow
	 * this interface to be functional. Will call
	 * {@link INBTSerializable#deserializeNBT(net.minecraft.nbt.INBT)}
	 * and {@link INBTSerializable#serializeNBT()} if true.
	 * 
	 * @return If the callback holds any data.
	 */
	default boolean hasData() {
		return false;
	}
	
	@Override
	default void deserializeNBT(CompoundNBT nbt) {}
	
	@Override
	default CompoundNBT serializeNBT() { return null; }
	
	/**
	 * The phases of the callback.
	 */
	public static enum Phase {
		/**
		 * Called when the callback is first loaded
		 * into the game or when restarted if {@link ICallback#restartOnReload()}
		 * returns true.
		 */
		START,
		/**
		 * Called when the sanity level changes.
		 */
		UPDATE,
		/**
		 * Called when the callback is about to be removed.
		 */
		STOP;
	}
}
