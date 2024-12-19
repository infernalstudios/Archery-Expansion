package org.infernalstudios.archeryexp.items;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.BowProperties;
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
                        float speed = json.has("player_speed") ? json.get("player_speed").getAsFloat() : 0.8f;
                        float recoil = json.has("recoil") ? json.get("recoil").getAsFloat() : 0.0f;

                        List<PotionData> effects = new ArrayList<>();
                        boolean listFilled = false;

                        if (json.has("on_fire_effects") && json.get("on_fire_effects").isJsonArray()) {
                            JsonArray potionArray = json.getAsJsonArray("on_fire_effects");

                            if (!potionArray.isEmpty()) {
                                listFilled = true;
                                for (JsonElement potionElement : potionArray) {
                                    if (potionElement.isJsonObject()) {
                                        JsonObject potionObj = potionElement.getAsJsonObject();

                                        String effect = potionObj.has("effect") ? potionObj.get("effect").getAsString() : "minecraft:empty";
                                        int lvl = potionObj.has("lvl") ? potionObj.get("lvl").getAsInt() : 1;
                                        int length = potionObj.has("length") ? potionObj.get("length").getAsInt() : 0;

                                        PotionData data = new PotionData(effect, lvl, length);
                                        effects.add(data);
                                    }
                                }
                            }
                        }

                        ((BowProperties) bowItem).setSpecialProperties(true);
                        ((BowProperties) bowItem).setBowCooldown(cooldown);
                        ((BowProperties) bowItem).setBaseDamage(base_damage);
                        ((BowProperties) bowItem).setChargeTime(draw_time);
                        ((BowProperties) bowItem).setRange(range);
                        ((BowProperties) bowItem).setBreakingResistance(break_resist);
                        ((BowProperties) bowItem).setMovementSpeedMultiplier(speed);
                        ((BowProperties) bowItem).setRecoil(recoil);

                        if (listFilled) {
                            ((BowProperties) bowItem).setEffects(effects);
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
