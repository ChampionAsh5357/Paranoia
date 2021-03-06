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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import io.github.championash5357.paranoia.client.ClientHandler;
import io.github.championash5357.paranoia.common.network.IMessage;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class SAddGhostBlocks implements IMessage {

	private Map<BlockPos, Block> ghostBlocks;
	
	public SAddGhostBlocks(Map<BlockPos, Block> ghostBlocks) {
		this.ghostBlocks = ghostBlocks;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(this.ghostBlocks.size());
		this.ghostBlocks.forEach((pos, block) -> {
			buffer.writeBlockPos(pos);
			buffer.writeString(block.getRegistryName().toString());
		});
	}

	public static SAddGhostBlocks decode(PacketBuffer buffer) {
		int size = buffer.readInt();
		Map<BlockPos, Block> blocks = new HashMap<>(size);
		for(int i = 0; i < size; i++) blocks.put(buffer.readBlockPos(), ForgeRegistries.BLOCKS.getValue(new ResourceLocation(buffer.readString())));
		return new SAddGhostBlocks(blocks);
	}
	
	@Override
	public boolean handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(this.ghostBlocks)));
		return true;
	}
}
