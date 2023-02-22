package me.kalmemarq.hudoverlays;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.overlay.OverlayContainer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OverlayManager implements SimpleSynchronousResourceReloadListener {
    private static final Identifier RESOURCE = new Identifier(HudOverlayMod.MOD_ID, "hud_overlays.json");
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    public static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");

    public static OverlayContainer PUMPKIN_OC = OverlayContainer.EMPTY;
    public static OverlayContainer SPYGLASS_OC = OverlayContainer.EMPTY;
    public static OverlayContainer POWDER_SNOW_OC = OverlayContainer.EMPTY;
    public static OverlayContainer PORTAL_OC = OverlayContainer.EMPTY;
    public static OverlayContainer VIGNETTE_OC = OverlayContainer.EMPTY;
    public static OverlayContainer UNDERWATER_OC = OverlayContainer.EMPTY;
    public static Map<String, OverlayContainer> CONTAINERS = Maps.newHashMap();

    @Override
    public Identifier getFabricId() {
        return new Identifier(HudOverlayMod.MOD_ID, "hud_overlays");
    }

    @Override
    public void reload(ResourceManager manager) {
        try {
            Map<String, OverlayContainer> nContainers = Maps.newHashMap();

            for (Resource resource : manager.getAllResources(RESOURCE)) {
                try (InputStreamReader ireader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(ireader)) {
                    JsonObject obj = GSON.fromJson(reader, JsonObject.class);

                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                        String key = entry.getKey();

                        JsonObject eObj = entry.getValue().getAsJsonObject();
                        boolean replace = JsonHelper.getBoolean(eObj, "replace", false);

                        if (replace || !nContainers.containsKey(key)) {
                            nContainers.put(key, OverlayContainer.deserialize(key, resource.getResourcePackName(), eObj));
                        } else {
                            OverlayContainer oldContainer = nContainers.get(key);
                            OverlayContainer newContainer = OverlayContainer.deserialize(key, resource.getResourcePackName(), eObj);
                            oldContainer.addOverlays(newContainer.getOverlays());
                            oldContainer.addConditions(newContainer.getConditions());
                            nContainers.put(key, oldContainer);
                        }
                    }
                } catch (Exception e) {
                    HudOverlayMod.LOGGER.error("Failed to load hud_overlays.json in " + resource.getResourcePackName());
                }
            }

            if (nContainers.containsKey("minecraft:underwater")) {
                UNDERWATER_OC = nContainers.get("minecraft:underwater");
                nContainers.remove("minecraft:underwater");
            }

            if (nContainers.containsKey("minecraft:pumpkin_blur")) {
                PUMPKIN_OC = nContainers.get("minecraft:pumpkin_blur");
                nContainers.remove("minecraft:pumpkin_blur");
            }

            if (nContainers.containsKey("minecraft:powder_snow_outline")) {
                POWDER_SNOW_OC = nContainers.get("minecraft:powder_snow_outline");
                nContainers.remove("minecraft:powder_snow_outline");
            }

            if (nContainers.containsKey("minecraft:vignette")) {
                VIGNETTE_OC = nContainers.get("minecraft:vignette");
                nContainers.remove("minecraft:vignette");
            }

            if (nContainers.containsKey("minecraft:spyglass")) {
                SPYGLASS_OC = nContainers.get("minecraft:spyglass");
                nContainers.remove("minecraft:spyglass");
            }

            if (nContainers.containsKey("minecraft:portal")) {
                PORTAL_OC = nContainers.get("minecraft:portal");
                nContainers.remove("minecraft:portal");
            }

            CONTAINERS = nContainers;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasCustomPumpkin() {
        return PUMPKIN_OC.getOverlays().size() > 0 || PUMPKIN_OC.getConditions().size() > 0;
    }

    public static boolean hasCustomPortal() {
        return PORTAL_OC.getOverlays().size() > 0 || PUMPKIN_OC.getConditions().size() > 0;
    }

    public static boolean hasCustomVignette() {
        return VIGNETTE_OC.getOverlays().size() > 0 || PUMPKIN_OC.getConditions().size() > 0;
    }

    public static boolean hasCustomUnderwater() {
        return UNDERWATER_OC.getOverlays().size() > 0 || PUMPKIN_OC.getConditions().size() > 0;
    }

    public static boolean hasCustomSnowPowder() {
        return POWDER_SNOW_OC.getOverlays().size() > 0 || POWDER_SNOW_OC.getConditions().size() > 0;
    }

    public static boolean hasCustomSpyglass() {
        return SPYGLASS_OC.getOverlays().size() > 0 || SPYGLASS_OC.getConditions().size() > 0;
    }

    public static Collection<OverlayContainer> getCustomOverlayContainers() {
        return CONTAINERS.values();
    }
}
