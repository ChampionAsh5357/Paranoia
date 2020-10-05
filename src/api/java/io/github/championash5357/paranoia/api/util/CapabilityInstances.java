package io.github.championash5357.paranoia.api.util;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityInstances {

	@CapabilityInject(ISanity.class)
	public static final Capability<ISanity> SANITY_CAPABILITY = null;
}
