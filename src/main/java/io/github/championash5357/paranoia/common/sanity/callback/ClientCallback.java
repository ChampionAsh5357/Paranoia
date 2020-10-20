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

package io.github.championash5357.paranoia.common.sanity.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.championash5357.paranoia.api.callback.ICallback;
import io.github.championash5357.paranoia.api.callback.IClientCallbackHandler;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks.CallbackType;
import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.network.server.SHandleClientCallback;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

//TODO: Expand on later
public class ClientCallback implements ICallback {
	
	private static final Logger LOGGER = LogManager.getLogger();
	@SuppressWarnings("rawtypes")
	private final Map<String, IClientCallbackHandler> handlers;
	
	public ClientCallback() {
		this.handlers = SanityCallbacks.constructClientCallbacks();
	}
	
	@Override
	public void call(ServerPlayerEntity player, ISanity inst, int sanity, int prevSanity, Phase phase) {
		List<String> calls = new ArrayList<>();
		List<CallbackType> singletons = new ArrayList<>();
		this.handlers.forEach((str, callback) -> {
			if(callback.test(player, sanity, prevSanity, phase)) {
				if(singletons.contains(callback.getType()));
				else {
					if(callback.getType().isSingle()) singletons.add(callback.getType());
					calls.add(callback.getId().toString());
				}
			}
		});
		if(!calls.isEmpty()) Paranoia.getInstance().getNetwork().send(PacketDistributor.PLAYER.with(() -> player), new SHandleClientCallback(sanity, calls));
	}

	@Override
	public boolean restartOnReload() {
		return true;
	}
	
	@Override
	public boolean hasData() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		nbt.keySet().forEach(str -> {
			if(this.handlers.containsKey(str)) this.handlers.get(str).deserializeNBT(nbt.get(str));
			else LOGGER.warn("Callback {} no longer exists. Will skip!", str);
		});
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		this.handlers.forEach((str, callback) -> nbt.put(str, callback.serializeNBT()));
		return nbt;
	}
}
