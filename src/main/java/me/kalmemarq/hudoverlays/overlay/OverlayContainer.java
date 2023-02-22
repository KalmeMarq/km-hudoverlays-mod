package me.kalmemarq.hudoverlays.overlay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.OverlayContext;
import me.kalmemarq.hudoverlays.HudOverlayMod;
import me.kalmemarq.hudoverlays.condition.OverlayConditions;
import me.kalmemarq.hudoverlays.condition.IOverlayCondition;
import me.kalmemarq.hudoverlays.condition.IOverlayConditionSerializer;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverlayContainer {
    public static final OverlayContainer EMPTY = new OverlayContainer(new ArrayList<>(), new ArrayList<>());

    private final List<IOverlayCondition> conditions;
    private final List<Overlay> overlays;

    public OverlayContainer(List<Overlay> overlays, List<IOverlayCondition> conditions) {
        this.overlays = overlays;
        this.conditions = conditions;
        this.overlays.sort(Comparator.comparingInt(Overlay::getLayer));
    }

    public void addOverlays(List<Overlay> overlays) {
        this.overlays.addAll(overlays);
    }

    public void addConditions(List<IOverlayCondition> conditions) {
        this.conditions.addAll(conditions);
        this.overlays.sort(Comparator.comparingInt(Overlay::getLayer));
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    public List<IOverlayCondition> getConditions() {
        return conditions;
    }

    public boolean canDisplay(OverlayContext context) {
        for (IOverlayCondition condition : conditions) {
            if (!condition.test(context)) {
                return false;
            }
        }

        return true;
    }

    public static OverlayContainer deserialize(String name, String resourcepack, JsonObject obj) {
        List<Overlay> overlays = new ArrayList<>();
        List<IOverlayCondition> conditions = new ArrayList<>();

        if (JsonHelper.hasArray(obj, "overlays")) {
            JsonArray ovArr = JsonHelper.getArray(obj, "overlays");

            int i = 0;
            for (JsonElement el : ovArr) {
                JsonObject eObj = el.getAsJsonObject();

                try {
                    overlays.add(Overlay.deserialize(eObj));
                } catch (Exception e) {
                    HudOverlayMod.LOGGER.error("Failed to parse overlay in " + resourcepack + "/" + name + "[" + i + "]");
                }

                ++i;
            }
        }

        try {
            if (JsonHelper.hasArray(obj, "conditions")) {
                JsonArray condArr = JsonHelper.getArray(obj, "conditions");

                for (JsonElement e : condArr) {
                    JsonObject eObj = e.getAsJsonObject();
                    String type = JsonHelper.getString(eObj, "condition");

                    IOverlayConditionSerializer<?> serializer = OverlayConditions.getSerializer(type);
                    if (serializer != null) {
                        conditions.add(serializer.fromJson(eObj));
                    }
                }
            }
        } catch (Exception e) {
            HudOverlayMod.LOGGER.error("Failed to parse conditions list in " + resourcepack + "/" + name);
        }

        return new OverlayContainer(overlays, conditions);
    }
}
