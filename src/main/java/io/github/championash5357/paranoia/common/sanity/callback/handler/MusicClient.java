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

package io.github.championash5357.paranoia.common.sanity.callback.handler;

import io.github.championash5357.paranoia.api.callback.HandlerClient;
import io.github.championash5357.paranoia.api.callback.ICallback.Phase;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks.CallbackType;
import io.github.championash5357.paranoia.common.Paranoia;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class MusicClient extends HandlerClient {

	protected static final byte ELEVEN = 0b10;
	public static final ResourceLocation MUSIC = new ResourceLocation(Paranoia.ID, "music");
	public static final ResourceLocation ELEVEN_MUSIC = new ResourceLocation(Paranoia.ID, "eleven_music");
	
	public MusicClient(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean test(ServerPlayerEntity player, int sanity, int prevSanity, Phase phase) {
		if(phase == Phase.STOP) {
			this.setStatus(STOP);
			return true;
		} else if(phase == Phase.START && (this.getStatus() == ELEVEN || this.getStatus() == NORMAL)) return true;
		else {
			if(sanity <= 20 && prevSanity > 20) {
				this.setStatus(ELEVEN);
				return true;
			} else if((this.getStatus() == ELEVEN && sanity > 20 && sanity < 40) || (sanity <= 40 && prevSanity > 40)) {
				this.setStatus(NORMAL);
				return true;
			} else if(sanity > 40 && this.getStatus() != STOP) {
				this.setStatus(STOP);
				return true;
			} else return false;
		}
	}
	
	@Override
	protected ResourceLocation getId(byte status) {
		if(status == ELEVEN) return ELEVEN_MUSIC;
		return super.getId(status);
	}

	@Override
	public CallbackType getType() {
		return CallbackType.OTHER;
	}
}
