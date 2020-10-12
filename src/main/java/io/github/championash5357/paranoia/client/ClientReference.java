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

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.championash5357.paranoia.api.client.ClientCallbackRegistry;
import io.github.championash5357.paranoia.common.ISidedReference;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.callback.HeartOverlayClient;
import io.github.championash5357.paranoia.common.callback.MissingHeartClient;
import io.github.championash5357.paranoia.common.callback.ShaderClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientReference implements ISidedReference {

	private Minecraft mc;
	private static final ResourceLocation RED_SHADER = new ResourceLocation(Paranoia.ID, "shaders/post/red.json");
	private static final Map<Integer, ResourceLocation> DESATURATION_MAP = Util.make(new HashMap<>(100), map -> {
		for (int i = 0; i < 100; ++i) {
			if(i == 20) map.put(i, new ResourceLocation("shaders/post/desaturate.json"));
			else map.put(i, new ResourceLocation(Paranoia.ID, "shaders/post/saturate_" + i + ".json"));
		}
	});
	private static boolean enableMissingHealth;
	private static boolean enableSanityOverlay;
	
	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		mod.addListener(this::client);
		forge.addListener(EventPriority.HIGHEST, this::overlayPre);
		forge.addListener(EventPriority.HIGHEST, this::overlayPost);
	}

	private void client(final FMLClientSetupEvent event) {
		this.mc = event.getMinecraftSupplier().get();
		ClientCallbackRegistry.attachClientCallback(ShaderClient.SHADER, sanity -> {
			ClientRegistry.registerEntityShader(ClientPlayerEntity.class, DESATURATION_MAP.get(sanity));
			this.mc.setRenderViewEntity(this.mc.player);
		}, sanity -> {
			ClientRegistryHelper.removeEntityShader(ClientPlayerEntity.class);
			this.mc.setRenderViewEntity(this.mc.player);
		});
		ClientCallbackRegistry.attachClientCallback(ShaderClient.RED_SHADER, sanity -> {
			ClientRegistry.registerEntityShader(ClientPlayerEntity.class, RED_SHADER);
			this.mc.setRenderViewEntity(this.mc.player);
		});
		ClientCallbackRegistry.attachClientCallback(HeartOverlayClient.HEART_OVERLAY, sanity -> enableSanityOverlay = true, sanity -> enableSanityOverlay = false);
		ClientCallbackRegistry.attachClientCallback(MissingHeartClient.MISSING_HEART, sanity -> enableMissingHealth = true, sanity -> enableMissingHealth = false);
	}
	
	private void overlayPre(final RenderGameOverlayEvent.Pre event) {
		RenderSystem.enableTexture();
	}
	
	private void overlayPost(final RenderGameOverlayEvent.Post event) {
		RenderSystem.enableTexture();
	}
	
	public static boolean isMissingHealth() {
		return enableMissingHealth;
	}
	
	public static boolean hasSanityOverlay() {
		return enableSanityOverlay;
	}
}
