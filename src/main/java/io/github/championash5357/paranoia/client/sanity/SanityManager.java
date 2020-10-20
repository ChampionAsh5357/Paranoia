package io.github.championash5357.paranoia.client.sanity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.*;

import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SanityManager extends JsonReloadListener {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final Set<ResourceLocation> sounds = new HashSet<>();
	private boolean invalidate = true;
	
	public SanityManager() {
		super(GSON, "sanity");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> map, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		this.invalidate = true;
		this.sounds.clear();
		map.forEach((id, element) -> {
			if(id.getPath().equals("behind_sounds")) this.parseBehindSounds(JSONUtils.getJsonObject(element, "behind_sounds"));
			else throw new JsonIOException("The following json file is incorrectly named or placed: " + id);
		});
	}
	
	private void parseBehindSounds(JsonObject obj) {
		if(JSONUtils.getBoolean(obj, "replace", false)) this.sounds.clear();
		JSONUtils.getJsonArray(obj, "entries").forEach(entry -> this.sounds.add(new ResourceLocation(entry.getAsString())));
	}
	
	public ResourceLocation getRandomSound() {
		if(this.invalidate) this.verifyInformation();
		return this.sounds.stream().skip(Helper.random().nextInt(this.sounds.size())).findFirst().get();
	}
	
	private void verifyInformation() {
		Collection<ResourceLocation> sounds = Minecraft.getInstance().getSoundHandler().getAvailableSounds();
		this.sounds.forEach(loc -> {
			if(!sounds.contains(loc)) throw new IllegalArgumentException("The following sound is not available: " + loc);
		});
		this.invalidate = false;
	}
}
