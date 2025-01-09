package org.infernalstudios.archeryexp.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;

public class ArcheryNetworkingFabric {

    public static final ResourceLocation BowStatsPacket = new ResourceLocation(ArcheryExpansion.MOD_ID, "bow_stats_packet");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BowStatsPacket, ArcheryNetworkingFabric::bowStatsRecieve);
    }

    private static void bowStatsRecieve(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        Item bow = buf.readItem().getItem();
        float range = buf.readFloat();
        int drawTime = buf.readInt();
        float speed = buf.readFloat();

        ((BowProperties) bow).setSpecialProperties(true);
        ((BowProperties) bow).setRange(range);
        ((BowProperties) bow).setChargeTime(drawTime);
        ((BowProperties) bow).setMovementSpeedMultiplier(speed);
//        ArcheryExpansion.LOGGER.info("Bow Packet Received");
    }

    public static void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeItem(bow);
        buf.writeFloat(range);
        buf.writeInt(drawTime);
        buf.writeFloat(speed);
        ServerPlayNetworking.send(player, BowStatsPacket, buf);
    }

}
