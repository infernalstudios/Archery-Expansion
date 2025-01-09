package org.infernalstudios.archeryexp.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;

import java.util.function.Supplier;

public class ArcheryNetworkingForge {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ArcheryExpansion.MOD_ID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void registerPackets() {
        CHANNEL.registerMessage(
                0,
                BowStatsPacket.class,
                BowStatsPacket::encode,
                BowStatsPacket::decode,
                BowStatsPacket::handle
        );
    }

    public static class BowStatsPacket {
        private final Item bow;
        private final float range;
        private final int drawTime;
        private final float speed;

        public BowStatsPacket(Item bow, float range, int drawTime, float speed) {
            this.bow = bow;
            this.range = range;
            this.drawTime = drawTime;
            this.speed = speed;
        }

        public static void encode(BowStatsPacket packet, FriendlyByteBuf buf) {
            buf.writeItem(new ItemStack(packet.bow));
            buf.writeFloat(packet.range);
            buf.writeInt(packet.drawTime);
            buf.writeFloat(packet.speed);
        }

        public static BowStatsPacket decode(FriendlyByteBuf buf) {
            Item bow = buf.readItem().getItem();
            float range = buf.readFloat();
            int drawTime = buf.readInt();
            float speed = buf.readFloat();
            return new BowStatsPacket(bow, range, drawTime, speed);
        }

        public static void handle(BowStatsPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    Item bow = packet.bow;
                    float range = packet.range;
                    int drawTime = packet.drawTime;
                    float speed = packet.speed;

                    if (bow instanceof BowProperties) {
                        ((BowProperties) bow).setSpecialProperties(true);
                        ((BowProperties) bow).setRange(range);
                        ((BowProperties) bow).setChargeTime(drawTime);
                        ((BowProperties) bow).setMovementSpeedMultiplier(speed);
//                        ArcheryExpansion.LOGGER.info("Bow Packet Received");
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }

    public static void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new BowStatsPacket(bow.getItem(), range, drawTime, speed));
    }

}
