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

package io.github.championash5357.paranoia.common.network.server;

import java.util.function.Supplier;

import io.github.championash5357.paranoia.client.ClientHandler;
import io.github.championash5357.paranoia.common.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SHandleClientCallback implements IMessage {

	private final int sanity;
	private final byte type;
	
	public SHandleClientCallback(byte type, int sanity) {
		this.sanity = sanity;
		this.type = type;
	}
	
	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeByte(this.type);
		buffer.writeInt(this.sanity);
	}

	public static SHandleClientCallback decode(PacketBuffer buffer) {
		return new SHandleClientCallback(buffer.readByte(), buffer.readInt());
	}
	
	@Override
	public boolean handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(this.type, this.sanity)));
		return true;
	}
	
	public static class Type {
		public static final byte STOP = 0b0;
		public static final byte DESATURATION_SHADER = 0b1;
		public static final byte RED_SHADER = 0b10;
	}
}
