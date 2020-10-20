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
import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class ShaderClient extends HandlerClient {

	protected static final byte RED = 0b10;
	public static final ResourceLocation SHADER = new ResourceLocation(Paranoia.ID, "shader");
	public static final ResourceLocation RED_SHADER = new ResourceLocation(Paranoia.ID, "red_shader");
	
	public ShaderClient(ResourceLocation id) {
		super(id);
	}
	
	@Override
	public boolean test(ServerPlayerEntity player, int sanity, int prevSanity, Phase phase) {
		if(phase == Phase.STOP) {
			this.setStatus(STOP);
			return true;
		} else if(phase == Phase.START && this.getStatus() == RED) return true;
		else {
			if(sanity <= 20 && prevSanity > 20 && this.getStatus() != RED && Helper.random().nextInt(100) < 5) {
				this.setStatus(RED);
				return true;
			} else if(sanity > 20) this.setStatus(NORMAL);
			if(this.getStatus() != RED) return true;
			else return false;
		}
	}

	@Override
	protected ResourceLocation getId(byte status) {
		if(status == RED) return RED_SHADER;
		return super.getId(status);
	}

	@Override
	public CallbackType getType() {
		return CallbackType.SHADER;
	}
}
