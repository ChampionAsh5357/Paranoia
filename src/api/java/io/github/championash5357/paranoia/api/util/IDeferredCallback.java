package io.github.championash5357.paranoia.api.util;

import io.github.championash5357.paranoia.api.sanity.ISanity;
import net.minecraft.entity.player.ServerPlayerEntity;

@FunctionalInterface
public interface IDeferredCallback {

	void run(ServerPlayerEntity player, ISanity inst, int sanity, int prevSanity);
}
