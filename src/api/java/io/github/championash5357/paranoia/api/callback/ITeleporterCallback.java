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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

//TODO: Document
@FunctionalInterface
public interface ITeleporterCallback {

	void teleport(ServerPlayerEntity player);
	
	static void teleportTo(ServerPlayerEntity player, double x, double y, double z) {
		teleportTo(player, x, y, z, player.rotationYaw, player.rotationPitch);
	}
	
	static void teleportTo(ServerPlayerEntity player, ServerWorld world, double x, double y, double z) {
		teleportTo(player, world, x, y, z, player.rotationYaw, player.rotationPitch);
	}
	
	static void teleportTo(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch) {
		teleportTo(player, player.getServerWorld(), x, y, z, yaw, pitch);
	}
	
	static void teleportTo(ServerPlayerEntity player, ServerWorld world, double x, double y, double z, float yaw, float pitch) {
		BlockPos pos = new BlockPos(x, y, z);
		if(!World.isInvalidPosition(pos)) throw new IllegalStateException("One of the registered teleporters tried to teleport the player to an invalid position!");
		world.getChunkProvider().registerTicket(TicketType.POST_TELEPORT, new ChunkPos(pos), 1, player.getEntityId());
		player.stopRiding();
		if(player.isSleeping()) player.stopSleepInBed(true, true);
		if(world == player.world) player.connection.setPlayerLocation(x, y, z, yaw, pitch);
		else player.teleport(world, x, y, z, yaw, pitch);
	}
}
