package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

public interface IHudOverlayConditionSerializer {
    public IHudOverlayCondition fromJson(JsonObject obj);
}
