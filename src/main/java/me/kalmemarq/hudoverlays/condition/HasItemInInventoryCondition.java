package me.kalmemarq.hudoverlays.condition;

import com.google.common.collect.ImmutableList;
import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class HasItemInInventoryCondition implements IHudOverlayCondition {
    public Item item;
    @Nullable
    public Integer count;

    public HasItemInInventoryCondition(Item item, @Nullable Integer count) {
        this.item = item;
        this.count = count;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        PlayerEntity player = context.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        List<List<ItemStack>> combinedInv = ImmutableList.of(context.getInventoryItems(), context.getHotbarItems(), context.getArmorItems(), playerInventory.offHand);
        Iterator<List<ItemStack>> var2 = combinedInv.iterator();

        ItemStack stack = new ItemStack(this.item);

        while(var2.hasNext()) {
            List<ItemStack> list = var2.next();

            for (ItemStack itemStack : list) {
                if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(stack)) {
                    return this.count == null || itemStack.getCount() == this.count;
                }
            }
        }

        return false;
    }
}
