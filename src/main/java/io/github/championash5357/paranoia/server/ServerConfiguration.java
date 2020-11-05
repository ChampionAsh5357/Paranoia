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

package io.github.championash5357.paranoia.server;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ServerConfiguration {

	public final BooleanValue hardMode;

	public ServerConfiguration(final ForgeConfigSpec.Builder builder) {
		this.hardMode = builder.comment("Sets a specific world to hard mode.",
				"This will decrease your maximum sanity to original capacity in 1.0.0 instead of the updated value.",
				"This must be set before the world is loaded.",
				"Any previous version will be updated to handle this.")
				.worldRestart()
				.define("hardMode", false);
	}
	
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final ServerConfiguration SERVER;
	
	static {
		final Pair<ServerConfiguration, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);
		SERVER_SPEC = specPair.getRight();
		SERVER = specPair.getLeft();
	}
}
