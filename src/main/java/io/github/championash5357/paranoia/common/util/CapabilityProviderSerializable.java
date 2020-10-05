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

package io.github.championash5357.paranoia.common.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityProviderSerializable<HANDLER> extends CapabilityProviderSimple<HANDLER> implements INBTSerializable<INBT> {

	public CapabilityProviderSerializable(Capability<HANDLER> capability, @Nullable Direction direction) {
		super(capability, capability.getDefaultInstance(), direction);
	}
	
	public CapabilityProviderSerializable(Capability<HANDLER> capability, @Nullable HANDLER instance, @Nullable Direction direction) {
		super(capability, instance, direction);
	}

	@Override
	public INBT serializeNBT() {
		return this.getInstance() == null ? null : this.getCapability().writeNBT(this.getInstance(), this.getDirection());
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		if(this.getInstance() == null) return;
		this.getCapability().readNBT(this.getInstance(), this.getDirection(), nbt);
	}
}
