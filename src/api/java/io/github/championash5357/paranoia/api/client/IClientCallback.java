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

import io.github.championash5357.paranoia.api.callback.SanityCallbacks;

/**
 * An interface used to handle when a client callback is sent
 * across the network. The associated client callback must be
 * registered using {@link SanityCallbacks#registerClientCallback(net.minecraft.util.ResourceLocation, java.util.function.Function)}.
 */
@FunctionalInterface
public interface IClientCallback {

	/**
	 * Used to handle client callbacks.
	 * 
	 * @param sanity The current sanity value.
	 */
	void handle(int sanity);
}
