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

import com.mojang.brigadier.CommandDispatcher;

import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.common.util.LocalizationStrings;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandRegistrar {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("sanity").requires(source -> source.hasPermissionLevel(2))
				.executes(source -> sendSanityInformation(source.getSource().asPlayer()))
				.then(Commands.argument("player", EntityArgument.player())
						.executes(source -> sendSanityInformation(EntityArgument.getPlayer(source, "player")))));
	}
	
	private static int sendSanityInformation(ServerPlayerEntity player) {
		player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
			player.sendMessage((new StringTextComponent("- ")).append(player.getDisplayName()), Util.DUMMY_UUID);
			player.sendMessage((new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_SANITY)).append(new StringTextComponent(": " + TextFormatting.GOLD + sanity.getSanity())), Util.DUMMY_UUID);
			player.sendMessage((new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_MAX_SANITY)).append(new StringTextComponent(": " + TextFormatting.GOLD + sanity.getMaxSanity())), Util.DUMMY_UUID);
		});
		return 1;
	}
}
