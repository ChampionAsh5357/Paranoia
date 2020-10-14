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

package io.github.championash5357.paranoia.common.sanity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.*;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class SanityManager extends JsonReloadListener {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Integer, Integer> sanityAttackMap = new HashMap<>();
	private final Map<Integer, Integer> maxSanityRecoverTimeMap = new HashMap<>();
	private final Map<Integer, List<Integer>> sanityLevelMap = new HashMap<>();
	private final Map<EntityType<?>, Integer> entitySanityLoss = new HashMap<>();
	private final Map<RegistryKey<World>, Double> dimensionMultiplier = new HashMap<>();
	
	public SanityManager() {
		super(GSON, "sanity");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager manager, IProfiler profiler) {
		this.sanityAttackMap.clear();
		this.maxSanityRecoverTimeMap.clear();
		this.sanityLevelMap.clear();
		this.entitySanityLoss.clear();
		this.dimensionMultiplier.clear();
		map.forEach((id, element) -> {
			if(id.getPath().equals("sanity_attack")) parseSanityAttack(JSONUtils.getJsonObject(element, "sanity_attack"));
			else if(id.getPath().equals("sanity_levels")) parseSanityLevels(JSONUtils.getJsonObject(element, "sanity_levels"));
			else if(id.getPath().equals("max_sanity")) parseMaxSanityRecovery(JSONUtils.getJsonObject(element, "max_sanity"));
			else if(id.getPath().equals("entity_loss")) parseEntitySanityLoss(JSONUtils.getJsonObject(element, "entity_loss"));
			else if(id.getPath().equals("dimension_multiplier")) parseDimensionMultiplier(JSONUtils.getJsonObject(element, "dimension_multiplier"));
			else throw new JsonIOException("The following json file is incorrectly named or placed: " + id);
		});
	}
	
	//TODO: Handle as equation at some point
	private void parseSanityAttack(JsonObject obj) {
		obj.entrySet().forEach(entry -> this.sanityAttackMap.put(Integer.valueOf(entry.getKey()), entry.getValue().getAsInt()));
	}
	
	//TODO: Handle as equation at some point
	private void parseSanityLevels(JsonObject obj) {
		obj.entrySet().forEach(entry -> {
			List<Integer> breakdown = new ArrayList<>();
			JSONUtils.getJsonArray(entry.getValue(), "hearts_breakdown").forEach(element -> breakdown.add(element.getAsInt()));
			this.sanityLevelMap.put(Integer.valueOf(entry.getKey()), breakdown);
		});
	}
	
	//TODO: Handle as equation at some point
	private void parseMaxSanityRecovery(JsonObject obj) {
		obj.entrySet().forEach(entry -> this.maxSanityRecoverTimeMap.put(Integer.valueOf(entry.getKey()), entry.getValue().getAsInt()));
	}
	
	private void parseEntitySanityLoss(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.entitySanityLoss.clear();
		JSONUtils.getJsonObject(obj, "entries").entrySet().forEach(entry -> {
			EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entry.getKey()));
			if(type == null) LOGGER.warn("The entity {} is currently not present or doesn't exist. Skipping.", entry.getKey());
			else this.entitySanityLoss.put(type, -1 * entry.getValue().getAsInt());
		});
	}
	
	private void parseDimensionMultiplier(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.dimensionMultiplier.clear();
		JSONUtils.getJsonObject(obj, "entries").entrySet().forEach(entry -> {
			RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(entry.getKey()));
			if(key == null) LOGGER.warn("The current world key {} is currently not present or doesn't exist. Skipping.", entry.getKey());
			else this.dimensionMultiplier.put(key, MathHelper.clamp(entry.getValue().getAsDouble(), -1, 1)); //TODO: Handle correctly
		});
	}
	
	public int getAttackTime(int sanity) {
		return this.sanityAttackMap.getOrDefault(sanity, -1);
	}
	
	public int getMaxSanityRecoveryTime(RegistryKey<World> key, int lightLevel) {
		int time = this.maxSanityRecoverTimeMap.getOrDefault(lightLevel, -1);
		double multiplier = this.dimensionMultiplier.getOrDefault(key, 1.0);
		return (int) (time < 0 ? time * (1 - multiplier) : time * (multiplier + 1));
	}
	
	public int getSanityLevelTime(RegistryKey<World> key, int lightLevel, int hearts) {
		int time = this.sanityLevelMap.getOrDefault(lightLevel, new ArrayList<>()).get(hearts);
		double multiplier = this.dimensionMultiplier.getOrDefault(key, 1.0);
		return (int) (time < 0 ? time * (1 - multiplier) : time * (multiplier + 1));
	}
	
	public int getSanityLoss(EntityType<?> type) {
		return this.entitySanityLoss.getOrDefault(type, 0);
	}
}