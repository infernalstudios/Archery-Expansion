package org.infernalstudios.archeryexp.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import org.infernalstudios.archeryexp.util.BowProperties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BowStatsLoader {

    public static void loadBowStats() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BowItem bowItem) {

                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                if (itemId == null) continue;

                String jsonFileName = itemId.getPath() + ".json";

                try (InputStream input = getResourceAsStream("data/" + itemId.getNamespace() + "/bow_stats/" + jsonFileName)) {
                    if (input == null) {
                        continue;
                    }

                    JsonObject json = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();

                    int cooldown = json.has("cooldown") ? json.get("cooldown").getAsInt() : 16;
                    float base_damage = json.has("base_damage") ? json.get("base_damage").getAsFloat() : 1.2f;
                    int draw_time = json.has("draw_time") ? json.get("draw_time").getAsInt() : 16;
                    float range = json.has("range") ? json.get("range").getAsFloat() : 1.0f;
                    float break_resist = json.has("break_resist") ? json.get("break_resist").getAsFloat() : 0.0f;
                    float speed = json.has("speed") ? json.get("speed").getAsFloat() : 0.8f;
                    int quickdraw = json.has("quickdraw") ? json.get("quickdraw").getAsInt() : 0;

                    ((BowProperties) bowItem).setSpecialProperties(true);

                    ((BowProperties) bowItem).setBowCooldown(cooldown);
                    ((BowProperties) bowItem).setBaseDamage(base_damage);
                    ((BowProperties) bowItem).setChargeTime(draw_time);
                    ((BowProperties) bowItem).setRange(range);
                    ((BowProperties) bowItem).setBreakingResistance(break_resist);
                    ((BowProperties) bowItem).setMovementSpeedMultiplier(speed);
                    ((BowProperties) bowItem).setQuickDrawLvl(quickdraw);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static InputStream getResourceAsStream(String path) {
        return BowStatsLoader.class.getClassLoader().getResourceAsStream(path);
    }
}
