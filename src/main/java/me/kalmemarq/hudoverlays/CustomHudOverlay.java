package me.kalmemarq.hudoverlays;

import me.kalmemarq.hudoverlays.condition.HudOverlayConditions;
import me.kalmemarq.hudoverlays.condition.IHudOverlayCondition;
import me.kalmemarq.hudoverlays.condition.IHudOverlayConditionSerializer;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomHudOverlay {
    private final List<HudOverlay> overlays;
    private final List<IHudOverlayCondition> conditions;

    public CustomHudOverlay() {
        this.overlays = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public void addOverlay(HudOverlay hudOverlay) {
        this.overlays.add(hudOverlay);
        this.overlays.sort(Comparator.comparingInt(HudOverlay::getLayer));
    }

    public void addCondition(IHudOverlayCondition condition) {
        this.conditions.add(condition);
    }

    public List<HudOverlay> getOverlays() {
        return this.overlays;
    }

    public boolean canDisplay(HudOverlayContext context) {
        for (IHudOverlayCondition cond : conditions) {
            if (!cond.test(context)) {
                return false;
            }
        }

        return true;
    }

    public static final class Serializer {
        public static CustomHudOverlay fromJson(JsonObject obj) {
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
                                JsonObject eaObj = ea.getAsJsonObject();
                                String type = JsonHelper.getString(eaObj, "condition");

                                IHudOverlayConditionSerializer<?> serializer = HudOverlayConditions.getSerializer(type);
                                if (serializer != null) {
                                    hudOverlay.addCondition(serializer.fromJson(eaObj));
                                }
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
                        String type = JsonHelper.getString(eObj, "condition");

                        IHudOverlayConditionSerializer<?> serializer = HudOverlayConditions.getSerializer(type);
                        if (serializer != null) {
                            customHudOverlay.addCondition(serializer.fromJson(eObj));
                        }
                    }
                }
            } catch (Exception e) {
                HudOverlayMod.LOGGER.info("Failed to parse conditions list");
            }

            return customHudOverlay;
        }
    }
}
