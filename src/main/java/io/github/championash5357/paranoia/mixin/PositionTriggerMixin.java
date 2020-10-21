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

package io.github.championash5357.paranoia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.api.util.Timer;
import io.github.championash5357.paranoia.common.Paranoia;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

@Mixin(PositionTrigger.class)
public abstract class PositionTriggerMixin {

	private static final ResourceLocation SLEPT_IN_BED = new ResourceLocation("slept_in_bed");
	@Shadow public abstract ResourceLocation getId();
	
	@Inject(method = "trigger(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At("HEAD"))
	private void addTickable(ServerPlayerEntity player, CallbackInfo info) {
		if(this.getId().equals(SLEPT_IN_BED)) {
			player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				sanity.addTemporaryTickable(new ResourceLocation(Paranoia.ID, "sleeping"), new Timer(600, 100, (p) -> sanity.changeSanity(1)));
			});
		}
	}
}
