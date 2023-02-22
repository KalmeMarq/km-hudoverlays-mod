package me.kalmemarq.hudoverlays.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;

import java.util.Map;

public class PlayerPropertiesCondition implements IOverlayCondition {
    private final JsonObject props;

    public PlayerPropertiesCondition(JsonObject props) {
        this.props = props;
    }

    @Override
    public boolean test(OverlayContext context) {
        PlayerEntity player = context.getPlayer();

        for (Map.Entry<String, JsonElement> e : this.props.entrySet()) {
            String k = e.getKey();
            JsonElement v = e.getValue();

            if (JsonHelper.isBoolean(v)) {
                if (PlayerProperty.has(k)) {
                    return v.getAsBoolean() == PlayerProperty.get(k, player);
                }
            }
        }

        return true;
    }

    public static final class Serializer implements IOverlayConditionSerializer<PlayerPropertiesCondition> {
        @Override
        public PlayerPropertiesCondition fromJson(JsonObject obj) {
            JsonObject props = new JsonObject();
            if (JsonHelper.hasJsonObject(obj, "properties")) {
                props = JsonHelper.getObject(obj, "properties");
            }
            return new PlayerPropertiesCondition(props);
        }
    }

    public static final class PlayerProperty {
        public static Map<String, PPCheckFunc> PP = Maps.newHashMap();

        static {
            register("is_on_fire", PlayerEntity::isOnFire);
            register("is_on_ground", PlayerEntity::isOnGround);
            register("is_holding_onto_ladder", PlayerEntity::isHoldingOntoLadder);
            register("is_using_splyglass", PlayerEntity::isUsingSpyglass);
            register("is_fall_flying", PlayerEntity::isFallFlying);
            register("is_blocking", PlayerEntity::isBlocking);
            register("is_dead", PlayerEntity::isDead);
            register("is_alive", PlayerEntity::isAlive);
            register("is_glowing", PlayerEntity::isGlowing);
            register("is_sneaking", PlayerEntity::isSneaking);
            register("is_sprinting", PlayerEntity::isSprinting);
            register("is_in_lava", PlayerEntity::isInLava);
            register("is_inside_wall", PlayerEntity::isInsideWall);
            register("is_underwater", PlayerEntity::isSubmergedInWater);
        }

        private static void register(String name, PPCheckFunc checkFunc) {
            PP.put(name, checkFunc);
        }

        public static boolean has(String name) {
            return PP.containsKey(name);
        }

        public static boolean get(String name, PlayerEntity player) {
            return PP.get(name).check(player);
        }
    }

    interface PPCheckFunc {
        boolean check(PlayerEntity player);
    }
}
