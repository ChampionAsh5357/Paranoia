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

import java.util.UUID;

import io.github.championash5357.paranoia.api.callback.SanityCallback;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.callback.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;

public class CallbackRegistrar {

	public static void register() {
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "client_displays"), id -> new SanityCallback(id, 99, 100, new ClientCallback()));
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "attributes"), id -> new SanityCallback(id, 49, 50, new AttributeCallback()));
		SanityCallbacks.registerClientCallback(ShaderClient.SHADER, ShaderClient::new);
		SanityCallbacks.registerClientCallback(HeartOverlayClient.HEART_OVERLAY, HeartOverlayClient::new);
		SanityCallbacks.registerClientCallback(MissingHeartClient.MISSING_HEART, MissingHeartClient::new);
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
	}
}
