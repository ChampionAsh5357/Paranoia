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

import net.minecraft.util.ResourceLocation;

//TODO: Document
public class SanityCallback {

	private final ResourceLocation id;
	private final int startSanity, stopSanity;
	private final ICallbackHandler handler;
	
	public SanityCallback(ResourceLocation id, int startSanity, int stopSanity, ICallbackHandler handler) {
		this.id = id;
		this.startSanity = startSanity;
		this.stopSanity = stopSanity;
		this.handler = handler;
	}
	
	public int getStartSanity() {
		return startSanity;
	}
	
	public int getStopSanity() {
		return stopSanity;
	}
	
	public ICallbackHandler getHandler() {
		return handler;
	}
	
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
