package io.github.championash5357.paranoia.common.sanity.callback.handler;

import io.github.championash5357.paranoia.api.callback.HandlerClient;
import io.github.championash5357.paranoia.api.callback.ICallback.Phase;
import io.github.championash5357.paranoia.api.callback.SanityCallbacks.CallbackType;
import io.github.championash5357.paranoia.common.Paranoia;
import io.github.championash5357.paranoia.common.util.Helper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class FoggyClient extends HandlerClient {

	public static final ResourceLocation FOGGY = new ResourceLocation(Paranoia.ID, "foggy");
	
	public FoggyClient(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean test(ServerPlayerEntity player, int sanity, int prevSanity, Phase phase) {
		if(phase == Phase.STOP) {
			this.setStatus(STOP);
			return true;
		} else if(phase == Phase.START && this.getStatus() == NORMAL) return true;
		else {
			if(sanity <= 40 && prevSanity > 40 && Helper.random().nextInt(100) < 10) {
				this.setStatus(NORMAL);
				return true;
			} else if(sanity > 50 && this.getStatus() == NORMAL) {
				this.setStatus(STOP);
				return true;
			} else return false;
		}
	}

	@Override
	public CallbackType getType() {
		return CallbackType.OTHER;
	}
}
