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

import net.minecraft.util.ResourceLocation;

//TODO: Document
public class ClientCallbackRegistry {

	private static final Map<String, IClientCallback> CLIENT_CALLBACKS = new HashMap<>();
	private static final Logger LOGGER = LogManager.getLogger();

	public static synchronized void attachClientCallback(ResourceLocation location, IClientCallback callback) {
		CLIENT_CALLBACKS.put(location.toString(), callback);
	}
	
	public static synchronized void attachClientCallback(ResourceLocation location, IClientCallback callback, IClientCallback stopCallback) {
		CLIENT_CALLBACKS.put(location.toString(), callback);
		CLIENT_CALLBACKS.put(constructStop(location), stopCallback);
	}
	
	public static void handleCallback(String loc, int sanity) {
		CLIENT_CALLBACKS.getOrDefault(loc, (s) -> LOGGER.error("No callback has been added for {}.", loc)).handle(sanity);
	}
	
	private static String constructStop(ResourceLocation loc) {
		return loc.getNamespace() + ":stop_" + loc.getPath();
	}
}
