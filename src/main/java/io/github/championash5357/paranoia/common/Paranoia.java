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

package io.github.championash5357.paranoia.common;

import io.github.championash5357.paranoia.api.sanity.PlayerSanity;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.client.ClientReference;
import io.github.championash5357.paranoia.common.init.CapabilityRegistrar;
import io.github.championash5357.paranoia.common.network.NetworkHandler;
import io.github.championash5357.paranoia.common.util.CapabilityProviderSerializable;
import io.github.championash5357.paranoia.server.dedicated.DedicatedServerReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(Paranoia.ID)
public class Paranoia {

	public static final String ID = "paranoia";
	
	public static final ISidedReference SIDED_SYSTEM = DistExecutor.safeRunForDist(() -> ClientReference::new, () -> DedicatedServerReference::new);
	private static SimpleChannel network;
	
	public Paranoia() {
		final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
				forge = MinecraftForge.EVENT_BUS;
		
		mod.addListener(this::setup);
		SIDED_SYSTEM.setup(mod, forge);
		forge.addGenericListener(Entity.class, this::attachPlayerCaps);
	}
	
	public static final SimpleChannel getNetwork() {
		return network;
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		network = NetworkHandler.createNetwork();
		CapabilityRegistrar.register();
	}
	
	private void attachPlayerCaps(final AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(new ResourceLocation(ID, "sanity"), new CapabilityProviderSerializable<>(CapabilityInstances.SANITY_CAPABILITY, new PlayerSanity(), null).attachListeners(event::addListener));
	}
}
