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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.util.CapabilityInstances;
import io.github.championash5357.paranoia.common.util.LocalizationStrings;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandRegistrar {

	//TODO: Add more sanity options in commands
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> base = Commands.literal("paranoia");
		
		dispatcher.register(base.then(Commands.argument("type", StringArgumentType.word()).suggests((source, builder) -> ISuggestionProvider.suggest(new String[] {"sanity"}, builder)).requires(source -> source.hasPermissionLevel(2))
				.executes(source -> sendSanityInformation(source.getSource().asPlayer()))
				.then(Commands.argument("players", EntityArgument.players())
						.executes(source -> sendSanityInformation(source.getSource(), EntityArgument.getPlayers(source, "players")))
						.then(Commands.argument("action", StringArgumentType.word()).suggests((source, builder) -> ISuggestionProvider.suggest(new String[] {"set", "add", "set_max", "add_max"}, builder))
						.then(Commands.argument("amount", IntegerArgumentType.integer())
								.executes(source -> updateSanityInformation(EntityArgument.getPlayers(source, "players"), StringArgumentType.getString(source, "action"), IntegerArgumentType.getInteger(source, "amount"))))))));
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
	private static int sendSanityInformation(CommandSource receiver, Collection<ServerPlayerEntity> players) {
		IFormattableTextComponent playerText = new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_PLAYER),
				sanityText = new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_SANITY);
		int playerLength = playerText.getString().length(), sanityLength = sanityText.getString().length();
		receiver.sendFeedback(playerText
				.append(new StringTextComponent(StringUtils.repeat(' ', 17 - playerLength))).append(sanityText)
				.append(new StringTextComponent("  ")).append(new TranslationTextComponent(LocalizationStrings.COMMAND_SANITY_MAX_SANITY)), true);
		players.forEach(player -> {
			player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> {
				IFormattableTextComponent pText = (IFormattableTextComponent) player.getDisplayName();
				String sText = String.valueOf(sanity.getSanity());
				IFormattableTextComponent component = ((IFormattableTextComponent) player.getDisplayName())
						.append(new StringTextComponent(StringUtils.repeat(' ', 17 - pText.getString().length() + 1) + TextFormatting.GOLD + sText + StringUtils.repeat(' ', sanityLength - sText.length() + 2) + sanity.getMaxSanity()));
				System.out.println(component.getString());
				receiver.sendFeedback(component, true);
			});
		});
		return 1;
	}
	
	//TODO: Proper error handling
	private static int updateSanityInformation(Collection<ServerPlayerEntity> targets, String type, int amount) {
		final TriConsumer<ISanity, Integer, Boolean> task;
		switch(type) {
		case "set":
			task = ISanity::setSanity;
			break;
		case "add":
			task = ISanity::changeSanity;
			break;
		case "set_max":
			task = ISanity::setMaxSanity;
			break;
		case "add_max":
			task = ISanity::changeMaxSanity;
			break;
		default:
			task = (sanity, value, override) -> {};
			break;
		}
		targets.forEach(player -> {
			player.getCapability(CapabilityInstances.SANITY_CAPABILITY).ifPresent(sanity -> task.accept(sanity, amount, true));
		});
		return 1;
	}
}
