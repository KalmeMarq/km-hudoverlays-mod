package me.kalmemarq.hudoverlays;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.util.*;

public class HudOverlayManager implements SimpleSynchronousResourceReloadListener {
    public static final Random RANDOM = new Random();
    private static final Identifier RESOURCE = new Identifier(HudOverlayMod.MOD_ID, "hud_overlays.json");
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    public static Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");

    public static CustomHudOverlay pumpkinHudOverlay = new CustomHudOverlay();
    public static CustomHudOverlay powderSnowOverlay = new CustomHudOverlay();
    public static Map<String, CustomHudOverlay> customHudOverlays = Maps.newHashMap();

    @Override
    public Identifier getFabricId() {
        return new Identifier(HudOverlayMod.MOD_ID, "hud_overlays");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<String, CustomHudOverlay> cHOvs = Maps.newHashMap();
        CustomHudOverlay pumpkinOs = new CustomHudOverlay();
        CustomHudOverlay powderSnowOs = new CustomHudOverlay();

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
                            if (JsonHelper.getBoolean(pumpObj, "replace")) pumpkinOs = new CustomHudOverlay();
                        }

                        CustomHudOverlay customHudOverlay = CustomHudOverlay.Serializer.fromJson(pumpObj);
                        for (HudOverlay ov : customHudOverlay.getOverlays()) {
                            pumpkinOs.addOverlay(ov);
                        }
                    } else if (key.equals("minecraft:powder_snow_outline") && !s.equals("minecraft:powder_snow_outline")) {
                        JsonObject pumpObj = JsonHelper.getObject(obj, "minecraft:powder_snow_outline");

                        if (JsonHelper.hasBoolean(pumpObj, "replace")) {
                            if (JsonHelper.getBoolean(pumpObj, "replace")) powderSnowOs = new CustomHudOverlay();
                        }

                        CustomHudOverlay customHudOverlay = CustomHudOverlay.Serializer.fromJson(pumpObj);
                        for (HudOverlay ov : customHudOverlay.getOverlays()) {
                            powderSnowOs.addOverlay(ov);
                        }
                    } else {
                        if (!s.equals(key)) {
                            cHOvs.put(key, CustomHudOverlay.Serializer.fromJson(a.getValue().getAsJsonObject()));
                        }
                    }

                    s = key;
                }
            } catch (Exception e) {
                HudOverlayMod.LOGGER.info("Failed to load hud_overlays.json");
            }
        }

        pumpkinHudOverlay = pumpkinOs;
        powderSnowOverlay = powderSnowOs;

        customHudOverlays = cHOvs;
        HudOverlayMod.LOGGER.info("Loaded " + (pumpkinOs.getOverlays().size() > 0 ? "1" : "0") + " overlay for pumpkin");
        HudOverlayMod.LOGGER.info("Loaded " + (powderSnowOs.getOverlays().size() > 0 ? "1" : "0") + " overlay for powder snow");
        HudOverlayMod.LOGGER.info("Loaded " + customHudOverlays.size() + " custom overlays");
    }

    public static List<HudOverlay> getPumpkinOverlays() {
        return pumpkinHudOverlay.getOverlays();
    }

    public static List<HudOverlay> getPowderSnowOverlays() {
        return powderSnowOverlay.getOverlays();
    }

    public static Collection<CustomHudOverlay> getCustomHudOverlays() {
        return customHudOverlays.values();
    }
}
