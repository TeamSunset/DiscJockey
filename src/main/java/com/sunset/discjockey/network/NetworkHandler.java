package com.sunset.discjockey.network;

import com.sunset.discjockey.network.message.ControllerSyncMessage;
import com.sunset.discjockey.network.message.MusicURLSyncMessage;
import com.sunset.discjockey.network.message.TagMessage;
import com.sunset.discjockey.util.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {
    private static final String VERSION = "1.0.0";

    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Reference.MOD_ID, "network"),
            () -> VERSION,
            it -> it.equals(VERSION),
            it -> it.equals(VERSION)
    );

    public static void init() {
        NETWORK_CHANNEL.registerMessage(
                0,
                TagMessage.class,
                TagMessage::encode,
                TagMessage::decode,
                TagMessage::handle
        );
        NETWORK_CHANNEL.registerMessage(
                1,
                MusicURLSyncMessage.class,
                MusicURLSyncMessage::encode,
                MusicURLSyncMessage::decode,
                MusicURLSyncMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        NETWORK_CHANNEL.registerMessage(
                2,
                ControllerSyncMessage.class,
                ControllerSyncMessage::encode,
                ControllerSyncMessage::decode,
                ControllerSyncMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

//    public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
//        if (world instanceof ServerLevel serverLevel) {
//
//            serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).stream()
//                    .filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 96 * 96)
//                    .forEach(p -> NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), toSend));
//        }
//    }
}
