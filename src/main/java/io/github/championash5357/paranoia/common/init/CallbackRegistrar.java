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

package io.github.championash5357.paranoia.common.init;

import java.util.HashMap;
import java.util.UUID;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Triple;

import io.github.championash5357.paranoia.api.callback.*;
import io.github.championash5357.paranoia.api.util.Timer;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.network.server.*;
import io.github.championash5357.paranoia.common.sanity.callback.*;
import io.github.championash5357.paranoia.common.sanity.callback.handler.*;
import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.PacketDistributor;

public class CallbackRegistrar {

	public static void register() {
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "client_displays"), id -> new SanityCallback(id, 99, 100, new ClientCallback()));
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "attributes"), id -> new SanityCallback(id, 49, 50, new AttributeCallback()));
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "teleporters"), id -> new SanityCallback(id, 30, 31, new TeleportCallback()));
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "tickables"), id -> new SanityCallback(id, 99, 100, new TickableCallback()));
		SanityCallbacks.registerClientCallback(ShaderClient.SHADER, ShaderClient::new);
		SanityCallbacks.registerClientCallback(HeartOverlayClient.HEART_OVERLAY, HeartOverlayClient::new);
		SanityCallbacks.registerClientCallback(MissingHeartClient.MISSING_HEART, MissingHeartClient::new);
		SanityCallbacks.registerClientCallback(FoggyClient.FOGGY, FoggyClient::new);
		SanityCallbacks.registerClientCallback(MusicClient.MUSIC, MusicClient::new);
		SanityCallbacks.registerAttributeCallback(Attributes.MOVEMENT_SPEED, new AttributeModifier(UUID.fromString("8462dd74-3bea-4710-ae76-1a91da304354"), "sanity.paranoia.generic.movement_speed", 0.0D, AttributeModifier.Operation.MULTIPLY_TOTAL), sanity -> {
			if(sanity <= 5) return 0.75;
			else if(sanity < 20) return -0.5;
			else if(sanity < 40) return -0.25;
			else return 0.0;
		});
		SanityCallbacks.registerAttributeCallback(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("598067c7-a25f-434f-99e5-aa2c76f473bb"), "sanity.paranoia.generic.attack_damage", 0.0D, AttributeModifier.Operation.ADDITION), sanity -> {
			if(sanity <= 5) return -10.0;
			else if(sanity <= 10) return 5.0;
			else return 0.0;
		});
		SanityCallbacks.registerAttributeCallback(Attributes.ATTACK_SPEED, new AttributeModifier(UUID.fromString("6456d0a4-bbe4-4953-a125-2328aa97a644"), "sanity.paranoia.generic.attack_speed", 0.0D, AttributeModifier.Operation.ADDITION), sanity -> {
			if(sanity <= 5) return 4.0;
			else return 0.0;
		});
		SanityCallbacks.registerAttributeCallback(Attributes.ARMOR, new AttributeModifier(UUID.fromString("2a491020-26f3-4579-8ac0-608c043b0c9b"), "sanity.paranoia.generic.armor", 0.0D, AttributeModifier.Operation.ADDITION), sanity -> {
			if(sanity <= 5) return -2.0;
			else if(sanity < 20) return 2.0;
			else if(sanity < 40) return 1.0;
			else return 0.0;
		});
		SanityCallbacks.registerAttributeCallback(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(UUID.fromString("1023d2f4-6a83-4a90-8817-ffe579976ca2"), "sanity.paranoia.reach_distance", 0.0D, AttributeModifier.Operation.ADDITION), sanity -> {
			if(sanity <= 5) return 2.0;
			else if(sanity < 20) return -2.0;
			else if(sanity < 40) return -1.0;
			else return 0.0;
		});
		SanityCallbacks.registerAttributeCallback(ForgeMod.NAMETAG_DISTANCE.get(), new AttributeModifier(UUID.fromString("e3649c10-9dcd-4de2-b5aa-f9c8c94a0116"), "sanity.paranoia.nametag_distance", 0.0D, AttributeModifier.Operation.MULTIPLY_TOTAL), sanity -> {
			if(sanity <= 5) return 0.0;
			else if(sanity < 20) return 0.5;
			else if(sanity < 40) return 0.75;
			else return 0.0;
		});
		SanityCallbacks.registerTeleporterCallback(30, (player) -> {
			if(Helper.random().nextInt(100) < 5) teleportPlayer(player);
		});
		SanityCallbacks.registerTeleporterCallback(20, (player) -> {
			if(Helper.random().nextInt(100) < 25) teleportPlayer(player);
		});
		SanityCallbacks.registerTeleporterCallback(10, (player) -> {
			if(Helper.random().nextInt(100) < 75) teleportPlayer(player);
		});
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "the_doors"), 75, new Timer(3000, 3000, (sanity) -> {
			if(sanity < 5) return 0.25;
			else if(sanity < 20) return 0.5;
			else if(sanity < 60) return 0.75;
			else return 1.0;
		}, (player) -> {
			BlockPos.getClosestMatchingPosition(player.getPosition(), 16, 16, (pos) -> {
				return player.world.getBlockState(pos).getBlock() instanceof DoorBlock;
			}).ifPresent(pos -> {
				BlockState state = player.world.getBlockState(pos);
				player.world.setBlockState(pos, state.with(DoorBlock.OPEN, !state.get(DoorBlock.OPEN)), 10);
			});
		}));
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "drop_item"), 20, new Timer(1000, 1000, (sanity) -> {
			if(sanity < 5) return 0.25;
			else if(sanity < 10) return 0.5;
			else if(sanity < 15) return 0.75;
			else return 1.0;
		}, (player) -> {
			if(Helper.random().nextInt(100) < 5) {
				ItemStack stack = player.getHeldItemMainhand().copy();
				ItemEntity entity = new ItemEntity(player.world, player.getPosX(), player.getPosY(), player.getPosZ(), stack);
				entity.setMotion(Helper.random().nextGaussian() * (double)0.05F, Helper.random().nextGaussian() * (double)0.05F + (double)0.2F, Helper.random().nextGaussian() * (double)0.05F);
				player.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				player.world.addEntity(entity);
			}
		}));
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "swap_slots"), 40, new Timer(2000, 2000, (sanity) -> {
			if(sanity < 5) return 0.05;
			else if(sanity < 10) return 0.2;
			else if(sanity < 15) return 0.45;
			else if(sanity < 20) return 0.7;
			else if(sanity < 25) return 0.95;
			else return 1.0;
		}, (player) -> {
			if(Helper.random().nextInt(100) < 50) {
				PlayerInventory inventory = player.inventory;
				IntStream.range(0, 9).filter(i -> !inventory.mainInventory.get(i).isEmpty()).findAny().ifPresent(org -> {
					int loc = Helper.random().nextInt(27) + 9;
					ItemStack copy = inventory.mainInventory.get(loc).copy();
					inventory.mainInventory.set(loc, inventory.mainInventory.get(org).copy());
					inventory.mainInventory.set(org, copy);
				});
			}
		}));
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "behind_sounds"), 60, new Timer(1200, 600, (sanity) -> {
			if(sanity < 20) return 0.25;
			else if(sanity < 30) return 0.5;
			else if(sanity < 40) return 0.75;
			else return 1.0;
		}, (player) -> {
			if(player.world.isNightTime() && Helper.random().nextInt(100) < 25) {
				Paranoia.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SMobSounds(player.getPositionVec().add(player.getLookVec().inverse().scale(Helper.random().nextInt(5)))));
			}
		}));
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "ghost_fires"), 20, new Timer(6000, 3000, (sanity) -> {
			if(sanity < 5) return 0.25;
			else if(sanity < 10) return 0.5;
			else if(sanity < 15) return 0.75;
			else return 1.0;
		}, (player) -> {
			if(Helper.random().nextInt(100) < 25) {
				BlockPos pos = player.getPosition().north(-2).east(-2);
				Paranoia.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SAddGhostBlocks(Util.make(new HashMap<>(),
						map -> IntStream.range(0, 25).filter(i -> i / 5 == 0 || i / 5 == 4 ? true : i % 5 == 0 || i % 5 == 4).forEach(i -> map.put(pos.north(i / 5).east(i % 5), Blocks.FIRE)))));
			}
		}));
		SanityCallbacks.registerTickableCallback(new ResourceLocation(Paranoia.ID, "ghost_creepers"), 10, new Timer(6000, 3000, (sanity) -> {
			if(sanity < 3) return 0.05;
			else if(sanity < 7) return 0.5;
			else return 1.0;
		}, (player) -> {
			if(Helper.random().nextInt(100) < 50) {
				BlockPos pos = player.getPosition();
				Paranoia.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SAddGhostEntities(Util.make(new HashMap<>(),
						map -> IntStream.range(0, 8).forEach(i -> map.put(Triple.of(i * 45.0f, i * 45.0f, Vector3d.copyCentered(pos.south(MathHelper.ceil(-2 * Math.cos(i * 45.0f * Math.PI / 180.0f))).east(MathHelper.ceil(2 * Math.sin(i * 45.0f * Math.PI / 180.0f))))), EntityType.CREEPER)))));
			}
		}));
		SanityCallbacks.registerMultiplier(player -> player.world.getDimensionKey() == World.THE_NETHER, -0.2);
		SanityCallbacks.registerMultiplier(player -> player.world.getDimensionKey() == World.THE_END, -0.5);
	}

	private static void teleportPlayer(ServerPlayerEntity player) {
		if(player.world == null) throw new IllegalStateException("The world is not registered!");
		int x = (int) (player.getPosX() + (Helper.random().nextDouble() - 0.5) * 64.0),
				z = (int) (player.getPosZ() + (Helper.random().nextDouble() - 0.5) * 64.0);
		int y = player.world.getHeight(Type.MOTION_BLOCKING, x, z);
		if(World.isValid(new BlockPos(x, y, z))) ITeleporterCallback.teleportTo(player, x, y, z);
	}
}
