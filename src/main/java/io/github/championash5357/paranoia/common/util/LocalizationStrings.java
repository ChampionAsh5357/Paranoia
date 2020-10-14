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

package io.github.championash5357.paranoia.common.util;

import io.github.championash5357.paranoia.common.Paranoia;

public class LocalizationStrings {

	private static final String COMMAND = "command";
	
	public static final String COMMAND_SANITY_SANITY = construct(COMMAND, "sanity.sanity");
	public static final String COMMAND_SANITY_MAX_SANITY = construct(COMMAND, "sanity.max_sanity");
	public static final String COMMAND_SANITY_PLAYER = construct(COMMAND, "sanity.player");
	
	private static final String construct(String type, String value) {
		return type + "." + Paranoia.ID + "." + value;
	}
}
