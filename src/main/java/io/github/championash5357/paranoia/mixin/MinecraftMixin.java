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

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.championash5357.paranoia.client.ClientReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvents;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	private static final BackgroundMusicSelector THIRTEEN = new BackgroundMusicSelector(SoundEvents.MUSIC_DISC_13, 3600, 7200, true);
	private static final BackgroundMusicSelector ELEVEN = new BackgroundMusicSelector(SoundEvents.MUSIC_DISC_11, 1500, 3000, true);
	@Shadow private ClientPlayerEntity player;
	
	@Inject(method = "getBackgroundMusicSelector()Lnet/minecraft/client/audio/BackgroundMusicSelector;", at = @At("HEAD"), cancellable = true)
	private void selectMusic(CallbackInfoReturnable<BackgroundMusicSelector> ci) {
		if(ClientReference.getInstance().shouldPlayEleven()) ci.setReturnValue(ELEVEN);
		else if(ClientReference.getInstance().shouldPlayThirteen()) ci.setReturnValue(THIRTEEN);
	}
	
	@ModifyArg(method = "processKeyBinds()V",
			   at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V"))
	private Entity renderView(@Nullable Entity entity) {
		return entity != null ? entity : this.player;
	}
}
