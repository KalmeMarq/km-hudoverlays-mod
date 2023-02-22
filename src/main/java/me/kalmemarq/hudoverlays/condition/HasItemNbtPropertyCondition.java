package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.JsonHelper;

public class HasItemNbtPropertyCondition implements IOverlayCondition {
    public String name;

    public HasItemNbtPropertyCondition(String name) {
        this.name = name;
    }

    @Override
    public boolean test(OverlayContext context) {
        if (context.getPlayer() != null) {

            ItemStack stack =  context.getArmorItems().get(0);

            if (stack != ItemStack.EMPTY) {
                NbtCompound c = stack.getNbt();
                if (c != null) {
                    if (c.contains(this.name)) {
                        return true;
                    } else if (c.contains("tag")) {
                        return c.getCompound("tag").contains(this.name);
                    }
                }
            }
        }

        return false;
    }

    public static final class Serializer implements IOverlayConditionSerializer<HasItemNbtPropertyCondition> {
        @Override
        public HasItemNbtPropertyCondition fromJson(JsonObject obj) {
            String name = JsonHelper.getString(obj, "name");
            return new HasItemNbtPropertyCondition(name);
        }
    }
}
