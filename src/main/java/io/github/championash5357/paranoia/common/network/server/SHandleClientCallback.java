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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.github.championash5357.paranoia.client.ClientHandler;
import io.github.championash5357.paranoia.common.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SHandleClientCallback implements IMessage {

	private final int sanity;
	private final List<String> calls;
	
	public SHandleClientCallback(int sanity, List<String> calls) {
		this.sanity = sanity;
		this.calls = calls;
	}
	
	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(this.sanity);
		buffer.writeInt(this.calls.size());
		this.calls.forEach(buffer::writeString);
	}

	public static SHandleClientCallback decode(PacketBuffer buffer) {
		int sanity = buffer.readInt();
		int size = buffer.readInt();
		List<String> calls = new ArrayList<>(size);
		for(int i = 0; i < size; ++i) calls.add(buffer.readString());
		return new SHandleClientCallback(sanity, calls);
	}
	
	@Override
	public boolean handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(this.sanity, this.calls)));
		return true;
	}
}
