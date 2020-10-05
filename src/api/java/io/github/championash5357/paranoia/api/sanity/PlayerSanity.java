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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

//TODO: Document
public class PlayerSanity implements ISanity {

	private int minSanity, maxSanity; //Should be effectively final
	private int sanity, tempMinSanity, tempMaxSanity;
	//TODO: Implement callback system
	private final Map<Integer, Set<ResourceLocation>> unloadedCallbacks = new HashMap<>();
	private final Map<Integer, Set<SanityCallback>> loadedCallbacks = new HashMap<>();

	
	public PlayerSanity() {
		this(0, 100);
	}
	
	public PlayerSanity(int minSanity, int maxSanity) {
		this(minSanity, maxSanity, maxSanity);
	}
	
	public PlayerSanity(int minSanity, int maxSanity, int sanity) {
		this(minSanity, maxSanity, sanity, minSanity, maxSanity);
	}
	
	public PlayerSanity(int minSanity, int maxSanity, int sanity, int tempMinSanity, int tempMaxSanity) {
		this.minSanity = minSanity;
		this.maxSanity = maxSanity;
		this.tempMinSanity = tempMinSanity;
		this.tempMaxSanity = tempMaxSanity;
		this.sanity = sanity;
	}

	@Override
	public int getSanity() {
		return this.sanity;
	}

	@Override
	public void setSanity(int sanity) {
		this.sanity = MathHelper.clamp(sanity, this.tempMinSanity, this.tempMaxSanity);
	}

	@Override
	public void changeSanity(int amount) {
		this.sanity = MathHelper.clamp(this.sanity + amount, this.tempMinSanity, this.tempMaxSanity);
	}

	@Override
	public void setMaxSanity(int maxSanity) {
		this.tempMaxSanity = MathHelper.clamp(maxSanity, this.tempMinSanity, this.maxSanity);
	}
	
	@Override
	public void changeMaxSanity(int amount) {
		this.tempMaxSanity = MathHelper.clamp(this.tempMaxSanity + amount, this.tempMinSanity, this.maxSanity);
	}

	@Override
	public void setMinSanity(int minSanity) {
		this.tempMinSanity = MathHelper.clamp(minSanity, this.minSanity, this.tempMaxSanity);
	}

	@Override
	public void changeMinSanity(int amount) {
		this.tempMinSanity = MathHelper.clamp(this.minSanity + amount, this.minSanity, this.tempMaxSanity);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("minSanity", this.minSanity);
		nbt.putInt("maxSanity", this.maxSanity);
		nbt.putInt("sanity", this.sanity);
		nbt.putInt("tempMinSanity", this.tempMinSanity);
		nbt.putInt("tempMaxSanity", this.tempMaxSanity);
		CompoundNBT unloadedCallbacks = new CompoundNBT();
		this.unloadedCallbacks.forEach((startSanity, list) -> {
			ListNBT locations = new ListNBT();
			list.forEach(location -> locations.add(StringNBT.valueOf(location.toString())));
			unloadedCallbacks.put(String.valueOf(startSanity), locations);
		});
		nbt.put("unloadedCallbacks", unloadedCallbacks);
		CompoundNBT loadedCallbacks = new CompoundNBT();
		this.loadedCallbacks.forEach((stopSanity, list) -> {
			ListNBT locations = new ListNBT();
			list.forEach(callback -> {
				if(callback.getHandler().hasData()) {
					CompoundNBT callbackData = new CompoundNBT();
					callbackData.putString("id", callback.getId().toString());
					callbackData.put("data", callback.getHandler().serializeNBT());
					locations.add(callbackData);
				} else {
					locations.add(StringNBT.valueOf(callback.getId().toString()));
				}
			});
			loadedCallbacks.put(String.valueOf(stopSanity), locations);
		});
		nbt.put("loadedCallbacks", loadedCallbacks);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.minSanity = nbt.getInt("minSanity");
		this.maxSanity = nbt.getInt("maxSanity");
		this.sanity = nbt.getInt("sanity");
		this.tempMinSanity = nbt.getInt("tempMinSanity");
		this.tempMaxSanity = nbt.getInt("tempMaxSanity");
		this.unloadedCallbacks.clear();
		this.loadedCallbacks.clear();
		CompoundNBT unloadedCallbacks = nbt.getCompound("unloadedCallbacks");
		unloadedCallbacks.keySet().forEach(startSanity -> {
			Set<ResourceLocation> locations = new HashSet<>();
			ListNBT list = unloadedCallbacks.getList(startSanity, 9);
			list.forEach(inbt -> {
				if(inbt instanceof StringNBT) {
					locations.add(new ResourceLocation(((StringNBT) inbt).getString()));
				} else {
					//TODO: Handle error in formatting
				}
			});
			this.unloadedCallbacks.put(Integer.valueOf(startSanity), locations);
		});
		CompoundNBT loadedCallbacks = nbt.getCompound("loadedCallbacks");
		loadedCallbacks.keySet().forEach(startSanity -> {
			Set<SanityCallback> callbacks = new HashSet<>();
			ListNBT list = unloadedCallbacks.getList(startSanity, 9);
			list.forEach(inbt -> {
				if(inbt instanceof StringNBT) {
					callbacks.add(SanityCallbacks.createCallback(new ResourceLocation(((StringNBT) inbt).getString())));
				} else if (inbt instanceof CompoundNBT) {
					SanityCallback callback = SanityCallbacks.createCallback(new ResourceLocation(((CompoundNBT) inbt).getString("id")));
					callback.getHandler().deserializeNBT(((CompoundNBT) inbt).getCompound("data"));
					callbacks.add(callback);
				} else {
					//TODO: Handle error in formatting
				}
			});
			this.loadedCallbacks.put(Integer.valueOf(startSanity), callbacks);
		});
	}
}
