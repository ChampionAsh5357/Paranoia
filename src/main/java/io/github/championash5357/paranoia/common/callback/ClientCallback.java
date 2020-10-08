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

package io.github.championash5357.paranoia.common.callback;

import java.util.Random;

import io.github.championash5357.paranoia.api.sanity.ICallbackHandler;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.network.server.SHandleClientCallback;
import io.github.championash5357.paranoia.common.network.server.SHandleClientCallback.Type;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class ClientCallback implements ICallbackHandler {

	private static final Random RANDOM = new Random();
	private boolean isRed;
	private int prevSanity;
	
	@Override
	public void start(ServerPlayerEntity player, int sanity) {
		this.handle(player, sanity);
	}

	@Override
	public void update(ServerPlayerEntity player, int sanity) {
		this.handle(player, sanity);
	}

	@Override
	public void stop(ServerPlayerEntity player, int sanity) {
		Paranoia.getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SHandleClientCallback(Type.STOP, sanity));
	}
	
	private void handle(ServerPlayerEntity player, int sanity) {
		if (sanity == 20 && this.prevSanity > sanity && !this.isRed && RANDOM.nextInt(100) < 5) {
			this.isRed = true;
			Paranoia.getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SHandleClientCallback(Type.RED_SHADER, sanity));
		} else if (sanity > 20 && this.isRed) {
			this.isRed = false;
		}
		
		if(!this.isRed) {
			Paranoia.getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SHandleClientCallback(Type.DESATURATION_SHADER, sanity));
		}
		this.prevSanity = sanity;
	}

	@Override
	public boolean restartOnReload() {
		return true;
	}
	
	@Override
	public boolean hasData() {
		return true;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.isRed = nbt.getBoolean("isRed");
		this.prevSanity = nbt.getInt("prevSanity");
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("isRed", this.isRed);
		nbt.putInt("prevSanity", this.prevSanity);
		return nbt;
	}
}
