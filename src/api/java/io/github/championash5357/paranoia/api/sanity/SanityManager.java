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

package io.github.championash5357.paranoia.api.sanity;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.*;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Grabs the server side information to be used by
 * the sanity instance when applicable. Since these
 * are JSON files, they are not referenced by any
 * callbacks and instead handled externally or inside
 * {@link ISanity#tick()}.
 */
public class SanityManager extends JsonReloadListener {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<Integer, Integer> sanityAttackMap = new HashMap<>();
	private final Map<Integer, Integer> maxSanityRecoverTimeMap = new HashMap<>();
	private final Map<Integer, List<Integer>> sanityLevelMap = new HashMap<>();
	private final Map<EntityType<?>, Integer> entitySanityLoss = new HashMap<>();
	private final Map<Item, Integer> itemSanity = new HashMap<>();
	private final Map<Item, Pair<Float, Integer>> itemMaxSanity = new HashMap<>();
	private final Map<Item, Integer> lightSanity = new HashMap<>();
	
	public SanityManager() {
		super(GSON, "sanity");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager manager, IProfiler profiler) {
		this.sanityAttackMap.clear();
		this.maxSanityRecoverTimeMap.clear();
		this.sanityLevelMap.clear();
		this.entitySanityLoss.clear();
		this.itemSanity.clear();
		this.itemMaxSanity.clear();
		this.lightSanity.clear();
		map.forEach((id, element) -> {
			if(id.getPath().equals("sanity_attack")) this.parseSanityAttack(JSONUtils.getJsonObject(element, "sanity_attack"));
			else if(id.getPath().equals("sanity_levels")) this.parseSanityLevels(JSONUtils.getJsonObject(element, "sanity_levels"));
			else if(id.getPath().equals("max_sanity")) this.parseMaxSanityRecovery(JSONUtils.getJsonObject(element, "max_sanity"));
			else if(id.getPath().equals("entity_damage")) this.parseEntitySanityLoss(JSONUtils.getJsonObject(element, "entity_damage"));
			else if(id.getPath().equals("item_sanity")) this.parseItemSanity(JSONUtils.getJsonObject(element, "item_sanity"));
			else if(id.getPath().equals("item_max_sanity")) this.parseItemMaxSanity(JSONUtils.getJsonObject(element, "item_max_sanity"));
			else if(id.getPath().equals("light_sanity")) this.parseLightSanity(JSONUtils.getJsonObject(element, "light_sanity"));
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
		obj.entrySet().forEach(entry -> this.maxSanityRecoverTimeMap.put(Integer.valueOf(entry.getKey()), Math.abs(entry.getValue().getAsInt()))); //TODO: Handle error properly
	}
	
	private void parseEntitySanityLoss(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.entitySanityLoss.clear();
		JSONUtils.getJsonObject(obj, "entries").entrySet().forEach(entry -> {
			EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entry.getKey()));
			if(type == null) LOGGER.warn("The entity {} is currently not present or doesn't exist. Skipping.", entry.getKey());
			else this.entitySanityLoss.put(type, -1 * entry.getValue().getAsInt());
		});
	}
	
	private void parseItemSanity(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.itemSanity.clear();
		JSONUtils.getJsonObject(obj, "entries").entrySet().forEach(entry -> {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
			if(item == null) LOGGER.warn("The item {} is currently not present or doesn't exist. Skipping.", entry.getKey());
			else this.itemSanity.put(item, entry.getValue().getAsInt());
		});
	}
	
	private void parseItemMaxSanity(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.itemMaxSanity.clear();
		JSONUtils.getJsonArray(obj, "entries").forEach(entry -> {
			JsonObject obj_entry = JSONUtils.getJsonObject(entry, "entry");
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getString(obj_entry, "item")));
			if(item == null) LOGGER.warn("The item {} is currently not present or doesn't exist. Skipping.", JSONUtils.getString(obj_entry, "item"));
			else this.itemMaxSanity.put(item, Pair.of(JSONUtils.getFloat(obj_entry, "chance", 1.0f), JSONUtils.getInt(obj_entry, "sanity", 1)));
		});
	}
	
	private void parseLightSanity(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.lightSanity.clear();
		JSONUtils.getJsonObject(obj, "entries").entrySet().forEach(entry -> {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entry.getKey()));
			if(item == null) LOGGER.warn("The item {} is currently not present or doesn't exist. Skipping.", entry.getKey());
			else this.lightSanity.put(item, entry.getValue().getAsInt());
		});
	}
	
	/**
	 * Grabs the current attack threshold for
	 * when to attack the player. Returns -1
	 * if no attack time is registered for the
	 * current sanity level.
	 * 
	 * @param sanity The current sanity level.
	 * @return The attack threshold in ticks.
	 */
	public int getAttackTime(int sanity) {
		return this.sanityAttackMap.getOrDefault(sanity, -1);
	}
	
	/**
	 * Grabs the current maximum sanity recovery
	 * threshold for when to recover the player's max sanity.
	 * Returns -1 if no maximum recovery time is
	 * registered for the current light level.
	 * 
	 * @param lightLevel The current light level.
	 * @return The maximum recovery threshold in ticks.
	 */
	public int getMaxSanityRecoveryTime(int lightLevel) {
		return this.maxSanityRecoverTimeMap.getOrDefault(lightLevel, -1);
	}
	
	/**
	 * Grabs the current sanity change
	 * threshold for when to change the
	 * player's sanity level. If the value
	 * is negative, it will decrease the player
	 * sanity.
	 * 
	 * @param lightLevel The current light level.
	 * @param hearts The current player hearts.
	 * @return The sanity change threshold in ticks.
	 * 
	 * @throws IndexOutOfBoundsException If there is no registered hearts value.
	 */
	public int getSanityLevelTime(int lightLevel, int hearts) {
		return this.sanityLevelMap.getOrDefault(lightLevel, new ArrayList<>()).get(hearts);
	}
	
	/**
	 * Gets how much sanity should be lost
	 * when damage is taken from this entity.
	 * Returns 0 if the entity is not registered.
	 * 
	 * @param type The entity type from which the user took damage from.
	 * @return The amount of sanity to lose.
	 */
	public int getSanityLoss(EntityType<?> type) {
		return this.entitySanityLoss.getOrDefault(type, 0);
	}
	
	/**
	 * Gets how much sanity is gained whenever
	 * an item is finished being use (e.g. food eaten,
	 * potion drank). Returns 0 if the item is
	 * not registered.
	 * 
	 * @param item The item from which the user finished using.
	 * @return The amount of sanity to gain.
	 */
	public int getItemSanityEffect(Item item) {
		return this.itemSanity.getOrDefault(item, 0);
	}
	
	/**
	 * Gets how much max sanity is gained whenever
	 * an item is finished being use (e.g. food eaten,
	 * potion drank). Returns 0 if the item is
	 * not registered.
	 * 
	 * @param item The item from which the user finished using.
	 * @return The amount of max sanity to gain.
	 */
	public int getItemMaxSanityEffect(Item item) {
		Pair<Float, Integer> pair = this.itemMaxSanity.getOrDefault(item, Pair.of(1.0f, 0));
		return Math.random() < pair.getLeft() ? pair.getRight() : 0;
	}
	
	/**
	 * Gets how much time should pass before this
	 * held item should increment or decrement sanity.
	 * This has no effect on the actual sanity loss from
	 * environmental lighting.
	 * 
	 * @param item The held item.
	 * @return The amount of time before sanity is affected.
	 */
	public int getItemLightSanity(Item item) {
		return this.lightSanity.getOrDefault(item, 0);
	}
}