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

import java.util.List;
import java.util.Map;

import io.github.championash5357.paranoia.api.callback.ICallback;
import io.github.championash5357.paranoia.api.callback.ITeleporterCallback;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class TeleportCallback implements ICallback {
	
	private final Map<Integer, List<ITeleporterCallback>> teleports;
	
	public TeleportCallback() {
		this.teleports = SanityCallbacks.getTeleporters();
	}
	
	@Override
	public void call(ServerPlayerEntity player, ISanity inst, int sanity, int prevSanity, Phase phase) {
		if(phase != Phase.STOP && prevSanity > sanity) {
			teleports.entrySet().stream().filter(entry -> entry.getKey() >= sanity && entry.getKey() < prevSanity)
			.flatMap(entry -> entry.getValue().stream()).forEach(teleporter -> teleporter.teleport(player));
		}
	}
}
