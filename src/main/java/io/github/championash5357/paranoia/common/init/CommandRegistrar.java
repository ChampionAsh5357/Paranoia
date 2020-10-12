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

import java.util.Collection;
import java.util.function.BiConsumer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.common.util.LocalizationStrings;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandRegistrar {

	//TODO: Add more sanity options in commands
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		
		dispatcher.register(Commands.literal("sanity").requires(source -> source.hasPermissionLevel(2))
				.executes(source -> sendSanityInformation(source.getSource().asPlayer()))
				.then(Commands.argument("players", EntityArgument.players())
						.executes(source -> sendSanityInformation(EntityArgument.getPlayer(source, "players")))
						.then(Commands.argument("action", StringArgumentType.word()).suggests((source, builder) -> ISuggestionProvider.suggest(new String[] {"set", "add"}, builder))
						.then(Commands.argument("amount", IntegerArgumentType.integer())
								.executes(source -> updateSanityInformation(EntityArgument.getPlayers(source, "players"), StringArgumentType.getString(source, "action"), IntegerArgumentType.getInteger(source, "amount")))))));
	}
	
	private static int sendSanityInformation(ServerPlayerEntity player) {
		player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
			player.sendMessage((new StringTextComponent("- ")).append(player.getDisplayName()), Util.DUMMY_UUID);
			player.sendMessage((new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_SANITY)).append(new StringTextComponent(": " + TextFormatting.GOLD + sanity.getSanity())), Util.DUMMY_UUID);
			player.sendMessage((new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_MAX_SANITY)).append(new StringTextComponent(": " + TextFormatting.GOLD + sanity.getMaxSanity())), Util.DUMMY_UUID);
		});
		return 1;
	}
	
	//TODO: Proper error handling
	private static int updateSanityInformation(Collection<ServerPlayerEntity> targets, String type, int amount) {
		final BiConsumer<ISanity, Integer> task;
		switch(type) {
		case "set":
			task = ISanity::setSanity;
			break;
		case "add":
			task = ISanity::changeSanity;
			break;
		default:
			task = (sanity, value) -> {};
			break;
		}
		targets.forEach(player -> {
			player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> task.accept(sanity, amount));
		});
		return 1;
	}
}
