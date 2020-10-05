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

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.championash5357.paranoia.common.ISidedReference;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientReference implements ISidedReference {

	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		mod.addListener(this::client);
		forge.addListener(EventPriority.HIGHEST, this::overlayPre);
		forge.addListener(EventPriority.HIGHEST, this::overlayPost);
	}

	private void client(final FMLClientSetupEvent event) {
		//ClientRegistry.registerEntityShader(ClientPlayerEntity.class, new ResourceLocation(Paranoia.ID, "shaders/post/saturate_0.json"));
	}
	
	private void overlayPre(final RenderGameOverlayEvent.Pre event) {
		RenderSystem.enableTexture();
	}
	
	private void overlayPost(final RenderGameOverlayEvent.Post event) {
		RenderSystem.enableTexture();
	}
}
