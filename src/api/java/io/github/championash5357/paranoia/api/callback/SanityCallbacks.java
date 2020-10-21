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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * A class used to register all callbacks and its
 * attachments within the player sanity. Any client
 * implementation should be registered within ClientCallbackRegistry.
 */
public class SanityCallbacks {

	private static final Map<ResourceLocation, Function<ResourceLocation, SanityCallback>> SANITY_CALLBACKS = new HashMap<>();
	private static final Map<ResourceLocation, Function<ResourceLocation, IClientCallbackHandler<?>>> CLIENT_CALLBACK_HANDLERS = new LinkedHashMap<>();
	private static final Map<Attribute, Pair<AttributeModifier, Function<Integer, Double>>> ATTRIBUTES = new HashMap<>();
	private static final Map<Integer, List<ITeleporterCallback>> TELEPORTS = new HashMap<>();
	private static final Map<ResourceLocation, Pair<Integer, ITickable>> TICKABLES = new HashMap<>();
	private static final Map<Predicate<ServerPlayerEntity>, BiFunction<ServerPlayerEntity, ISanity, Double>> MULTIPLIERS = new HashMap<>();
	private static final SanityManager MANAGER = new SanityManager();
	
	/**
	 * Returns the reload listener for the sanity manager.
	 * 
	 * @return The sanity manager.
	 */
	public static final SanityManager getSanityManager() {
		return MANAGER;
	}
	
	/**
	 * Registers a callback to be handled by the sanity capability.
	 * Safe to call during {@link FMLCommonSetupEvent}.
	 * 
	 * @param id The id of the callback.
	 * @param callbackSupplier A function that maps the id to the callback instance.
	 */
	public static synchronized void registerCallback(ResourceLocation id, Function<ResourceLocation, SanityCallback> callbackSupplier) {
		if(SANITY_CALLBACKS.get(id) != null) throw new IllegalArgumentException("The name " + id.toString() + " has been registered twice.");
		SANITY_CALLBACKS.putIfAbsent(id, callbackSupplier);
	}
	
	/**
	 * For internal use only. Loads a new
	 * instance of the callback once the starting
	 * sanity level is met.
	 * 
	 * @param location The id of the callback.
	 * @return The constructed callback.
	 */
	public static SanityCallback createCallback(ResourceLocation location) {
		return SANITY_CALLBACKS.get(location).apply(location);
	}
	
	/**
	 * For internal use only. Validates that
	 * all callbacks have been loaded onto the
	 * player and adds new callbacks that might
	 * not have been available on original startup.
	 * 
	 * @return A map of all possible callbacks.
	 */
	public static Map<ResourceLocation, Function<ResourceLocation, SanityCallback>> getValidationMap() {
		return new HashMap<>(SANITY_CALLBACKS);
	}
	
	/**
	 * Registers a new handler that will be synchronized to the client via
	 * an already implemented callback. This callback is only present from
	 * a sanity level of 99 and downwards. A corresponding client side
	 * implementation must be registered in ClientCallbackRegisrty for any client
	 * side logic to occur. Safe to call during {@link FMLCommonSetupEvent}.
	 * 
	 * @param id The id of the handler to send across the network.
	 * @param clientCallbackSupplier A function that should map the id to the handler instance.
	 */
	public static synchronized void registerClientCallback(ResourceLocation id, Function<ResourceLocation, IClientCallbackHandler<?>> clientCallbackSupplier) {
		CLIENT_CALLBACK_HANDLERS.put(id, clientCallbackSupplier);
	}
	
	/**
	 * For internal use only. Constructs all current client
	 * handlers and stores them within the currently loaded
	 * callback instance.
	 * 
	 * @return A map of all possible client handlers.
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, IClientCallbackHandler> constructClientCallbacks() {
		return Util.make(new LinkedHashMap<>(), map -> CLIENT_CALLBACK_HANDLERS.forEach((id, supplier) -> map.put(id.toString(), supplier.apply(id))));
	}
	
	/**
	 * Registers a new attribute modifier to apply
	 * to the user once a certain sanity level is
	 * reached. This callback is only present from
	 * sanity level 49 and downwards.
	 * Safe to call during {@link FMLCommonSetupEvent}.
	 * 
	 * @param attribute The attribute instance.
	 * @param modifier The attribute modifier.
	 * @param amplifier The amplifier to apply to the modifier operation given the sanity level.
	 */
	public static synchronized void registerAttributeCallback(Attribute attribute, AttributeModifier modifier, Function<Integer, Double> amplifier) {
		ATTRIBUTES.put(attribute, Pair.of(modifier, amplifier));
	}
	
	/**
	 * For internal use only. Constructs all attributes
	 * and stores them within the currently loaded
	 * callback instance.
	 * 
	 * @return A map of all possible attributes.
	 */
	public static Map<Attribute, Pair<AttributeModifier, Function<Integer, Double>>> constructAttributeCallbacks() {
		return ImmutableMap.copyOf(ATTRIBUTES);
	}
	
	/**
	 * Registers a teleporter instance and calls whenever
	 * the specified sanity level is met on its way downwards.
	 * This callback is only present from sanity level 30
	 * and downwards.
	 * 
	 * @param sanity The sanity level to execute the teleport.
	 * @param callback The teleporter handler.
	 */
	public static synchronized void registerTeleporterCallback(int sanity, ITeleporterCallback callback) {
		TELEPORTS.computeIfAbsent(sanity, a -> new ArrayList<>()).add(callback);
	}
	
	/**
	 * For internal use only. Constructs all teleporters
	 * and stores them within the currently loaded
	 * callback instance.
	 * 
	 * @return A map of all possible teleporters.
	 */
	public static Map<Integer, List<ITeleporterCallback>> getTeleporters() {
		return ImmutableMap.copyOf(TELEPORTS);
	}
	
	/**
	 * Registers a new tickable callback that will be added whenever
	 * the sanity level is reached and removed once the sanity level
	 * exceeds that value once again. This callback is only present
	 * from sanity level 99 and downwards.
	 * 
	 * @param id The id of the tickable.
	 * @param sanity The sanity level to initialize the tickable.
	 * @param tickable The tickable instance.
	 */
	public static synchronized void registerTickableCallback(ResourceLocation id, int sanity, ITickable tickable) {
		TICKABLES.put(id, Pair.of(sanity, tickable));
	}
	
	/**
	 * For internal use only. Constructs all tickables
	 * and stores them within the currently loaded
	 * callback instance.
	 * 
	 * @return A map of all possible tickables.
	 */
	public static Map<ResourceLocation, Pair<Integer, ITickable>> getTickables() {
		return ImmutableMap.copyOf(TICKABLES);
	}
	
	/**
	 * Registers a multiplier to apply to the recovery
	 * and sanity change thresholds based on a certain
	 * condition. The multiplier should be negative if
	 * the sanity should decrease faster and increase
	 * slower or vice versa if positive.
	 * 
	 * @param condition The condition of when the multiplier should be applied.
	 * @param multiplier The multiplier. Should be between -1 and 1.
	 */
	public static void registerMultiplier(Predicate<ServerPlayerEntity> condition, double multiplier) {
		registerMultiplier(condition, (player, sanity) -> multiplier);
	}
	
	/**
	 * Registers a multiplier to apply to the recovery
	 * and sanity change thresholds based on a certain
	 * condition. The multiplier should be negative if
	 * the sanity should decrease faster and increase
	 * slower or vice versa if positive.
	 * 
	 * @param condition The condition of when the multiplier should be applied.
	 * @param multiplier A function that uses the player and sanity information to calculate the multiplier. Should be between -1 and 1.
	 */
	public static synchronized void registerMultiplier(Predicate<ServerPlayerEntity> condition, BiFunction<ServerPlayerEntity, ISanity, Double> multiplier) {
		MULTIPLIERS.put(condition, multiplier);
	}
	
	/**
	 * For internal use only. Grabs all multipliers
	 * and applies them based on the current situation
	 * provided by the {@link SanityManager}.
	 * 
	 * @param player The server player.
	 * @param sanity The sanity instance.
	 * @return A function that grabs the multiplier if the current threshold is positive or negative.
	 */
	public static Function<Boolean, Double> handleMultipliers(ServerPlayerEntity player, ISanity sanity) {
		return negative -> MULTIPLIERS.entrySet().stream().filter(entry -> entry.getKey().test(player)).reduce(1.0, (partial, entry) -> partial * MathHelper.clamp((1 + (negative ? 1 : -1) * entry.getValue().apply(player, sanity)), 0.0, 2.0), (a, b) -> a * b);
	}
	
	/**
	 * The callback types that can be used by the {@link IClientCallbackHandler}.
	 * Callback types should only be added if only one instance should be sent to the client.
	 * Otherwise, all multi-applied client callback handlers should use {@link CallbackType#OTHER}.
	 */
	public static class CallbackType {
		
	    private static final Map<String, CallbackType> VALUES = new ConcurrentHashMap<>();
		
	    /**
	     * Used for applying shaders to the player.
	     */
	    public static final CallbackType SHADER = get(new ResourceLocation("paranoia", "shader"), true);
	    /**
	     * Used for any non-singleton handler.
	     */
	    public static final CallbackType OTHER = get(new ResourceLocation("paranoia", "other"), false);
	    
	    /**
	     * Grabs or creates a new {@link CallbackType} if it's not already present.
	     * 
	     * @param loc The id of the type.
	     * @param isSingle If the type is single. Can be null if already present.
	     * @return The new or existing type instance.
	     */
	    public static CallbackType get(ResourceLocation loc, @Nullable Boolean isSingle) {
	    	String name = loc.toString();
	    	return VALUES.computeIfAbsent(name, k -> {
	            return new CallbackType(name, isSingle);
	        });
	    }
	    
	    private final boolean single;
	    
		private CallbackType(String name, Boolean single) {
			if(single == null) throw new IllegalArgumentException("CallbackType has a null new constructor: " + name);
			this.single = single;
		}

		/**
		 * Grabs if the callback type should specify if the handler
		 * sends a single instance of this type to the client.
		 * 
		 * @return If the callback type is single.
		 */
		public boolean isSingle() {
			return single;
		}
	}
}
