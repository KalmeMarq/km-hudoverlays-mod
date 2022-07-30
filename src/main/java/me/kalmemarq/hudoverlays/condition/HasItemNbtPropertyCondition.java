package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.JsonHelper;

public class HasItemNbtPropertyCondition implements IHudOverlayCondition {
    public String name;

    public HasItemNbtPropertyCondition(String name) {
        this.name = name;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        if (context.getPlayer() != null) {

            ItemStack stack =  context.getArmorItems().get(0);

            if (stack != ItemStack.EMPTY) {
                NbtCompound c = stack.getNbt();
                if (c != null) {
                    if (c.contains(this.name)) {
                        return true;
                    } else if (c.contains("tag")) {
                        if (c.getCompound("tag").contains(this.name)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static final class Serializer implements IHudOverlayConditionSerializer {
        @Override
        public IHudOverlayCondition fromJson(JsonObject obj) {
            String name = JsonHelper.getString(obj, "name");
            return new HasItemNbtPropertyCondition(name);
        }
    }
}
