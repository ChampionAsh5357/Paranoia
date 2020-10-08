package io.github.championash5357.paranoia.common.sanity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SanityManager extends JsonReloadListener {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final Map<Integer, Integer> sanityAttackMap = new HashMap<>();
	private final Map<Integer, Integer> maxSanityRecoverTimeMap = new HashMap<>();
	private final Map<Integer, List<Integer>> sanityLevelMap = new HashMap<>();
	
	public SanityManager() {
		super(GSON, "sanity");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager manager, IProfiler profiler) {
		this.sanityAttackMap.clear();
		this.maxSanityRecoverTimeMap.clear();
		this.sanityLevelMap.clear();
		map.forEach((id, element) -> {
			if(id.getPath().equals("sanity_attack")) parseSanityAttack(JSONUtils.getJsonObject(element, "sanity_attack"));
			else if(id.getPath().equals("sanity_levels")) parseSanityLevels(JSONUtils.getJsonObject(element, "sanity_levels"));
			else if(id.getPath().equals("max_sanity")) parseMaxSanityRecovery(JSONUtils.getJsonObject(element, "max_sanity"));
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
	
	public int getAttackTime(int sanity) {
		return this.sanityAttackMap.getOrDefault(sanity, -1);
	}
	
	public int getMaxSanityRecoveryTime(int lightLevel) {
		return this.maxSanityRecoverTimeMap.getOrDefault(lightLevel, -1);
	}
	
	public int getSanityLevelTime(int lightLevel, int hearts) {
		return this.sanityLevelMap.getOrDefault(lightLevel, new ArrayList<>()).get(hearts);
	}
}