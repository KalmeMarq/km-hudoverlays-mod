package me.kalmemarq.hudoverlays;

import me.kalmemarq.hudoverlays.condition.OverlayConditions;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import me.kalmemarq.hudoverlays.condition.IOverlayConditionSerializer;
import me.kalmemarq.hudoverlays.nineslice.NinesliceInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.*;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CustomHudOverlay {
    private final List<HudOverlay> overlays;
    private final List<IOverlayCondition> conditions;

    public CustomHudOverlay() {
        this.overlays = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public void addOverlay(HudOverlay hudOverlay) {
        this.overlays.add(hudOverlay);
        this.overlays.sort(Comparator.comparingInt(HudOverlay::getLayer));
    }

    public void addCondition(IOverlayCondition condition) {
        this.conditions.add(condition);
    }

    public List<HudOverlay> getOverlays() {
        return this.overlays;
    }

    public boolean canDisplay(OverlayContext context) {
        for (IOverlayCondition cond : conditions) {
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

                        @Nullable
                        NinesliceInfo nsInfo = null;

                        if (JsonHelper.hasArray(eObj, "nineslice_size") && JsonHelper.hasArray(eObj, "base_size")) {
                            nsInfo = new NinesliceInfo();

                            nsInfo.setNineslice(OverlayProperty.NINESLICE_SIZE.parse(eObj));

                            int[] baseSize = OverlayProperty.BASE_SIZE.parse(eObj);
                            nsInfo.setBaseSize(baseSize[0], baseSize[1]);
                        }

                        HudOverlay hudOverlay = new HudOverlay(
                                OverlayProperty.TEXTURE.parse(eObj),
                                OverlayProperty.FIT_TO_SCREEN.parse(eObj),
                                OverlayProperty.LAYER.parse(eObj),
                                OverlayProperty.ALPHA.parse(eObj),
                                nsInfo);

                        List<IOverlayCondition> conds = OverlayProperty.CONDITIONS.parse(eObj);
                        for (IOverlayCondition cond : conds) {
                            hudOverlay.addCondition(cond);
                        }

                        customHudOverlay.addOverlay(hudOverlay);
                    }
                } catch (Exception e) {
                    HudOverlayMod.LOGGER.info("Failed to parse overlays list");
                }
            }

            try {
                List<IOverlayCondition> conds = OverlayProperty.CONDITIONS.parse(obj);
                for (IOverlayCondition cond : conds) {
                    customHudOverlay.addCondition(cond);
                }
            } catch (Exception e) {
                HudOverlayMod.LOGGER.info("Failed to parse conditions list");
            }

            return customHudOverlay;
        }
    }

    public static class OverlayProperty<T> {
        public static final OverlayProperty<Identifier> TEXTURE = new OverlayProperty<>("texture", el -> {
            if (el.isJsonPrimitive()) {
                try {
                    return Identifier.tryParse(el.getAsString());
                } catch (Exception e) {
                    return new Identifier("minecraft:textures/misc/powder_snow_outline.png");
                }
            }

            return new Identifier("minecraft:textures/misc/powder_snow_outline.png");
        }, new Identifier("minecraft:textures/misc/powder_snow_outline.png"));
        public static final OverlayProperty<Boolean> FIT_TO_SCREEN = new OverlayProperty<>("fit_to_screen", el -> !JsonHelper.isBoolean(el) || el.getAsBoolean(), true);
        public static final OverlayProperty<Integer> LAYER = new OverlayProperty<>("layer", el -> JsonHelper.isNumber(el) ? el.getAsInt() : -90, -90);
        public static final OverlayProperty<Float> ALPHA = new OverlayProperty<>("alpha", el -> JsonHelper.isNumber(el) ? el.getAsFloat() : 1.0f, 1.0f);
        public static final OverlayProperty<int[]> NINESLICE_SIZE = new OverlayProperty<>("nineslice_size", el -> {
            int[] arr = new int[] {0, 0, 0, 0};

            if (el.isJsonArray()) {
                JsonArray nsArr = el.getAsJsonArray();

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

                arr[0] = u0;
                arr[1] = v0;
                arr[2] = u1;
                arr[3] = v1;
            }

            return arr;
        }, new int[] {0, 0, 0, 0});
        public static final OverlayProperty<int[]> BASE_SIZE = new OverlayProperty<>("base_size", el -> {
            int[] arr = new int[] {256, 256};
            if (el.isJsonArray()) {
                JsonArray bsArr = el.getAsJsonArray();
                arr[0] = bsArr.get(1).getAsInt();
                arr[1] = bsArr.get(0).getAsInt();
            }
            return arr;
        }, new int[] {256, 256});
        public static final OverlayProperty<List<IOverlayCondition>> CONDITIONS = new OverlayProperty<>("conditions", el -> {
            List<IOverlayCondition> conds = new ArrayList<>();
            if (el.isJsonArray()) {
                JsonArray condArr = el.getAsJsonArray();

                for (JsonElement e : condArr) {
                    JsonObject eObj = e.getAsJsonObject();
                    String type = JsonHelper.getString(eObj, "condition");

                    IOverlayConditionSerializer<?> serializer = OverlayConditions.getSerializer(type);
                    if (serializer != null) {
                        conds.add(serializer.fromJson(eObj));
                    }
                }
            }

            return conds;
        }, new ArrayList<>());

        private final String name;
        private final OverlayPropertyParser<T> parser;
        private final T defaultValue;

        private OverlayProperty(String name, OverlayPropertyParser<T> parser, T defaultValue) {
            this.name = name;
            this.parser = parser;
            this.defaultValue = defaultValue;
        }

        public T parse(JsonObject element) {
            return element.has(this.name) ? this.parser.parse(element.get(this.name)) : defaultValue;
        }

        interface OverlayPropertyParser<T> {
            T parse(JsonElement element);
        }
    }
}
