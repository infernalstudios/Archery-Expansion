package org.infernalstudios.archeryexp.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;

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
        float x = buf.readFloat();
        float y = buf.readFloat();

        ((IBowProperties) bow).archeryexp$setSpecial(true);
        ((IBowProperties) bow).archeryexp$setRange(range);
        ((IBowProperties) bow).archeryexp$setChargeTime(drawTime);
        ((IBowProperties) bow).archeryexp$setWalkSpeed(speed);
        ((IBowProperties) bow).archeryexp$setOffsetX(x);
        ((IBowProperties) bow).archeryexp$setOffsetY(y);
    }

    public static void sendBowStatsPacket(ServerPlayer player, ItemStack bow, float range, int drawTime, float speed, float x, float y) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeItem(bow);
        buf.writeFloat(range);
        buf.writeInt(drawTime);
        buf.writeFloat(speed);
        buf.writeFloat(x);
        buf.writeFloat(y);
        ServerPlayNetworking.send(player, BowStatsPacket, buf);
    }

}
