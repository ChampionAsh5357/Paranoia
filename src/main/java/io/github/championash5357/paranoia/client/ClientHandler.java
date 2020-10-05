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

package io.github.championash5357.paranoia.client;

import java.util.Optional;

import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.common.network.server.SUpdateSanityMessage;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ClientHandler {

	public static void handle(SUpdateSanityMessage message, Optional<World> opt) {
		opt.ifPresent(world -> {
			Entity entity = world.getEntityByID(message.getEntityId());
			if(entity != null) entity.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(cap -> cap.deserializeNBT(message.getNbt()));
		});
	}
}
