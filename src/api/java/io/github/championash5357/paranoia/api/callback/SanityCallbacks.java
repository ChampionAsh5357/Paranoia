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

package io.github.championash5357.paranoia.api.callback;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.sanity.SanityManager;
import io.github.championash5357.paranoia.api.util.ITickable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

//TODO: Document
public class SanityCallbacks {

	private static final Map<ResourceLocation, Function<ResourceLocation, SanityCallback>> SANITY_CALLBACKS = new HashMap<>();
	private static final Map<ResourceLocation, Function<ResourceLocation, IClientCallbackHandler<?>>> CLIENT_CALLBACK_HANDLERS = new LinkedHashMap<>();
	private static final Map<Attribute, Pair<AttributeModifier, Function<Integer, Double>>> ATTRIBUTES = new HashMap<>();
	private static final Map<Integer, List<ITeleporterCallback>> TELEPORTS = new HashMap<>();
	private static final Map<ResourceLocation, Pair<Integer, ITickable>> TICKABLES = new HashMap<>();
	private static final Map<Predicate<ServerPlayerEntity>, BiFunction<ServerPlayerEntity, ISanity, Double>> MULTIPLIERS = new HashMap<>();
	private static final SanityManager MANAGER = new SanityManager();
	
	public static final SanityManager getSanityManager() {
		return MANAGER;
	}
	
	public static synchronized void registerCallback(ResourceLocation id, Function<ResourceLocation, SanityCallback> callbackSupplier) {
		if(SANITY_CALLBACKS.get(id) != null) throw new IllegalArgumentException("The name " + id.toString() + " has been registered twice.");
		SANITY_CALLBACKS.putIfAbsent(id, callbackSupplier);
	}
	
	public static SanityCallback createCallback(ResourceLocation location) {
		return SANITY_CALLBACKS.get(location).apply(location);
	}
	
	public static Map<ResourceLocation, Function<ResourceLocation, SanityCallback>> getValidationMap() {
		return new HashMap<>(SANITY_CALLBACKS);
	}
	
	public static synchronized void registerClientCallback(ResourceLocation id, Function<ResourceLocation, IClientCallbackHandler<?>> clientCallbackSupplier) {
		CLIENT_CALLBACK_HANDLERS.put(id, clientCallbackSupplier);
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String, IClientCallbackHandler> constructClientCallbacks() {
		return Util.make(new LinkedHashMap<>(), map -> CLIENT_CALLBACK_HANDLERS.forEach((id, supplier) -> map.put(id.toString(), supplier.apply(id))));
	}
	
	public static synchronized void registerAttributeCallback(Attribute attribute, AttributeModifier modifier, Function<Integer, Double> amplifier) {
		ATTRIBUTES.put(attribute, Pair.of(modifier, amplifier));
	}
	
	public static Map<Attribute, Pair<AttributeModifier, Function<Integer, Double>>> constructAttributeCallbacks() {
		return ImmutableMap.copyOf(ATTRIBUTES);
	}
	
	public static synchronized void registerTeleporterCallback(int sanity, ITeleporterCallback callback) {
		TELEPORTS.computeIfAbsent(sanity, a -> new ArrayList<>()).add(callback);
	}
	
	public static Map<Integer, List<ITeleporterCallback>> getTeleporters() {
		return ImmutableMap.copyOf(TELEPORTS);
	}
	
	public static synchronized void registerTickableCallback(ResourceLocation id, int sanity, ITickable tickable) {
		TICKABLES.put(id, Pair.of(sanity, tickable));
	}
	
	public static Map<ResourceLocation, Pair<Integer, ITickable>> getTickables() {
		return ImmutableMap.copyOf(TICKABLES);
	}
	
	public static void registerMultiplier(Predicate<ServerPlayerEntity> condition, double multiplier) {
		registerMultiplier(condition, (player, sanity) -> multiplier);
	}
	
	public static synchronized void registerMultiplier(Predicate<ServerPlayerEntity> condition, BiFunction<ServerPlayerEntity, ISanity, Double> multiplier) {
		MULTIPLIERS.put(condition, multiplier);
	}
	
	public static Function<Boolean, Double> handleMultipliers(ServerPlayerEntity player, ISanity sanity) {
		return negative -> MULTIPLIERS.entrySet().stream().filter(entry -> entry.getKey().test(player)).reduce(1.0, (partial, entry) -> partial * MathHelper.clamp((1 + (negative ? 1 : -1) * entry.getValue().apply(player, sanity)), 0.0, 2.0), (a, b) -> a * b);
	}
	
	public static class CallbackType {
		
	    private static final Map<String, CallbackType> VALUES = new ConcurrentHashMap<>();
		
	    public static final CallbackType SHADER = get(new ResourceLocation("paranoia", "shader"), true);
	    public static final CallbackType OTHER = get(new ResourceLocation("paranoia", "other"), false);
	    
	    public static CallbackType get(ResourceLocation loc, @Nullable Boolean isSingle) {
	    	String name = loc.toString();
	    	return VALUES.computeIfAbsent(name, k -> {
	            return new CallbackType(name, isSingle);
	        });
	    }
	    
	    private final String name;
	    private final boolean single;
	    
		private CallbackType(String name, Boolean single) {
			if(single == null) throw new IllegalArgumentException("CallbackType has a null new constructor: " + name);
			this.name = name;
			this.single = single;
		}
		
		public String getName() {
			return this.name;
		}
		
		public boolean isSingle() {
			return single;
		}
	}
}
