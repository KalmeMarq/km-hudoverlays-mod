package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

public interface IHudOverlayConditionSerializer<T extends IHudOverlayCondition> {
    public T fromJson(JsonObject obj);
}
