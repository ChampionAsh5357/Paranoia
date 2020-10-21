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

/**
 * A basic implementation of {@link IClientCallbackHandler}.
 * Sends the id on user implementation but handles the stored
 * information and the string to send to the client.
 * Uses bytes to leave a smaller footprint.
 */
public abstract class HandlerClient implements IClientCallbackHandler<ByteNBT> {

	/**
	 * The stop field. Should be set whenever
	 * the user wants to stop the current action.
	 */
	protected static final byte STOP = 0b0;
	/**
	 * The default field. Should be set whenever
	 * the user wants to start or continue the
	 * current action.
	 */
	protected static final byte NORMAL = 0b1;
	private final ResourceLocation id, stopId;
	private byte handler;
	
	/**
	 * A constructor for the client.
	 * 
	 * @param id The id of the callback. The stop id will be created internally from this.
	 */
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

	/**
	 * Sets the status of this handler.
	 * 
	 * @param status The new status.
	 */
	protected void setStatus(byte status) {
		this.handler = status;
	}
	
	/**
	 * Gets the status of this handler.
	 * 
	 * @return The current status.
	 */
	protected byte getStatus() {
		return handler;
	}
	
	/**
	 * Gets the current id to send to the client.
	 * Should be overridden when adding new statuses
	 * to send. Those statuses must have unique ids.
	 * 
	 * @param status The current status.
	 * @return The id to send to the client.
	 */
	protected ResourceLocation getId(byte status) {
		if(status == NORMAL) return this.id;
		else return this.stopId;
	}
	
	/**
	 * Do not override. Internal use only.
	 * 
	 * @deprecated Use {@link HandlerClient#getId(byte)}
	 */
	@Deprecated
	@Override
	public ResourceLocation getId() {
		return this.getId(this.handler);
	}
	
	private ResourceLocation constructStop(ResourceLocation loc) {
		return new ResourceLocation(loc.getNamespace(), "stop_" + loc.getPath());
	}
}
