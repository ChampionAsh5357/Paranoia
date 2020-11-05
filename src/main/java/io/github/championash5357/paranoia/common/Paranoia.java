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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.sanity.PlayerSanity;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.api.util.Timer;
import io.github.championash5357.paranoia.client.ClientReference;
import io.github.championash5357.paranoia.common.init.*;
import io.github.championash5357.paranoia.common.network.NetworkHandler;
import io.github.championash5357.paranoia.common.util.CapabilityProviderSerializable;
import io.github.championash5357.paranoia.data.client.Localizations;
import io.github.championash5357.paranoia.server.ServerConfiguration;
import io.github.championash5357.paranoia.server.dedicated.DedicatedServerReference;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.PotionEvent.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(Paranoia.ID)
public class Paranoia {

	public static final String ID = "paranoia";

	public static final ISidedReference SIDED_SYSTEM = DistExecutor.safeRunForDist(() -> ClientReference::new, () -> DedicatedServerReference::new);
	private static final Logger LOGGER = LogManager.getLogger();
	private static Paranoia instance;
	private SimpleChannel network;

	public Paranoia() {
		instance = this;
		ModLoadingContext.get().registerConfig(Type.SERVER, ServerConfiguration.SERVER_SPEC);
		
		final IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
				forge = MinecraftForge.EVENT_BUS;

		mod.addListener(this::setup);
		mod.addListener(this::data);
		mod.addListener(this::configLoad);
		mod.addListener(this::configReload);
		SIDED_SYSTEM.setup(mod, forge);
		forge.addGenericListener(Entity.class, this::attachPlayerCaps);
		forge.addListener(this::registerCommands);
		forge.addListener(this::attachListeners);
		forge.addListener(this::playerLoggedIn);
		forge.addListener(this::tickPlayer);
		forge.addListener(this::clonePlayer);
		forge.addListener(EventPriority.LOWEST, this::damage);
		forge.addListener(this::slept);
		forge.addListener(this::wake);
		forge.addListener(this::itemUse);
		forge.addListener(this::potionApplied);
		forge.addListener(this::potionExpired);
		forge.addListener(EventPriority.LOWEST, this::potionRemoved);
		forge.addListener(this::equipment);
		forge.addListener(this::itemRightClick);
		forge.addListener(this::entitySpawn);
	}

	public static final Paranoia getInstance() {
		return instance;
	}

	public final SimpleChannel getNetwork() {
		return this.network;
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

	private void itemUse(final LivingEntityUseItemEvent.Finish event) {
		if(event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof PlayerEntity)
			event.getEntityLiving().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				Item item = event.getItem().getItem();
				sanity.changeSanity(SanityCallbacks.getSanityManager().getItemSanityEffect(item));
				sanity.changeMaxSanity(SanityCallbacks.getSanityManager().getItemMaxSanityEffect(item));
			});
	}
	
	private void itemRightClick(final PlayerInteractEvent.RightClickItem event) {
		PlayerEntity player = event.getPlayer();
		if(!event.isCanceled() && player.isServerWorld() && event.getItemStack().getItem() == Items.NETHER_STAR) {
			if(!player.abilities.isCreativeMode) event.getItemStack().shrink(1);
			player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				sanity.changeSanity(50);
				sanity.changeMaxSanity(10);
			});
		}
	}
	
	private void entitySpawn(final LivingSpawnEvent.CheckSpawn event) {
		LivingEntity entity = event.getEntityLiving();
		if(event.getResult() != Result.DENY && entity.isServerWorld() && event.getSpawnReason() == SpawnReason.CONVERSION) {
			entity.world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(entity.getPosition())).grow(10.0).expand(0.0D, (double)entity.world.getHeight(), 0.0D))
			.forEach(player -> {
				player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.changeSanity(10));
			});
		}
	}

	private void damage(final LivingDamageEvent event) {
		if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof PlayerEntity && event.getSource().getTrueSource() != null)
			event.getEntityLiving().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.changeSanity(SanityCallbacks.getSanityManager().getSanityLoss(event.getSource().getTrueSource().getType())));
	}

	private void playerLoggedIn(final PlayerLoggedInEvent event) {
		if(event.getPlayer().isServerWorld()) {
			event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.executeLoginCallbacks((ServerPlayerEntity) event.getPlayer()));
		}
	}

	private void clonePlayer(final PlayerEvent.Clone event) {
		event.getOriginal().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(original -> {
			event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(instance -> {
				instance.deserializeNBT(original.serializeNBT());
				if(event.isWasDeath()) {
					instance.changeMaxSanity(-10, true);
					instance.setSanity(instance.getMaxSanity(), true);
				}
			});
		});
	}

	private void slept(final SleepFinishedTimeEvent event) {
		if(!event.getWorld().isRemote()) {
			event.getWorld().getPlayers().forEach(player -> {
				player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
					sanity.changeMaxSanity(5);
					sanity.changeSanity(15);
				});
			});
		}
	}

	private void wake(final PlayerWakeUpEvent event) {
		if(event.getPlayer().isServerWorld()) {
			event.getPlayer().getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				if(!sanity.removeTemporaryTickable(new ResourceLocation(Paranoia.ID, "sleeping"))) LOGGER.error("Tickable {} does not exist!", "paranoia:sleeping");
			});
		}
	}

	private void tickPlayer(final PlayerTickEvent event) {
		if(event.side == LogicalSide.CLIENT || event.phase == Phase.START || !event.player.isAlive()) return;
		event.player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(ISanity::tick);
	}
	
	private void potionApplied(final PotionAddedEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(!SanityCallbacks.isHardMode() && entity instanceof PlayerEntity && entity.isServerWorld()
				&& event.getOldPotionEffect() == null && event.getPotionEffect().getPotion() == Effects.NIGHT_VISION)
			entity.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.addSanityLock());
	}
	
	private void potionExpired(final PotionExpiryEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity instanceof PlayerEntity && entity.isServerWorld()
				&& event.getPotionEffect() != null && event.getPotionEffect().getPotion() == Effects.NIGHT_VISION)
			entity.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.removeSanityLock());
	}
	
	private void potionRemoved(final PotionRemoveEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(!event.isCanceled() && entity instanceof PlayerEntity && entity.isServerWorld()
				&& event.getPotion() == Effects.NIGHT_VISION)
			entity.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> sanity.removeSanityLock());
	}
	
	private void equipment(final LivingEquipmentChangeEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity instanceof PlayerEntity && entity.isServerWorld() && event.getSlot() == EquipmentSlotType.OFFHAND) {
			entity.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				sanity.removeTemporaryTickable(new ResourceLocation(Paranoia.ID, "held_light_source"));
				Item item = event.getTo().getItem();
				int time = 0;
				if(item instanceof BlockItem) time = MathHelper.clamp(((BlockItem) item).getBlock().getDefaultState().getLightValue(), 0, 15);
				if(time != 0) {
					time = 4971 * (16 - time) - 2565;
				}
				
				int light_time = SanityCallbacks.getSanityManager().getItemLightSanity(item);
				time = light_time != 0 ? light_time : time;
				
				final int t = time;
				if(t != 0) sanity.addTemporaryTickable(new ResourceLocation(Paranoia.ID, "held_light_source"), new Timer(Math.abs(time), Math.abs(time) / 8, player -> sanity.changeSanity(t > 0 ? 1 : -1)));
			});
		}
	}

	private void attachListeners(final AddReloadListenerEvent event) {
		event.addListener(SanityCallbacks.getSanityManager());
	}

	private void configLoad(final ModConfig.Loading event) {
		if(event.getConfig().getType() == Type.SERVER) SanityCallbacks.toggleHardMode(ServerConfiguration.SERVER.hardMode.get());
	}
	
	private void configReload(final ModConfig.Reloading event) {
		if(event.getConfig().getType() == Type.SERVER) SanityCallbacks.toggleHardMode(ServerConfiguration.SERVER.hardMode.get());
	}
	
	private void addLanguageProviders(final DataGenerator gen) {
		for(String locale : new String[] {"en_us"}) gen.addProvider(new Localizations(gen, locale));
	}
}
