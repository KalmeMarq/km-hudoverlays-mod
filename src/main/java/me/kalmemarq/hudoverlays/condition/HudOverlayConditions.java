package me.kalmemarq.hudoverlays.condition;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

public class HudOverlayConditions {
    private static final Map<String, IHudOverlayConditionSerializer<?>> SERIALIZERS = Maps.newHashMap();

    static {
        register("is_difficulty", new DifficultyCondition.Serializer());
        register("is_gamemode", new GameModeCondition.Serializer());
        register("has_item", new HasItemCondition.Serializer());
        register("has_item_in_inventory", new HasItemInInventoryCondition.Serializer());
        register("has_item_nbt_property", new HasItemNbtPropertyCondition.Serializer());
        register("player_properties", new PlayerPropertiesCondition.Serializer());
        register("is_underwater", new UnderwaterCondition.Serializer());
        register("is_using_spyglass", new IsUsingSpyglass.Serializer());
    }

    private static void register(String name, IHudOverlayConditionSerializer<?> serializer) {
        SERIALIZERS.put(name, serializer);
    }

    @Nullable
    public static IHudOverlayConditionSerializer<?> getSerializer(String name) {
        return SERIALIZERS.get(name);
    }
}
