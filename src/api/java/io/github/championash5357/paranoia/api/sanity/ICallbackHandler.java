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

package io.github.championash5357.paranoia.api.sanity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

//TODO: Document
public interface ICallbackHandler extends INBTSerializable<CompoundNBT> {

	boolean start(PlayerEntity player);
	boolean update(PlayerEntity player);
	boolean stop(PlayerEntity player);
	
	default boolean restartOnReload() {
		return false;
	}
	
	default boolean hasData() {
		return false;
	}
	
	@Override
	default void deserializeNBT(CompoundNBT nbt) {}
	
	@Override
	default CompoundNBT serializeNBT() { return null; }
}
