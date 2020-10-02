package io.github.championash5357.paranoia.client;

import io.github.championash5357.paranoia.common.ISidedReference;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientReference implements ISidedReference {

	@Override
	public void setup(IEventBus mod, IEventBus forge) {
		mod.addListener(this::client);
	}

	private void client(final FMLClientSetupEvent event) {
		//ClientRegistry.registerEntityShader(ClientPlayerEntity.class, new ResourceLocation(Paranoia.ID, "shaders/post/full_desaturate.json"));
	}
}
