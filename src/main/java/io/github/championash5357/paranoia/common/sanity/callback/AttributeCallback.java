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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import io.github.championash5357.paranoia.api.callback.ICallback;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;

public class AttributeCallback implements ICallback {

	private final Map<Attribute, AttributeInformation> attributeModifierMap = new HashMap<>();

	public AttributeCallback() {
		SanityCallbacks.constructAttributeCallbacks().forEach((attribute, pair) -> this.attributeModifierMap.put(attribute, new AttributeInformation(pair.getLeft(), pair.getRight())));
	}

	@Override
	public void call(ServerPlayerEntity player, int sanity, int prevSanity, Phase phase) {
		if(phase == Phase.STOP) removeAttributesModifiersFromEntity(player.getAttributeManager());
		else applyAttributesModifiersToEntity(player.getAttributeManager(), sanity);
	}
	
	@Override
	public boolean restartOnReload() {
		return true;
	}

	public void removeAttributesModifiersFromEntity(AttributeModifierManager attributeMap) {
		for(Entry<Attribute, AttributeInformation> entry : this.attributeModifierMap.entrySet()) {
			ModifiableAttributeInstance modifiableattributeinstance = attributeMap.createInstanceIfAbsent(entry.getKey());
			if (modifiableattributeinstance != null) {
				modifiableattributeinstance.removeModifier(entry.getValue().modifier);
			}
		}
	}

	public void applyAttributesModifiersToEntity(AttributeModifierManager attributeMap, int sanity) {
		for(Entry<Attribute, AttributeInformation> entry : this.attributeModifierMap.entrySet()) {
			ModifiableAttributeInstance modifiableattributeinstance = attributeMap.createInstanceIfAbsent(entry.getKey());
			if (modifiableattributeinstance != null && entry.getValue().reevaluate(sanity)) {
				AttributeModifier attributemodifier = entry.getValue().modifier;
				modifiableattributeinstance.removeModifier(attributemodifier);
				double amplifier = entry.getValue().applyLogic(sanity);
				if(amplifier != 0) modifiableattributeinstance.applyPersistentModifier(new AttributeModifier(attributemodifier.getID(), attributemodifier.getName(), amplifier, attributemodifier.getOperation()));
			}
		}
	}

	public static class AttributeInformation {
		private final AttributeModifier modifier;
		private final Function<Integer, Double> amplifier;
		private double prevAmplifier;

		private AttributeInformation(AttributeModifier modifier, Function<Integer, Double> amplifier) {
			this.modifier = modifier;
			this.amplifier = amplifier;
		}

		private double applyLogic(int sanity) {
			this.prevAmplifier = this.amplifier.apply(sanity);
			return this.prevAmplifier;
		}
		
		private boolean reevaluate(int sanity) {
			return this.prevAmplifier != this.amplifier.apply(sanity);
		}
	}
}
