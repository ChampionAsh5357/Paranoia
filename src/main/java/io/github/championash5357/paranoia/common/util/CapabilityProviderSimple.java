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

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityProviderSimple<HANDLER> implements ICapabilityProvider {

	protected final Capability<HANDLER> capability;
	protected final LazyOptional<HANDLER> capabilityHolder;
	
	@Nullable
	protected final Direction direction;
	@Nullable
	protected final HANDLER instance;
	
	public CapabilityProviderSimple(final Capability<HANDLER> capability, @Nullable final HANDLER instance, @Nullable final Direction direction) {
		this.capability = capability;
		this.direction = direction;
		this.instance = instance;
		this.capabilityHolder = this.instance != null ? LazyOptional.of(() -> this.instance) : LazyOptional.empty();
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return this.getCapability().orEmpty(cap, this.capabilityHolder);
	}
	
	public Capability<HANDLER> getCapability() {
		return capability;
	}

	@Nullable
	public Direction getDirection() {
		return direction;
	}
	
	@Nullable
	public HANDLER getInstance() {
		return instance;
	}
	
	public CapabilityProviderSimple<HANDLER> attachListeners(Consumer<Runnable> listeners) {
		listeners.accept(capabilityHolder::invalidate);
		return this;
	}
}
