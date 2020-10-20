package io.github.championash5357.paranoia.common.network.server;

import java.util.function.Supplier;

import io.github.championash5357.paranoia.client.ClientHandler;
import io.github.championash5357.paranoia.common.network.IMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SMobSounds implements IMessage {

	private final Vector3d pos;
	
	public SMobSounds(Vector3d pos) {
		this.pos = pos;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeDouble(this.pos.x);
		buffer.writeDouble(this.pos.y);
		buffer.writeDouble(this.pos.z);
	}
	
	public static SMobSounds decode(PacketBuffer buffer) {
		return new SMobSounds(new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
	}

	@Override
	public boolean handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(this.pos)));
		return true;
	}
}
