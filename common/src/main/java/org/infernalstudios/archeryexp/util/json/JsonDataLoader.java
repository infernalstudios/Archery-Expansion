package org.infernalstudios.archeryexp.util.json;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.ArcheryExpansion;
import org.infernalstudios.archeryexp.util.json.data.BowEffectData;
import org.infernalstudios.archeryexp.util.json.data.BowParticleData;
import org.infernalstudios.archeryexp.util.mixinterfaces.IBowProperties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonDataLoader {

    /**
     * Loops through all registered items in the game. If an item is a bow, it will try to find a json file for
     * bow stats and apply special properties to the bow item.
     */
    public static void loadBowStats(ResourceManager resourceManager) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BowItem bowItem) {
                IBowProperties bow = (IBowProperties) bowItem;
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                ResourceLocation fileLocation = new ResourceLocation(itemId.getNamespace(), "bow_stats/" + itemId.getPath() + ".json");
                try {
                    Resource resource = resourceManager.getResource(fileLocation).orElseThrow();
                    try (InputStream input = resource.open(); InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        bow.archeryexp$setSpecial(true);
                        
                        bow.archeryexp$setBowCooldown(getI(json, "cooldown", 16));
                        bow.archeryexp$setBaseDamage(getF(json, "base_damage", 1.2f));
                        bow.archeryexp$setChargeTime(getI(json, "draw_time", 16));
                        bow.archeryexp$setRange(getF(json, "range", 1));
                        bow.archeryexp$setBreakResist(getF(json, "break_resist", 0));
                        bow.archeryexp$setBreakChance(getF(json, "break_chance", 0.33f));
                        bow.archeryexp$setWalkSpeed(getF(json, "player_speed", 0.8f));
                        bow.archeryexp$setRecoil(getF(json, "recoil", 0));
                        bow.archeryexp$setOffsetX(getF(json, "arrow_offset_x", 0));
                        bow.archeryexp$setOffsetY(getF(json, "arrow_offset_y", 0));
                        bow.archeryexp$setHasDesc(getBl(json, "has_description", false));

                        bow.archeryexp$setEffects(getDataList(json, "on_fire_effects", BowEffectData::fromJson));
                        bow.archeryexp$setParticles(getDataList(json, "on_fire_particles", BowParticleData::fromJson));

                        ArcheryExpansion.LOGGER.info("Loaded Bow Stats for " + itemId);
                    } catch (Exception e) {
                        ArcheryExpansion.LOGGER.info("Failed to load Bow Stats for " + itemId);
                        bow.archeryexp$setSpecial(false);
                    }
                } catch (Exception e) {
                    ArcheryExpansion.LOGGER.info("No Bow Stats Json file found for " + itemId);
                }
            }
        }
        ArcheryExpansion.BOW_STAT_PLAYER_LIST.clear();
        ArcheryExpansion.LOGGER.info("Finished Loading Bow Stats");
    }

    // Various methods that check the json for a value, and either return the value from the json or a fallback value

    public static int getI(JsonObject json, String field, int fallback) {
        return json.has(field) ? json.get(field).getAsInt() : fallback;
    }

    public static float getF(JsonObject json, String field, float fallback) {
        return json.has(field) ? json.get(field).getAsFloat() : fallback;
    }

    public static boolean getBl(JsonObject json, String field, boolean fallback) {
        return json.has(field) ? json.get(field).getAsBoolean() : fallback;
    }

    /**
     * Gets a list of values from a "DataType" (ParticleData or PotionData)
     * @param json json object
     * @param field the name of the json field
     * @param dataType the method for getting the json data
     * @return returns a list of the datatypes
     * @param <T> the Data Type class
     */
    private static <T> List<T> getDataList(JsonObject json, String field, DataType<T> dataType) {
        List<T> list = new ArrayList<>();

        if (json.has(field) && json.get(field).isJsonArray()) {
            JsonArray jsonList = json.getAsJsonArray(field);

            jsonList.forEach(element -> {
                if (element.isJsonObject()) {
                    list.add(dataType.fromJson(element.getAsJsonObject()));
                }
            });
        }
        return list;
    }

    /**
     * Functional Interface for calling from Json in Particle or Potion Data
     * @param <T> the Data Type class
     */
    @FunctionalInterface
    public interface DataType<T> {
        T fromJson(JsonObject json);
    }
}
