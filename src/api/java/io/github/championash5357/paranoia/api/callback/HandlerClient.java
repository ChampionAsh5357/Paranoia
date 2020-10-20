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

import net.minecraft.nbt.ByteNBT;
import net.minecraft.util.ResourceLocation;

//TODO: Document
public abstract class HandlerClient implements IClientCallbackHandler<ByteNBT> {

	protected static final byte STOP = 0b0;
	protected static final byte NORMAL = 0b1;
	private final ResourceLocation id, stopId;
	private byte handler;
	
	public HandlerClient(ResourceLocation id) {
		this.id = id;
		this.stopId = this.constructStop(this.id);
	}

	@Override
	public ByteNBT serializeNBT() {
		return ByteNBT.valueOf(this.handler);
	}

	@Override
	public void deserializeNBT(ByteNBT nbt) {
		this.handler = nbt.getByte();
	}

	protected void setStatus(byte status) {
		this.handler = status;
	}
	
	protected byte getStatus() {
		return handler;
	}
	
	protected ResourceLocation getId(byte status) {
		if(status == NORMAL) return this.id;
		else return this.stopId;
	}
	
	@Override
	public ResourceLocation getId() {
		return this.getId(this.handler);
	}
	
	private ResourceLocation constructStop(ResourceLocation loc) {
		return new ResourceLocation(loc.getNamespace(), "stop_" + loc.getPath());
	}
}
