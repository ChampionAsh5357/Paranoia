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

package io.github.championash5357.paranoia.api.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.championash5357.paranoia.api.callback.SanityCallbacks;
import net.minecraft.util.ResourceLocation;

/**
 * A class used to register all client side implementations. Their
 * associated handler should all be registered within {@link SanityCallbacks}.
 */
public class ClientCallbackRegistry {

	private static final Map<String, IClientCallback> CLIENT_CALLBACKS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Attaches a new client callback to execute when the
	 * specified resource location is sent from the server.
	 * 
	 * @param location The id of the client callback.
	 * @param callback The client callback instance.
	 */
	public static synchronized void attachClientCallback(ResourceLocation location, IClientCallback callback) {
		CLIENT_CALLBACKS.put(location.toString(), callback);
	}
	
	/**
	 * Attaches a new client callback to execute when the
	 * specified resource location is sent from the server.
	 * Also constructs the stop instance internally. Should
	 * be used over {@link ClientCallbackRegistry#attachClientCallback(ResourceLocation, IClientCallback)}
	 * unless trying to add a special callback.
	 * 
	 * @param location The id of the client callback.
	 * @param callback The client callback instance.
	 * @param stopCallback The stop callback instance.
	 */
	public static synchronized void attachClientCallback(ResourceLocation location, IClientCallback callback, IClientCallback stopCallback) {
		CLIENT_CALLBACKS.put(location.toString(), callback);
		CLIENT_CALLBACKS.put(constructStop(location), stopCallback);
	}
	
	/**
	 * For internal use only. Handles when a client
	 * callback is synced from the network.
	 * 
	 * @param loc The id of the client callback.
	 * @param sanity The current sanity level.
	 */
	public static void handleCallback(String loc, int sanity) {
		CLIENT_CALLBACKS.getOrDefault(loc, (s) -> LOGGER.error("No callback has been added for {}.", loc)).handle(sanity);
	}
	
	private static String constructStop(ResourceLocation loc) {
		return loc.getNamespace() + ":stop_" + loc.getPath();
	}
}
