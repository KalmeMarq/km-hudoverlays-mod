package me.kalmemarq.hudoverlays.condition;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.JsonHelper;

import java.util.Map;

public class PlayerPropertiesCondition implements IHudOverlayCondition {
    private final JsonObject props;

    public PlayerPropertiesCondition(JsonObject props) {
        this.props = props;
    }

    @Override
    public boolean test(HudOverlayContext context) {
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

    public static final class Serializer implements IHudOverlayConditionSerializer {
        @Override
        public IHudOverlayCondition fromJson(JsonObject obj) {
            JsonObject props = new JsonObject();
            if (JsonHelper.hasJsonObject(obj, "properties")) {
                props = JsonHelper.getObject(obj, "properties");
            }
            return new PlayerPropertiesCondition(props);
        }
    }

    public static final class PlayerProperty {
        public static Map<String, PPCheckFunc> PP = Maps.newHashMap();

        public static final String IS_ON_FIRE = register("is_on_fire", PlayerEntity::isOnFire);
        public static final String IS_ON_GROUND = register("is_on_ground", PlayerEntity::isOnGround);
        public static final String IS_HOLDING_ONTO_LADDER = register("is_holding_onto_ladder", PlayerEntity::isHoldingOntoLadder);
        public static final String IS_USING_SPLYGLASS = register("is_using_splyglass", PlayerEntity::isUsingSpyglass);
        public static final String IS_FALL_FLYING = register("is_fall_flying", PlayerEntity::isFallFlying);
        public static final String IS_BLOCKING = register("is_blocking", PlayerEntity::isBlocking);
        public static final String IS_DEAD = register("is_dead", PlayerEntity::isDead);
        public static final String IS_ALIVE = register("is_alive", PlayerEntity::isAlive);
        public static final String IS_GLOWING = register("is_glowing", PlayerEntity::isGlowing);
        public static final String IS_SNEAKING = register("is_sneaking", PlayerEntity::isSneaking);
        public static final String IS_SPRINTING = register("is_sprinting", PlayerEntity::isSprinting);
        public static final String IS_IN_LAVA = register("is_in_lava", PlayerEntity::isInLava);
        public static final String IS_INSIDE_WALL = register("is_inside_wall", PlayerEntity::isInsideWall);
        public static final String IS_UNDERWATER = register("is_underwater", PlayerEntity::isSubmergedInWater);

        private static String register(String name, PPCheckFunc checkFunc) {
            PP.put(name, checkFunc);
            return name;
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
