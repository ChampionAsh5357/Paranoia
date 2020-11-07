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

import org.apache.commons.lang3.tuple.Triple;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.championash5357.paranoia.api.client.ClientCallbackRegistry;
import io.github.championash5357.paranoia.client.sanity.SanityManager;
import io.github.championash5357.paranoia.client.util.ValueTimer;
import io.github.championash5357.paranoia.common.ISidedReference;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.sanity.callback.handler.*;
import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.LightType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
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
	private boolean enableMissingHealth,
					enableSanityOverlay,
					isVeryFoggy;
	private byte track;
	private final Map<BlockPos, ValueTimer<BlockState>> ghostBlocks = new HashMap<>();
	private final Map<Vector3d, ValueTimer<Entity>> ghostEntities = new HashMap<>();
	
	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		instance = this;
		this.sanityManager = new SanityManager();
		mod.addListener(this::construct);
		mod.addListener(this::client);
		forge.addListener(this::playerLogin);
		forge.addListener(this::playerLogout);
		forge.addListener(EventPriority.HIGHEST, this::overlayPre);
		forge.addListener(EventPriority.HIGHEST, this::overlayPost);
		forge.addListener(this::fogDensity);
		forge.addListener(this::renderWorldLast);
		forge.addListener(this::clientTick);
		forge.addListener(EventPriority.LOWEST, this::fogColor);
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
	
	private void playerLogin(final LoggedInEvent event) {
		this.resetClientCache();
	}
	
	private void playerLogout(final LoggedOutEvent event) {
		this.resetClientCache();
	}
	
	private void resetClientCache() {
		this.enableMissingHealth = false;
		this.enableSanityOverlay = false;
		this.isVeryFoggy = false;
		this.track = 0;
		this.ghostBlocks.clear();
		this.ghostEntities.clear();
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
		ClientCallbackRegistry.attachClientCallback(MusicClient.MUSIC, sanity -> this.track = 0b1, sanity -> this.track = 0b0);
		ClientCallbackRegistry.attachClientCallback(MusicClient.ELEVEN_MUSIC, sanity -> this.track = 0b10);
	}
	
	private void fogColor(final FogColors event) {
		if(this.isVeryFoggy) {
			event.setRed(event.getRed() * 0.2f);
			event.setGreen(event.getGreen() * 0.2f);
			event.setBlue(event.getBlue() * 0.2f);
		}
	}
	
	private void fogDensity(final FogDensity event) {
		if(this.isVeryFoggy) {
			event.setCanceled(true);
			event.setDensity(0.5f);
		}
	}
	
	private void renderWorldLast(final RenderWorldLastEvent event) {
		MatrixStack stack = event.getMatrixStack();
		Vector3d projection = this.mc.gameRenderer.getActiveRenderInfo().getProjectedView();
		IRenderTypeBuffer.Impl buffer = this.mc.getRenderTypeBuffers().getBufferSource();
		this.ghostBlocks.forEach((pos, timer) -> {
			if(Vector3d.copyCentered(pos).isWithinDistanceOf(projection, 16.0d)) {
				stack.push();
				stack.translate(pos.getX() - projection.x, pos.getY() - projection.y, pos.getZ() - projection.z);
				this.mc.getBlockRendererDispatcher().renderModel(timer.getValue(), pos, this.mc.world, stack, buffer.getBuffer(getBlockRenderType(timer.getValue())), true, Helper.random(), EmptyModelData.INSTANCE);
				stack.pop();
			}
		});
		this.ghostEntities.forEach((vec, timer) -> {
			if(vec.isWithinDistanceOf(vec, 16.0d))
				this.mc.getRenderManager().renderEntityStatic(timer.getValue(), vec.x - projection.x, vec.y - projection.y - 0.5, vec.z - projection.z, 0.0f, 1.0f, stack, buffer, getPackedLight(new BlockPos(vec)));
		});
		buffer.finish();
	}
	
	private final int getPackedLight(BlockPos pos) {
		return LightTexture.packLight(this.mc.world.getLightFor(LightType.BLOCK, pos), this.mc.world.getLightFor(LightType.SKY, pos));
	}
	
	private void clientTick(final ClientTickEvent event) {
		if(event.phase == Phase.START) return;
		if(!this.ghostBlocks.isEmpty()) {
			Iterator<Entry<BlockPos, ValueTimer<BlockState>>> it = this.ghostBlocks.entrySet().iterator();
			while(it.hasNext()) {
				Entry<BlockPos, ValueTimer<BlockState>> entry = it.next();
				entry.getValue().tick();
				if(entry.getValue().remove) it.remove();
			}
		}
		if(!this.ghostEntities.isEmpty()) {
			Iterator<Entry<Vector3d, ValueTimer<Entity>>> it = this.ghostEntities.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Vector3d, ValueTimer<Entity>> entry = it.next();
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
	
	public boolean shouldPlayThirteen() {
		return this.track == 0b1;
	}
	
	public boolean shouldPlayEleven() {
		return this.track == 0b10;
	}
	
	public void addBlockRender(BlockPos pos, BlockState state) {
		this.ghostBlocks.put(pos, new ValueTimer<>(state, Helper.random().nextInt(6000) + 3000));
	}
	
	public void addEntityRender(Triple<Float, Float, Vector3d> triple, EntityType<?> type) {
		Entity entity = type.create(this.mc.world);
		entity.setRenderYawOffset(triple.getLeft());
		entity.setRotationYawHead(triple.getMiddle());
		this.ghostEntities.put(triple.getRight(), new ValueTimer<>(entity, Helper.random().nextInt(6000) + 3000));
	}
	
	private static RenderType getBlockRenderType(BlockState state) {
		if(RenderTypeLookup.canRenderInLayer(state, RenderType.getTripwire())) return RenderType.getTripwire();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getTranslucent())) return RenderType.getTranslucent();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getCutout())) return RenderType.getCutout();
		else if(RenderTypeLookup.canRenderInLayer(state, RenderType.getCutoutMipped())) return RenderType.getCutoutMipped();
		else return RenderType.getSolid();
	}
}
