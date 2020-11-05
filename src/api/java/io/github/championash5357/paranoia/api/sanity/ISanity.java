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

package io.github.championash5357.paranoia.api.sanity;

import io.github.championash5357.paranoia.api.util.ITickable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Sanity interface used for the capability. Handles all
 * logic related to the mod. 
 */
public interface ISanity extends INBTSerializable<CompoundNBT> {
	
	/**
	 * Gets the current sanity level.
	 * 
	 * @return The sanity level.
	 */
	int getSanity();
	/**
	 * Gets the max sanity the player can hold
	 * 
	 * @return The max sanity.
	 */
	int getMaxSanity();
	
	/**
	 * Sets the current sanity level. Is scaled
	 * between the absolute minimum (0 by default)
	 * and maximum sanity.
	 * 
	 * @param sanity The new sanity level.
	 */
	default void setSanity(int sanity) { this.setSanity(sanity, false); }
	/**
	 * Adds to the current sanity level. Is scaled
	 * between the absolute minimum (0 by default)
	 * and maximum sanity.
	 * 
	 * @param amount The amount to change the sanity level.
	 */
	default void changeSanity(int amount) { this.changeSanity(amount, false); }
	/**
	 * Sets the maximum sanity level. Is scaled
	 * between the minimum and absolute maximum
	 * sanity (100 by default). Sanity is scaled
	 * once set.
	 * 
	 * @param maxSanity The new max sanity level.
	 */
	default void setMaxSanity(int maxSanity) { this.setMaxSanity(maxSanity, false); }
	/**
	 * Adds to the maximum sanity level. Is scaled
	 * between the minimum and absolute maximum
	 * sanity (100 by default). Sanity is scaled
	 * once set.
	 * 
	 * @param amount The amount to change the max sanity level.
	 */
	default void changeMaxSanity(int amount) { this.changeMaxSanity(amount, false); }
	/**
	 * Sets the minimum sanity level. Is scaled
	 * between the absolute minimum (0 by default) and maximum
	 * sanity. Sanity is scaled once set.
	 * 
	 * @param minSanity The new minimum sanity level.
	 */
	default void setMinSanity(int minSanity) { this.setMinSanity(minSanity, false); }
	/**
	 * Adds to the minimum sanity level. Is scaled
	 * between the absolute minimum (0 by default) and maximum
	 * sanity. Sanity is scaled once set.
	 * 
	 * @param amount The amount to change the minimum sanity level.
	 */
	default void changeMinSanity(int amount) { this.changeMinSanity(amount, false); }
	
	/**
	 * Sets the current sanity level. Is scaled
	 * between the absolute minimum (0 by default)
	 * and maximum sanity.
	 * 
	 * @param sanity The new sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void setSanity(int sanity, boolean overrideChecks);
	/**
	 * Adds to the current sanity level. Is scaled
	 * between the absolute minimum (0 by default)
	 * and maximum sanity.
	 * 
	 * @param amount The amount to change the sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void changeSanity(int amount, boolean overrideChecks);
	/**
	 * Sets the maximum sanity level. Is scaled
	 * between the minimum and absolute maximum
	 * sanity (100 by default). Sanity is scaled
	 * once set.
	 * 
	 * @param maxSanity The new max sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void setMaxSanity(int maxSanity, boolean overrideChecks);
	/**
	 * Adds to the maximum sanity level. Is scaled
	 * between the minimum and absolute maximum
	 * sanity (100 by default). Sanity is scaled
	 * once set.
	 * 
	 * @param amount The amount to change the max sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void changeMaxSanity(int amount, boolean overrideChecks);
	/**
	 * Sets the minimum sanity level. Is scaled
	 * between the absolute minimum (0 by default) and maximum
	 * sanity. Sanity is scaled once set.
	 * 
	 * @param minSanity The new minimum sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void setMinSanity(int minSanity, boolean overrideChecks);
	/**
	 * Adds to the minimum sanity level. Is scaled
	 * between the absolute minimum (0 by default) and maximum
	 * sanity. Sanity is scaled once set.
	 * 
	 * @param amount The amount to change the minimum sanity level.
	 * @param overrideChecks If any checks should be overridden. This includes if the player is alive or in a different gamemode.
	 */
	void changeMinSanity(int amount, boolean overrideChecks);
	
	/**
	 * Adds a temporary tickable that will be called
	 * every tick. Does not stop ticking until removed.
	 * 
	 * @param location The id of the tickable.
	 * @param tickable The tickable instance.
	 */
	void addTemporaryTickable(ResourceLocation location, ITickable tickable);
	/**
	 * Removes a temporary tickable. If not present,
	 * logs the inconsistency in data.
	 * 
	 * @param location The id of the tickable.
	 * @return If the tickable was removed.
	 */
	boolean removeTemporaryTickable(ResourceLocation location);
	
	/**
	 * Defers deserialized callbacks that restart on reload or
	 * new callbacks until the player logs in.
	 * 
	 * @param player The server player.
	 */
	void executeLoginCallbacks(ServerPlayerEntity player);
	/**
	 * A tick method.
	 */
	void tick();
	/**
	 * Adds a sanity lock to determine whether
	 * a sanity value can be ticked.
	 * 
	 * @param lock The sanity lock.
	 */
	void addSanityLock();
	/**
	 * Removes a sanity lock to determine whether
	 * a sanity value can be ticked.
	 * 
	 * @param lock The sanity lock.
	 */
	void removeSanityLock();
}
