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

import java.util.*;
import java.util.Map.Entry;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.championash5357.paranoia.api.client.ClientCallbackRegistry;
import io.github.championash5357.paranoia.client.sanity.SanityManager;
import io.github.championash5357.paranoia.client.util.StateTimer;
import io.github.championash5357.paranoia.common.ISidedReference;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.sanity.callback.handler.*;
import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

public class ClientReference implements ISidedReference {
	
	private static final ResourceLocation RED_SHADER = new ResourceLocation(Paranoia.ID, "shaders/post/red.json");
	private static final Map<Integer, ResourceLocation> DESATURATION_MAP = Util.make(new HashMap<>(100), map -> {
		for (int i = 0; i < 100; ++i) {
			if(i == 20) map.put(i, new ResourceLocation("shaders/post/desaturate.json"));
			else map.put(i, new ResourceLocation(Paranoia.ID, "shaders/post/saturate_" + i + ".json"));
		}
	});
	private static ClientReference instance;
	private Minecraft mc;
	private SanityManager sanityManager;
	private boolean enableMissingHealth;
	private boolean enableSanityOverlay;
	private boolean isVeryFoggy;
	private final Map<BlockPos, StateTimer> ghostBlocks = new HashMap<>();
	
	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		instance = this;
		this.sanityManager = new SanityManager();
		mod.addListener(this::construct);
		mod.addListener(this::client);
		forge.addListener(this::playerLogout);
		forge.addListener(EventPriority.HIGHEST, this::overlayPre);
		forge.addListener(EventPriority.HIGHEST, this::overlayPost);
		forge.addListener(this::fogDensity);
		forge.addListener(this::renderWorldLast);
		forge.addListener(this::clientTick);
	}

	public static final ClientReference getInstance() {
		return instance;
	}
	
	public final SanityManager getSanityManager() {
		return this.sanityManager;
	}
	
	private void construct(final FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
			manager.addReloadListener(this.sanityManager);
		});
	}
	
	private void playerLogout(final LoggedInEvent event) {
		this.enableMissingHealth = false;
		this.enableSanityOverlay = false;
		this.isVeryFoggy = false;
		this.ghostBlocks.clear();
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
		ClientCallbackRegistry.attachClientCallback(HeartOverlayClient.HEART_OVERLAY, sanity -> this.enableSanityOverlay = true, sanity -> this.enableSanityOverlay = false);
		ClientCallbackRegistry.attachClientCallback(MissingHeartClient.MISSING_HEART, sanity -> this.enableMissingHealth = true, sanity -> this.enableMissingHealth = false);
		ClientCallbackRegistry.attachClientCallback(FoggyClient.FOGGY, sanity -> this.isVeryFoggy = true, sanity -> this.isVeryFoggy = false);
	}
	
	private void fogDensity(final FogDensity event) {
		if(this.isVeryFoggy) {
			event.setCanceled(true);
			event.setDensity(0.5f);
		}
	}
	
	private void renderWorldLast(final RenderWorldLastEvent event) {
		MatrixStack stack = event.getMatrixStack();
		Vector3d vec = this.mc.gameRenderer.getActiveRenderInfo().getProjectedView();
		IRenderTypeBuffer.Impl buffer = this.mc.getRenderTypeBuffers().getBufferSource();
		this.ghostBlocks.forEach((pos, timer) -> {
			if(Vector3d.copyCentered(pos).isWithinDistanceOf(vec, 16.0d)) {
				stack.push();
				stack.translate(pos.getX() - vec.x, pos.getY() - vec.y, pos.getZ() - vec.z);
				this.mc.getBlockRendererDispatcher().renderModel(timer.getState(), pos, this.mc.world, stack, buffer.getBuffer(getBlockRenderType(timer.getState())), true, Helper.random(), EmptyModelData.INSTANCE);
				stack.pop();
			}
		});
		buffer.finish();
	}
	
	private void clientTick(final ClientTickEvent event) {
		if(event.phase == Phase.START) return;
		if(!this.ghostBlocks.isEmpty()) {
			Iterator<Entry<BlockPos, StateTimer>> it = this.ghostBlocks.entrySet().iterator();
			while(it.hasNext()) {
				Entry<BlockPos, StateTimer> entry = it.next();
				entry.getValue().tick();
				if(entry.getValue().remove) it.remove();
			}
		}
	}
	
	private void overlayPre(final RenderGameOverlayEvent.Pre event) {
		RenderSystem.enableTexture();
	}
	
	private void overlayPost(final RenderGameOverlayEvent.Post event) {
		RenderSystem.enableTexture();
	}
	
	public boolean isMissingHealth() {
		return this.enableMissingHealth;
	}
	
	public boolean hasSanityOverlay() {
		return this.enableSanityOverlay;
	}
	
	public void addBlockRender(BlockPos pos, BlockState state) {
		this.ghostBlocks.put(pos, new StateTimer(state, Helper.random().nextInt(6000) + 3000));
	}
	
	private static RenderType getBlockRenderType(BlockState state) {
		if(RenderTypeLookup.canRenderInLayer(state, RenderType.getTripwire())) return RenderType.getTripwire();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getTranslucent())) return RenderType.getTranslucent();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())) return RenderType.getCutout();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) return RenderType.getCutoutMipped();
		else return RenderType.getSolid();
	}
}
