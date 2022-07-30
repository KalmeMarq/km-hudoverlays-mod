package me.kalmemarq.hudoverlays;

import com.google.gson.*;
import me.kalmemarq.hudoverlays.condition.*;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.*;

public class HudOverlayManager implements SimpleSynchronousResourceReloadListener {
    public static final Random RANDOM = new Random();
    private static final Identifier RESOURCE = new Identifier(HudOverlayMod.MOD_ID, "hud_overlays.json");
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    public static Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");

    public static List<HudOverlay> pumpkinOverlays = new ArrayList<>();
    public static List<HudOverlay> powderSnowOverlays = new ArrayList<>();
    public static List<CustomHudOverlay> customHudOverlays = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(HudOverlayMod.MOD_ID, "hud_overlays");
    }

    @Override
    public void reload(ResourceManager manager) {
        List<HudOverlay> pumpkinOs = new ArrayList<>();
        List<HudOverlay> powderSnowOs = new ArrayList<>();
        List<CustomHudOverlay> customOs = new ArrayList<>();

        List<Resource> resL = manager.getAllResources(RESOURCE);

        for (Resource res : resL) {
            try {
                BufferedReader reader = res.getReader();

                JsonObject obj = GSON.fromJson(reader, JsonObject.class);

                String s = "";
                for (Map.Entry<String, JsonElement> a : obj.entrySet()) {
                    String key = a.getKey();

                    if (key.equals("minecraft:pumpkin_blur") && !s.equals("minecraft:pumpkin_blur")) {
                        JsonObject pumpObj = JsonHelper.getObject(obj, "minecraft:pumpkin_blur");

                        if (JsonHelper.hasBoolean(pumpObj, "replace")) {
                            if (JsonHelper.getBoolean(pumpObj, "replace")) pumpkinOs.clear();
                        }

                        if (JsonHelper.hasArray(pumpObj, "overlays")) {
                            JsonArray ovArr = JsonHelper.getArray(pumpObj, "overlays");

                            for (JsonElement e : ovArr) {
                                if (e.isJsonObject()) {
                                    JsonObject eObj = e.getAsJsonObject();

                                    Identifier txr = Identifier.tryParse(JsonHelper.getString(eObj, "texture", "minecraft:textures/misc/pumpkinblur.png"));
                                    boolean fitsScreen = JsonHelper.getBoolean(eObj, "fit_to_screen", true);
                                    int layer = JsonHelper.getInt(eObj, "layer", -90);
                                    float alpha = JsonHelper.getFloat(eObj, "alpha", 1.0f);

                                    @Nullable
                                    NinesliceInfo nsInfo = null;

                                    if (JsonHelper.hasArray(eObj, "nineslice_size") && JsonHelper.hasArray(eObj, "base_size")) {
                                        nsInfo = new NinesliceInfo();

                                        JsonArray nsArr = JsonHelper.getArray(eObj, "nineslice_size");

                                        int u0 = 0;
                                        int v0 = 0;
                                        int u1 = 0;
                                        int v1 = 0;

                                        if (nsArr.size() == 2) {
                                            u0 = nsArr.get(0).getAsInt();
                                            v0 = nsArr.get(1).getAsInt();
                                            u1 = u0;
                                            v1 = v0;
                                        } else if (nsArr.size() == 4) {
                                            u0 = nsArr.get(0).getAsInt();
                                            v0 = nsArr.get(1).getAsInt();
                                            u1 = nsArr.get(2).getAsInt();
                                            v1 = nsArr.get(3).getAsInt();
                                        }
                                        nsInfo.setNineslice(u0, v0, u1, v1);

                                        JsonArray bsArr = JsonHelper.getArray(eObj, "base_size");
                                        int baseWidth = bsArr.get(0).getAsInt();
                                        int baseHeight = bsArr.get(1).getAsInt();

                                        nsInfo.setBaseSize(baseWidth, baseHeight);
                                    }

                                    HudOverlay hudOverlay = new HudOverlay(txr, fitsScreen, layer, alpha, nsInfo);

                                    if (JsonHelper.hasArray(eObj, "conditions")) {
                                        JsonArray arr = JsonHelper.getArray(eObj, "conditions");

                                        for (JsonElement ea : arr) {
                                            parseCondition(hudOverlay, ea.getAsJsonObject());
                                        }
                                    }

                                    pumpkinOs.add(hudOverlay);
                                }
                            }
                        }
                    } else if (key.equals("minecraft:powder_snow_outline") && !s.equals("minecraft:powder_snow_outline")) {
                        JsonObject pumpObj = JsonHelper.getObject(obj, "powder_snow");

                        if (JsonHelper.hasBoolean(pumpObj, "replace")) {
                            if (JsonHelper.getBoolean(pumpObj, "replace")) powderSnowOs.clear();
                        }

                        if (JsonHelper.hasArray(pumpObj, "overlays")) {
                            JsonArray ovArr = JsonHelper.getArray(pumpObj, "overlays");

                            for (JsonElement e : ovArr) {
                                if (e.isJsonObject()) {
                                    JsonObject eObj = e.getAsJsonObject();

                                    Identifier txr = Identifier.tryParse(JsonHelper.getString(eObj, "texture", "minecraft:textures/misc/powder_snow_outline.png"));
                                    boolean fitsScreen = JsonHelper.getBoolean(eObj, "fit_to_screen", true);
                                    int layer = JsonHelper.getInt(eObj, "layer", -90);
                                    float alpha = JsonHelper.getFloat(eObj, "alpha", 1.0f);

                                    @Nullable
                                    NinesliceInfo nsInfo = null;

                                    if (JsonHelper.hasArray(eObj, "nineslice_size") && JsonHelper.hasArray(eObj, "base_size")) {
                                        nsInfo = new NinesliceInfo();

                                        JsonArray nsArr = JsonHelper.getArray(eObj, "nineslice_size");

                                        int u0 = 0;
                                        int v0 = 0;
                                        int u1 = 0;
                                        int v1 = 0;

                                        if (nsArr.size() == 2) {
                                            u0 = nsArr.get(0).getAsInt();
                                            v0 = nsArr.get(1).getAsInt();
                                            u1 = u0;
                                            v1 = v0;
                                        } else if (nsArr.size() == 4) {
                                            u0 = nsArr.get(0).getAsInt();
                                            v0 = nsArr.get(1).getAsInt();
                                            u1 = nsArr.get(2).getAsInt();
                                            v1 = nsArr.get(3).getAsInt();
                                        }

                                        nsInfo.setNineslice(u0, v0, u1, v1);

                                        JsonArray bsArr = JsonHelper.getArray(eObj, "base_size");
                                        int baseWidth = bsArr.get(0).getAsInt();
                                        int baseHeight = bsArr.get(1).getAsInt();

                                        nsInfo.setBaseSize(baseWidth, baseHeight);
                                    }

                                    HudOverlay hudOverlay = new HudOverlay(txr, fitsScreen, layer, alpha, nsInfo);

                                    if (JsonHelper.hasArray(eObj, "conditions")) {
                                        JsonArray arr = JsonHelper.getArray(eObj, "conditions");

                                        for (JsonElement ea : arr) {
                                            parseCondition(hudOverlay, ea.getAsJsonObject());
                                        }
                                    }

                                    powderSnowOs.add(hudOverlay);
                                }
                            }
                        }
                    } else {
                        if (!s.equals(key)) parseCustomOverlays(customOs, a.getValue().getAsJsonObject());
                    }

                    s = key;
                }
            } catch (Exception e) {
                HudOverlayMod.LOGGER.info("Failed to load hud_overlays.json");
            }
        }

        pumpkinOverlays.clear();
        pumpkinOverlays.addAll(pumpkinOs);
        pumpkinOverlays.sort(Comparator.comparingInt(HudOverlay::getLayer));
        powderSnowOverlays.clear();
        powderSnowOverlays.addAll(powderSnowOs);
        powderSnowOverlays.sort(Comparator.comparingInt(HudOverlay::getLayer));
        customHudOverlays.clear();
        customHudOverlays.addAll(customOs);

        HudOverlayMod.LOGGER.info("Loaded " + pumpkinOverlays.size() + " overlays for pumpkin");
        HudOverlayMod.LOGGER.info("Loaded " + powderSnowOverlays.size() + " overlays for powderSnow");
        HudOverlayMod.LOGGER.info("Loaded " + customHudOverlays.size() + " custom overlays");
    }

    private static void parseCondition(HudOverlay hudOverlay, JsonObject obj) {
        String type = JsonHelper.getString(obj, "condition");

        if (type.equals("has_item_nbt_property")) {
            String name = JsonHelper.getString(obj, "name");
            hudOverlay.addCondition(new HasItemNbtPropertyCondition(name));
            return;
        }

        if (type.equals("is_underwater")) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            hudOverlay.addCondition(new UnderwaterCondition(value));
            return;
        }

        if (type.equals("is_gamemode")) {
            String gm = JsonHelper.getString(obj, "name");
            hudOverlay.addCondition(new GameModeCondition(GameMode.byName(gm, GameMode.CREATIVE)));
            return;
        }

        if (type.equals("is_difficulty")) {
            String df = JsonHelper.getString(obj, "name");
            hudOverlay.addCondition(new DifficultyCondition(Difficulty.byName(df)));
            return;
        }

        if (type.equals("player_properties")) {
            JsonObject props = new JsonObject();
            if (JsonHelper.hasJsonObject(obj, "properties")) {
                props = JsonHelper.getObject(obj, "properties");
            }
            hudOverlay.addCondition(new PlayerPropertiesCondition(props));
            return;
        }

        if (type.equals("has_item_in_inventory")) {
            String name = JsonHelper.getString(obj, "item");
            Item item = Registry.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            hudOverlay.addCondition(new HasItemInInventoryCondition(item, count));
        }

        if (type.equals("has_item")) {
            String slot = JsonHelper.getString(obj, "slot");
            String name = JsonHelper.getString(obj, "item");
            Item item = Registry.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            JsonObject nbt = JsonHelper.getObject(obj, "nbt", null);
            boolean equalNBT = JsonHelper.getString(obj, "match_nbt", "equal").equals("equal");
            hudOverlay.addCondition(new HasItemCondition(slot, item, count, nbt, equalNBT));
        }
    }

    private static void parseCondition(CustomHudOverlay customHudOverlay, JsonObject obj) {
        String type = JsonHelper.getString(obj, "condition");

        if (type.equals("has_item_nbt_property")) {
            String name = JsonHelper.getString(obj, "name");
            customHudOverlay.addCondition(new HasItemNbtPropertyCondition(name));
        }

        if (type.equals("is_underwater")) {
            boolean value = JsonHelper.getBoolean(obj, "value", true);
            customHudOverlay.addCondition(new UnderwaterCondition(value));
            return;
        }

        if (type.equals("is_gamemode")) {
            String gm = JsonHelper.getString(obj, "name");
            customHudOverlay.addCondition(new GameModeCondition(GameMode.byName(gm, GameMode.CREATIVE)));
        }

        if (type.equals("is_difficulty")) {
            String df = JsonHelper.getString(obj, "name");
            customHudOverlay.addCondition(new DifficultyCondition(Difficulty.byName(df)));
            return;
        }

        if (type.equals("player_properties")) {
            JsonObject props = new JsonObject();
            if (JsonHelper.hasJsonObject(obj, "properties")) {
                props = JsonHelper.getObject(obj, "properties");
            }
            customHudOverlay.addCondition(new PlayerPropertiesCondition(props));
            return;
        }

        if (type.equals("has_item_in_inventory")) {
            String name = JsonHelper.getString(obj, "item");
            Item item = Registry.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            customHudOverlay.addCondition(new HasItemInInventoryCondition(item, count));
        }

        if (type.equals("has_item")) {
            String slot = JsonHelper.getString(obj, "slot");
            String name = JsonHelper.getString(obj, "item");
            Item item = Registry.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            JsonObject nbt = JsonHelper.getObject(obj, "nbt", null);
            boolean equalNBT = JsonHelper.getString(obj, "match_nbt", "equal").equals("equal");
            customHudOverlay.addCondition(new HasItemCondition(slot, item, count, nbt, equalNBT));
        }
    }

    private static void parseCustomOverlays(List<CustomHudOverlay> list, JsonObject obj) {
        CustomHudOverlay customHudOverlay = new CustomHudOverlay();

        if (JsonHelper.hasArray(obj, "overlays")) {
            try {
                JsonArray ovArr = JsonHelper.getArray(obj, "overlays");

                for (JsonElement e : ovArr) {
                    JsonObject eObj = e.getAsJsonObject();

                    Identifier txr = Identifier.tryParse(JsonHelper.getString(eObj, "texture", "minecraft:textures/misc/powder_snow_outline.png"));
                    boolean fitsScreen = JsonHelper.getBoolean(eObj, "fit_to_screen", true);
                    int layer = JsonHelper.getInt(eObj, "layer", -90);
                    float alpha = JsonHelper.getFloat(eObj, "alpha", 1.0f);

                    @Nullable
                    NinesliceInfo nsInfo = null;

                    if (JsonHelper.hasArray(eObj, "nineslice_size") && JsonHelper.hasArray(eObj, "base_size")) {
                        nsInfo = new NinesliceInfo();

                        JsonArray nsArr = JsonHelper.getArray(eObj, "nineslice_size");

                        int u0 = 0;
                        int v0 = 0;
                        int u1 = 0;
                        int v1 = 0;

                        if (nsArr.size() == 2) {
                            u0 = nsArr.get(0).getAsInt();
                            v0 = nsArr.get(1).getAsInt();
                            u1 = u0;
                            v1 = v0;
                        } else if (nsArr.size() == 4) {
                            u0 = nsArr.get(0).getAsInt();
                            v0 = nsArr.get(1).getAsInt();
                            u1 = nsArr.get(2).getAsInt();
                            v1 = nsArr.get(3).getAsInt();
                        }

                        nsInfo.setNineslice(u0, v0, u1, v1);

                        JsonArray bsArr = JsonHelper.getArray(eObj, "base_size");
                        int baseWidth = bsArr.get(0).getAsInt();
                        int baseHeight = bsArr.get(1).getAsInt();

                        nsInfo.setBaseSize(baseWidth, baseHeight);
                    }

                    HudOverlay hudOverlay = new HudOverlay(txr, fitsScreen, layer, alpha, nsInfo);

                    if (JsonHelper.hasArray(eObj, "conditions")) {
                        JsonArray arr = JsonHelper.getArray(eObj, "conditions");

                        for (JsonElement ea : arr) {
                            parseCondition(hudOverlay, ea.getAsJsonObject());
                        }
                    }

                    customHudOverlay.addOverlay(hudOverlay);
                }
            } catch (Exception e) {
                HudOverlayMod.LOGGER.info("Failed to parse overlays list");
            }
        }

        try {
            if (JsonHelper.hasArray(obj, "conditions")) {
                JsonArray condArr = JsonHelper.getArray(obj, "conditions");

                for (JsonElement e : condArr) {
                    JsonObject eObj = e.getAsJsonObject();
                    parseCondition(customHudOverlay, eObj);
                }
            }
        } catch (Exception e) {
            HudOverlayMod.LOGGER.info("Failed to parse conditions list");
        }

        list.add(customHudOverlay);
    }

    public static List<HudOverlay> getPumpkinOverlays() {
        return pumpkinOverlays;
    }

    public static List<HudOverlay> getPowderSnowOverlays() {
        return powderSnowOverlays;
    }

    public static List<CustomHudOverlay> getCustomHudOverlays() {
        return customHudOverlays;
    }
}
