package org.infernalstudios.archeryexp.items;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;
import org.infernalstudios.archeryexp.util.ParticleData;
import org.infernalstudios.archeryexp.util.PotionData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BowStatsLoader {

    public static void loadBowStats(ResourceManager resourceManager) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BowItem bowItem) {

                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);

                String jsonFileName = itemId.getPath() + ".json";
                ResourceLocation fileLocation = new ResourceLocation(itemId.getNamespace(), "bow_stats/" + jsonFileName);
                try {
                    Optional<Resource> resourceOptional = resourceManager.getResource(fileLocation);
                    if (resourceOptional.isEmpty()) {
                        continue;
                    }

                    Resource resource = resourceOptional.get();
                    try (InputStream input = resource.open();
                         InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        int cooldown = json.has("cooldown") ? json.get("cooldown").getAsInt() : 16;
                        float base_damage = json.has("base_damage") ? json.get("base_damage").getAsFloat() : 1.2f;
                        int draw_time = json.has("draw_time") ? json.get("draw_time").getAsInt() : 16;
                        float range = json.has("range") ? json.get("range").getAsFloat() : 1.0f;
                        float break_resist = json.has("break_resist") ? json.get("break_resist").getAsFloat() : 0.0f;
                        float break_chance = json.has("break_chance") ? json.get("break_chance").getAsFloat() : 0.33f;
                        float speed = json.has("player_speed") ? json.get("player_speed").getAsFloat() : 0.8f;
                        float recoil = json.has("recoil") ? json.get("recoil").getAsFloat() : 0.0f;

                        List<PotionData> effects = new ArrayList<>();
                        boolean potlistFilled = false;

                        if (json.has("on_fire_effects") && json.get("on_fire_effects").isJsonArray()) {
                            JsonArray potionArray = json.getAsJsonArray("on_fire_effects");

                            if (!potionArray.isEmpty()) {
                                potlistFilled = true;
                                for (JsonElement potionElement : potionArray) {
                                    if (potionElement.isJsonObject()) {
                                        JsonObject potionObj = potionElement.getAsJsonObject();

                                        String effect = potionObj.has("effect") ? potionObj.get("effect").getAsString() : "minecraft:empty";
                                        int lvl = potionObj.has("lvl") ? potionObj.get("lvl").getAsInt() : 1;
                                        int length = potionObj.has("length") ? potionObj.get("length").getAsInt() : 0;
                                        boolean particles = potionObj.has("particles") ? potionObj.get("particles").getAsBoolean() : false;

                                        PotionData data = new PotionData(effect, lvl, length, particles);
                                        effects.add(data);
                                    }
                                }
                            }
                        }

                        List<ParticleData> particles = new ArrayList<>();
                        boolean parlistFilled = false;

                        if (json.has("on_fire_particles") && json.get("on_fire_particles").isJsonArray()) {
                            JsonArray particleArray = json.getAsJsonArray("on_fire_particles");

                            if (!particleArray.isEmpty()) {
                                parlistFilled = true;
                                for (JsonElement particleElement : particleArray) {
                                    if (particleElement.isJsonObject()) {
                                        JsonObject particleObj = particleElement.getAsJsonObject();

                                        String effect = particleObj.has("effect") ? particleObj.get("effect").getAsString() : "minecraft:empty";
                                        float x = particleObj.has("xoffset") ? particleObj.get("xoffset").getAsFloat() : 0;
                                        float y = particleObj.has("yoffset") ? particleObj.get("yoffset").getAsFloat() : 0;
                                        float z = particleObj.has("zoffset") ? particleObj.get("zoffset").getAsFloat() : 0;

                                        float vx = particleObj.has("xvel") ? particleObj.get("xvel").getAsFloat() : 0;
                                        float vy = particleObj.has("yvel") ? particleObj.get("yvel").getAsFloat() : 0;
                                        float vz = particleObj.has("zvel") ? particleObj.get("zvel").getAsFloat() : 0;

                                        int count = particleObj.has("count") ? particleObj.get("count").getAsInt() : 0;
                                        float lookOffset = particleObj.has("look_offset") ? particleObj.get("look_offset").getAsFloat() : 0;

                                        ParticleData data = new ParticleData(effect, new Vec3(x, y, z), new Vec3(vx, vy, vz), count, lookOffset);
                                        particles.add(data);
                                    }
                                }
                            }
                        }

                        float offset_X = json.has("arrow_offset_x") ? json.get("arrow_offset_x").getAsFloat() : 0;
                        float offset_Y = json.has("arrow_offset_y") ? json.get("arrow_offset_y").getAsFloat() : 0;

                        boolean hasDescText = json.has("has_description") ? json.get("has_description").getAsBoolean() : false;

                        ((BowProperties) bowItem).setSpecialProperties(true);
                        ((BowProperties) bowItem).setBowCooldown(cooldown);
                        ((BowProperties) bowItem).setBaseDamage(base_damage);
                        ((BowProperties) bowItem).setChargeTime(draw_time);
                        ((BowProperties) bowItem).setRange(range);
                        ((BowProperties) bowItem).setBreakingResistance(break_resist);
                        ((BowProperties) bowItem).setBreakingChance(break_chance);
                        ((BowProperties) bowItem).setMovementSpeedMultiplier(speed);
                        ((BowProperties) bowItem).setRecoil(recoil);
                        ((BowProperties) bowItem).setOffsetX(offset_X);
                        ((BowProperties) bowItem).setOffsetY(offset_Y);
                        ((BowProperties) bowItem).setHasDescText(hasDescText);
                        ArcheryExpansion.bowStatPlayerList.clear();

                        if (potlistFilled) {
                            ((BowProperties) bowItem).setEffects(effects);
                        }
                        if (parlistFilled) {
                            ((BowProperties) bowItem).setParticles(particles);
                        }

                        ArcheryExpansion.LOGGER.info("Loaded Bow Stats for " + itemId.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ArcheryExpansion.LOGGER.info("Finished Loading Bow Stats");
    }
}
