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

import net.minecraft.util.ResourceLocation;

/**
 * A singleton handler used to attach
 * callbacks to the sanity instance.
 * Constructed only when loaded.
 * Otherwise stored using their id.
 */
public class SanityCallback {

	private final ResourceLocation id;
	private final int startSanity, stopSanity;
	private final ICallback handler;
	
	/**
	 * A handler constructor.
	 * 
	 * @param id The current id of the callback.
	 * @param startSanity The sanity level to load the callback.
	 * @param stopSanity The sanity level to unload the callback.
	 * @param handler The associated callback to load/unload.
	 */
	public SanityCallback(ResourceLocation id, int startSanity, int stopSanity, ICallback handler) {
		this.id = id;
		this.startSanity = startSanity;
		this.stopSanity = stopSanity;
		this.handler = handler;
	}
	
	/**
	 * Gets the starting sanity level.
	 * 
	 * @return The sanity level to load the callback.
	 */
	public int getStartSanity() {
		return startSanity;
	}
	
	/**
	 * Gets the stopping sanity level.
	 * 
	 * @return The sanity level to unload the callback.
	 */
	public int getStopSanity() {
		return stopSanity;
	}
	
	/**
	 * Gets the associated callback handler to construct when loaded.
	 * 
	 * @return The associated callback.
	 */
	public ICallback getHandler() {
		return handler;
	}
	
	/**
	 * Gets the id of the callback.
	 * 
	 * @return The id of the callback.
	 */
	public ResourceLocation getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SanityCallback)) return false;
		return this.id.equals(((SanityCallback) o).getId());
	}
}
