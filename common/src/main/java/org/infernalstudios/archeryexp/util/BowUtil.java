package org.infernalstudios.archeryexp.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BowUtil {

    public static List<Vec3> getBowTrajectoryPoints(Player player, Item bow) {
        List<Vec3> trajectoryPoints;
        BowProperties properties = (BowProperties) bow;

        int steps = 200;

        if (properties.hasSpecialProperties()) {

            trajectoryPoints = calculateTrajectory(
                    player,
                    player.getUsedItemHand(),
                    getPowerForDrawTime(player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks(), properties),
                    properties.getRange(),
                    -player.getYRot() - 90,
                    player.getXRot(),
                    0.05f,
                    steps
            );
        } else {
            trajectoryPoints = calculateTrajectory(
                    player,
                    player.getUsedItemHand(),
                    BowItem.getPowerForTime(player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks()),
                    3,
                    -player.getYRot() - 90,
                    player.getXRot(),
                    0.05f,
                    steps
            );
        }

        return trajectoryPoints;
    }

    private static List<Vec3> calculateTrajectory(LivingEntity user, InteractionHand hand, float power, float range, float yaw, float pitch, float gravity, int steps) {
        List<Vec3> points = new ArrayList<>();

        Vec3 startPosition = user.getEyePosition(1.0f);

        Vec3 direction = getDirectionVector(yaw, pitch).scale(power * range);

        Vec3 position = startPosition;
        Vec3 velocity = direction;

        float defaultDrag = 0.99F;
        float waterDrag   = 0.8F;

        for (int i = 0; i < steps; i++) {

            if (position.distanceTo(startPosition) >= 1) {
                if (points.isEmpty() || points.get(points.size() - 1).distanceTo(position) >= 1) {
                    points.add(position);
                }
            }

            if (user.level().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z)).is(Blocks.WATER)) {
                velocity = velocity.scale(waterDrag);
            } else {
                velocity = velocity.scale(defaultDrag);
            }

            position = position.add(velocity);

            velocity = velocity.add(0, -gravity, 0);

            if (position.y < -64) break;
        }

        return points;
    }

    private static Vec3 getDirectionVector(float yaw, float pitch) {
        float radYaw = -yaw * Mth.DEG_TO_RAD;
        float radPitch = -pitch * Mth.DEG_TO_RAD;

        float x = Mth.cos(radPitch) * Mth.cos(radYaw);
        float y = Mth.sin(radPitch);
        float z = Mth.cos(radPitch) * Mth.sin(radYaw);

        return new Vec3(x, y, z);
    }

    public static float getPowerForDrawTime(int drawTime, BowProperties stack) {
        float power = (float) drawTime / stack.getChargeTime();
        power = (power * power + power * 2.0F) / 3.0F;

        if (power > 1.0F) {
            power = 1.0F;
        }

        return power;
    }
}
