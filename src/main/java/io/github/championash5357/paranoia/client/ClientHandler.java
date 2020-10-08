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

import java.util.HashMap;
import java.util.Map;

import io.github.championash5357.paranoia.common.Paranoia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientHandler {

	private static Minecraft mc;
	private static final ResourceLocation RED_SHADER = new ResourceLocation(Paranoia.ID, "shaders/post/red.json");
	private static final Map<Integer, ResourceLocation> DESATURATION_MAP = Util.make(new HashMap<>(100), map -> {
		for (int i = 0; i < 100; ++i) {
			if(i == 20) map.put(i, new ResourceLocation("shaders/post/desaturate.json"));
			else map.put(i, new ResourceLocation(Paranoia.ID, "shaders/post/saturate_" + i + ".json"));
		}
	});
	
	public static void setMinecraft(Minecraft minecraft) {
		mc = minecraft;
	}
	
	public static void handle(byte type, int sanity) {
		switch(type) {
		case 0:
			ClientRegistryHelper.removeEntityShader(ClientPlayerEntity.class);
			mc.setRenderViewEntity(mc.player);
			break;
		case 1:
			ClientRegistry.registerEntityShader(ClientPlayerEntity.class, DESATURATION_MAP.get(sanity));
			mc.setRenderViewEntity(mc.player);
			break;
		case 2:
			ClientRegistry.registerEntityShader(ClientPlayerEntity.class, RED_SHADER);
			mc.setRenderViewEntity(mc.player);
			break;
		default:
			break;
		}
	}
}
