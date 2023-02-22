package me.kalmemarq.hudoverlays.condition;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HasItemInInventoryCondition implements IOverlayCondition {
    public Item item;
    @Nullable
    public Integer count;

    public HasItemInInventoryCondition(Item item, @Nullable Integer count) {
        this.item = item;
        this.count = count;
    }

    @Override
    public boolean test(OverlayContext context) {
        PlayerEntity player = context.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        List<List<ItemStack>> combinedInv = ImmutableList.of(context.getInventoryItems(), context.getHotbarItems(), context.getArmorItems(), playerInventory.offHand);
        Iterator<List<ItemStack>> var2 = combinedInv.iterator();

        ItemStack stack = new ItemStack(this.item);

        while(var2.hasNext()) {
            List<ItemStack> list = var2.next();

            for (ItemStack itemStack : list) {
                if (!itemStack.isEmpty() && itemStack.isItemEqual(stack)) {
                    return this.count == null || itemStack.getCount() == this.count;
                }
            }
        }

        return false;
    }

    public static final class Serializer implements IOverlayConditionSerializer<HasItemInInventoryCondition> {
        @Override
        public HasItemInInventoryCondition fromJson(JsonObject obj) {
            String name = JsonHelper.getString(obj, "item");
            Item item = Registries.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            return new HasItemInInventoryCondition(item, count);
        }
    }
}
