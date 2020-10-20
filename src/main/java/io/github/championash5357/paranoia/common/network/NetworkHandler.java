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

package io.github.championash5357.paranoia.common.network;

import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.network.server.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

	public static SimpleChannel createNetwork() {
		int id = 0;
		final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Paranoia.ID, "network"))
				.clientAcceptedVersions(version -> true)
				.serverAcceptedVersions(version -> true)
				.networkProtocolVersion(() -> Paranoia.ID + ":1")
				.simpleChannel();
		
		channel.messageBuilder(SHandleClientCallback.class, ++id, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(SHandleClientCallback::encode)
		.decoder(SHandleClientCallback::decode)
		.consumer(SHandleClientCallback::handle)
		.add();
		
		channel.messageBuilder(SMobSounds.class, ++id, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(SMobSounds::encode)
		.decoder(SMobSounds::decode)
		.consumer(SMobSounds::handle)
		.add();
		
		channel.messageBuilder(SAddGhostBlocks.class, ++id, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(SAddGhostBlocks::encode)
		.decoder(SAddGhostBlocks::decode)
		.consumer(SAddGhostBlocks::handle)
		.add();
		
		return channel;
	}
}
