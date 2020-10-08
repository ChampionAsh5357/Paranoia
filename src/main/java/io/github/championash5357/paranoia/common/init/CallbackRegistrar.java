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

package io.github.championash5357.paranoia.common.init;

import io.github.championash5357.paranoia.api.sanity.SanityCallback;
import io.github.championash5357.paranoia.api.sanity.SanityCallbacks;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.callback.ClientCallback;
import net.minecraft.util.ResourceLocation;

public class CallbackRegistrar {

	public static void register() {
		SanityCallbacks.registerCallback(new ResourceLocation(Paranoia.ID, "client_displays"), id -> new SanityCallback(id, 99, 100, new ClientCallback()));
	}
}
