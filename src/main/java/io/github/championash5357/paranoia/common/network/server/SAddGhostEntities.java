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

import org.apache.commons.lang3.tuple.Triple;

import io.github.championash5357.paranoia.client.ClientHandler;
import io.github.championash5357.paranoia.common.network.IMessage;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.registries.ForgeRegistries;

public class SAddGhostEntities implements IMessage {

	private Map<Triple<Float, Float, Vector3d>, EntityType<?>> ghostEntities;
	
	public SAddGhostEntities(Map<Triple<Float, Float, Vector3d>, EntityType<?>> ghostEntities) {
		this.ghostEntities = ghostEntities;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(this.ghostEntities.size());
		this.ghostEntities.forEach((triple, type) -> {
			buffer.writeFloat(triple.getLeft());
			buffer.writeFloat(triple.getMiddle());
			buffer.writeDouble(triple.getRight().x);
			buffer.writeDouble(triple.getRight().y);
			buffer.writeDouble(triple.getRight().z);
			buffer.writeString(type.getRegistryName().toString());
		});
	}

	public static SAddGhostEntities decode(PacketBuffer buffer) {
		int size = buffer.readInt();
		Map<Triple<Float, Float, Vector3d>, EntityType<?>> entities = new HashMap<>(size);
		for(int i = 0; i < size; i++) entities.put(Triple.of(buffer.readFloat(), buffer.readFloat(), new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())), ForgeRegistries.ENTITIES.getValue(new ResourceLocation(buffer.readString())));
		return new SAddGhostEntities(entities);
	}
	
	@Override
	public boolean handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handleEntities(this.ghostEntities)));
		return true;
	}
}

