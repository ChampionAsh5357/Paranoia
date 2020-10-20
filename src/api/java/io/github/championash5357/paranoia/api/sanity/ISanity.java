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

//TODO: Document, probably handle better too
public interface ISanity extends INBTSerializable<CompoundNBT> {
	
	int getSanity();
	int getMaxSanity();
	
	default void setSanity(int sanity) { this.setSanity(sanity, false); }
	default void changeSanity(int amount) { this.changeSanity(amount, false); }
	default void setMaxSanity(int maxSanity) { this.setMaxSanity(maxSanity, false); }
	default void changeMaxSanity(int amount) { this.changeMaxSanity(amount, false); }
	default void setMinSanity(int minSanity) { this.setMinSanity(minSanity, false); }
	default void changeMinSanity(int amount) { this.changeMinSanity(amount, false); }
	
	void setSanity(int sanity, boolean overrideChecks);
	void changeSanity(int amount, boolean overrideChecks);
	void setMaxSanity(int maxSanity, boolean overrideChecks);
	void changeMaxSanity(int amount, boolean overrideChecks);
	void setMinSanity(int minSanity, boolean overrideChecks);
	void changeMinSanity(int amount, boolean overrideChecks);
	
	void addTemporaryTickable(ResourceLocation location, ITickable tickable);
	boolean removeTemporaryTickable(ResourceLocation location);
	
	void executeLoginCallbacks(ServerPlayerEntity player);
	void tick();
}
