package io.github.championash5357.paranoia.common.init;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import io.github.championash5357.paranoia.api.sanity.PlayerSanity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityRegistrar {

	public static void register() {
		CapabilityManager.INSTANCE.register(ISanity.class, new IStorage<ISanity>() {

			@Override
			public INBT writeNBT(Capability<ISanity> capability, ISanity instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<ISanity> capability, ISanity instance, Direction side, INBT nbt) {
				if(!(nbt instanceof CompoundNBT)) throw new IllegalArgumentException("INBT must be an instance of IntArrayNBT.");
				instance.deserializeNBT((CompoundNBT) nbt);
			}
			
		}, PlayerSanity::new);
	}

}
