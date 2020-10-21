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

package io.github.championash5357.paranoia.client;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import io.github.championash5357.paranoia.api.client.ClientCallbackRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class ClientHandler {
	
	public static void handle(int sanity, List<String> calls) {
		calls.forEach(str -> ClientCallbackRegistry.handleCallback(str, sanity));
	}
	
	public static void handle(Vector3d pos) {
		Minecraft.getInstance().getSoundHandler().play(new SimpleSound(ClientReference.getInstance().getSanityManager().getRandomSound(), SoundCategory.HOSTILE, 1.0f, 1.0f, false, 0, AttenuationType.LINEAR, pos.x, pos.y, pos.z, false));
	}
	
	public static void handle(Map<BlockPos, Block> states) {
		states.forEach((pos, block) -> ClientReference.getInstance().addBlockRender(pos, block.getDefaultState()));
	}
	
	public static void handleEntities(Map<Triple<Float, Float, Vector3d>, EntityType<?>> states) {
		states.forEach((vec, type) -> ClientReference.getInstance().addEntityRender(vec, type));
	}
}
