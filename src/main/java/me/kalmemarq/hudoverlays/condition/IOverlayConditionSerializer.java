package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

public interface IOverlayConditionSerializer<T extends IOverlayCondition> {
    public T fromJson(JsonObject obj);
}
