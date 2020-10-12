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

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.sanity.PlayerSanity;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.client.ClientReference;
import io.github.championash5357.paranoia.common.init.CallbackRegistrar;
import io.github.championash5357.paranoia.common.init.CapabilityRegistrar;
import io.github.championash5357.paranoia.common.init.CommandRegistrar;
import io.github.championash5357.paranoia.common.network.NetworkHandler;
import io.github.championash5357.paranoia.common.sanity.SanityManager;
import io.github.championash5357.paranoia.common.util.CapabilityProviderSerializable;
import io.github.championash5357.paranoia.data.client.Localizations;
import io.github.championash5357.paranoia.server.dedicated.DedicatedServerReference;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(Paranoia.ID)
public class Paranoia {

	public static final String ID = "paranoia";
	
	public static final ISidedReference SIDED_SYSTEM = DistExecutor.safeRunForDist(() -> ClientReference::new, () -> DedicatedServerReference::new);
	private static Paranoia instance;
	private SimpleChannel network;
	private final SanityManager sanityManager;
	
	public Paranoia() {
		instance = this;
		this.sanityManager = new SanityManager();
		
		final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
				forge = MinecraftForge.EVENT_BUS;
		
		mod.addListener(this::setup);
		mod.addListener(this::data);
		SIDED_SYSTEM.setup(mod, forge);
		forge.addGenericListener(Entity.class, this::attachPlayerCaps);
		forge.addListener(this::registerCommands);
		forge.addListener(this::attachListeners);
		forge.addListener(this::playerLoggedIn);
		forge.addListener(this::tickPlayer);
		forge.addListener(this::clonePlayer);
		forge.addListener(this::damage);
	}
	
	public static final Paranoia getInstance() {
		return instance;
	}
	
	public final SimpleChannel getNetwork() {
		return this.network;
	}
	
	public final SanityManager getSanityManager() {
		return this.sanityManager;
	}
	
	private void data(final GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if(event.includeClient()) {
			addLanguageProviders(gen);
		}
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		network = NetworkHandler.createNetwork();
		CapabilityRegistrar.register();
		CallbackRegistrar.register();
	}
	
	private void attachPlayerCaps(final AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof PlayerEntity)
			event.addCapability(new ResourceLocation(ID, "sanity"), new CapabilityProviderSerializable<>(CapabilityInstances.SANITY_CAPABILITY, new PlayerSanity((PlayerEntity) event.getObject()), null).attachListeners(event::addListener));
	}
	
	private void registerCommands(final RegisterCommandsEvent event) {
		CommandRegistrar.register(event.getDispatcher());
	}
	
	private void damage(final LivingDamageEvent event) {
		if(event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof PlayerEntity && event.getSource().getTrueSource() != null)
			event.getEntityLiving().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.changeSanity(this.getSanityManager().getSanityLoss(event.getSource().getTrueSource().getType())));
	}
	
	private void playerLoggedIn(final PlayerLoggedInEvent event) {
		if(event.getPlayer().isServerWorld()) {
			event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.executeLoginCallbacks((ServerPlayerEntity) event.getPlayer()));
		}
	}
	
	private void clonePlayer(final PlayerEvent.Clone event) {
		if(event.isWasDeath()) {
			event.getOriginal().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(original -> {
				event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(instance -> {
					instance.deserializeNBT(original.serializeNBT());
					instance.changeMaxSanity(-10);
					instance.setSanity(instance.getMaxSanity());
				});
			});
		} else {
			event.getOriginal().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(original -> {
				event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(instance -> {
					instance.deserializeNBT(original.serializeNBT());
				});
			});
		}
	}
	
	private void tickPlayer(final PlayerTickEvent event) {
		if(event.side == LogicalSide.CLIENT || event.phase == Phase.START || !event.player.isAlive()) return;
		if(((ServerPlayerEntity) event.player).interactionManager.survivalOrAdventure()) event.player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(ISanity::tick);
	}
	
	private void attachListeners(final AddReloadListenerEvent event) {
		event.addListener(this.sanityManager);
	}
	
	private void addLanguageProviders(final DataGenerator gen) {
		for(String locale : new String[] {"en_us"}) gen.addProvider(new Localizations(gen, locale));
	}
}
