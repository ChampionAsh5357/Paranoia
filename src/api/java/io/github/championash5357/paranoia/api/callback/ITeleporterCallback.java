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

/**
 * An interface used to force the player to teleport
 * once the player reaches a certain sanity level.
 * Any other checks can be handled within here as well.
 */
@FunctionalInterface
public interface ITeleporterCallback {

	/**
	 * Called when the sanity level is reached
	 * to teleport the player. Any other checks
	 * should be handled in here as well.
	 * 
	 * @param player The server player.
	 */
	void teleport(ServerPlayerEntity player);
	
	/**
	 * A helper used to teleport the player
	 * within the specified coordinates. This
	 * will teleport within the same world as
	 * the player.
	 * 
	 * @param player The server player.
	 * @param x The x position to teleport to.
	 * @param y The y position to teleport to.
	 * @param z The z position to teleport to.
	 */
	static void teleportTo(ServerPlayerEntity player, double x, double y, double z) {
		teleportTo(player, x, y, z, player.rotationYaw, player.rotationPitch);
	}
	
	/**
	 * A helper used to teleport the player
	 * within the specified coordinates.
	 * 
	 * @param player The server player.
	 * @param world The world to teleport to.
	 * @param x The x position to teleport to.
	 * @param y The y position to teleport to.
	 * @param z The z position to teleport to.
	 */
	static void teleportTo(ServerPlayerEntity player, ServerWorld world, double x, double y, double z) {
		teleportTo(player, world, x, y, z, player.rotationYaw, player.rotationPitch);
	}
	
	/**
	 * A helper used to teleport the player
	 * within the specified coordinates. This
	 * will teleport within the same world as
	 * the player.
	 * 
	 * @param player The server player.
	 * @param x The x position to teleport to.
	 * @param y The y position to teleport to.
	 * @param z The z position to teleport to.
	 * @param yaw The yaw of the player to set.
	 * @param pitch The pitch of the player to set.
	 */
	static void teleportTo(ServerPlayerEntity player, double x, double y, double z, float yaw, float pitch) {
		teleportTo(player, player.getServerWorld(), x, y, z, yaw, pitch);
	}
	
	/**
	 * A helper used to teleport the player
	 * within the specified coordinates.
	 * 
	 * @param player The server player.
	 * @param world The world to teleport to.
	 * @param x The x position to teleport to.
	 * @param y The y position to teleport to.
	 * @param z The z position to teleport to.
	 * @param yaw The yaw of the player to set.
	 * @param pitch The pitch of the player to set.
	 */
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
