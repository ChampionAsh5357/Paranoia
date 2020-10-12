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

package io.github.championash5357.paranoia.common.init;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.sanity.PlayerSanity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityRegistrar {

	public static void register() {
		CapabilityManager.INSTANCE.register(ISanity.class, new IStorage<ISanity>() {

			@Override
			public INBT writeNBT(Capability<ISanity> capability, ISanity instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<ISanity> capability, ISanity instance, Direction side, INBT nbt) {
				if(!(nbt instanceof CompoundNBT)) throw new IllegalArgumentException("INBT must be an instance of CompoundNBT.");
				instance.deserializeNBT((CompoundNBT) nbt);
			}
			
		}, PlayerSanity::new);
	}

}
