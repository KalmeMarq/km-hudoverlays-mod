package me.kalmemarq.hudoverlays.condition;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HasItemCondition implements IHudOverlayCondition {
    public String slot;
    public Item item;
    @Nullable
    public Integer count;
    @Nullable
    public JsonObject nbt;
    public boolean equalNbt;

    public HasItemCondition(String slot, Item item, @Nullable Integer count, @Nullable JsonObject nbt, boolean equalNbt) {
        this.slot = slot;
        this.item = item;
        this.count = count;
        this.nbt = nbt;
        this.equalNbt = equalNbt;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        PlayerEntity player = context.getPlayer();
        List<ItemStack> armorItems = context.getArmorItems();

        if (this.slot.equals("armor.helmet")) {
            boolean has = armorItems.get(0).getItem() == this.item;
            if (this.count != null && has) has = armorItems.get(0).getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(armorItems.get(0));
            return has;
        } else if (this.slot.equals("armor.chestplate")) {
            boolean has = armorItems.get(1).getItem() == this.item;
            if (this.count != null && has) has = armorItems.get(1).getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(armorItems.get(1));
            return has;
        } else if (this.slot.equals("armor.leggings")) {
            boolean has = armorItems.get(2).getItem() == this.item;
            if (this.count != null && has) has = armorItems.get(2).getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(armorItems.get(2));
            return has;
        } else if (this.slot.equals("armor.boots")) {
            boolean has = armorItems.get(3).getItem() == this.item;
            if (this.count != null && has) has = armorItems.get(3).getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(armorItems.get(3));
            return has;
        } else if (this.slot.equals("offhand")) {
            boolean has = player.getOffHandStack().getItem() == this.item;
            if (this.count != null && has) has = player.getOffHandStack().getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(player.getOffHandStack());
            return has;
        } else if (this.slot.startsWith("hotbar.")) {
            int idx = Integer.parseInt(this.slot.substring(this.slot.lastIndexOf(".") + 1));

            if (idx >= 0 && idx < 9) {
                boolean has = context.getHotbarItems().get(idx).getItem() == this.item;
                if (this.count != null && has) {
                    has = context.getHotbarItems().get(idx).getCount() == this.count;
                }
                if (this.nbt != null && has) has = this.testNBT(context.getHotbarItems().get(idx));
                return has;
            }
        } else if (this.slot.startsWith("inventory.")) {
            int idx = Integer.parseInt(this.slot.substring(this.slot.lastIndexOf(".") + 1));

            if (idx >= 0 && idx < 27) {
                boolean has = context.getInventoryItems().get(idx).getItem() == this.item;
                if (this.count != null && has) {
                    has = context.getInventoryItems().get(idx).getCount() == this.count;
                }
                if (this.nbt != null && has) has = this.testNBT(context.getInventoryItems().get(idx));
                return has;
            }
        } else if (this.slot.equals("mainHand")) {
            boolean has = player.getActiveItem().getItem() == this.item;
            if (this.count != null && has) has = player.getActiveItem().getCount() == this.count;
            if (this.nbt != null && has) has = this.testNBT(player.getActiveItem());
            return has;
        }

        return false;
    }

    private boolean testNBT(ItemStack stack) {
        if (this.nbt == null) return true;

        NbtCompound nbt = stack.getNbt();

        if (nbt != null) {
            for (Map.Entry<String, JsonElement> e : this.nbt.entrySet()) {
                String key = e.getKey();
                JsonElement value = e.getValue();

                boolean checkK = nbt.contains(key);

                if (checkK) {
                    checkK = nbt.get(key).asString().equals(value.getAsString());
                }

                return checkK;
            }
        }

        return true;
    }

    private static boolean weakMatchNBT(@Nullable NbtElement standard, @Nullable NbtElement subject) {
        if (standard == subject) {
            return true;
        } else if (standard == null) {
            return true;
        } else if (subject == null) {
            return false;
        } else if (!standard.getClass().equals(subject.getClass())) {
            return false;
        } else if (standard instanceof NbtCompound standComp) {
            NbtCompound subjComp = (NbtCompound) subject;

            Iterator<String> iter = subjComp.getKeys().iterator();

            String key;
            do {
                if (!iter.hasNext()) {
                    return true;
                }

                key = iter.next();
            } while(weakMatchNBT(standComp.get(key), subjComp.get(key)));

            return false;
        } else if (standard instanceof NbtList) {
            return true;
        } else {
            return standard.equals(subject);
        }
    }

    public static final class Serializer implements IHudOverlayConditionSerializer<HasItemCondition> {
        @Override
        public HasItemCondition fromJson(JsonObject obj) {
            String slot = JsonHelper.getString(obj, "slot");
            String name = JsonHelper.getString(obj, "item");
            Item item = Registries.ITEM.get(new Identifier(name));
            Integer count = null;
            if (JsonHelper.hasNumber(obj, "count")) {
                count = JsonHelper.getInt(obj, "count");
            }
            JsonObject nbt = JsonHelper.getObject(obj, "nbt", null);
            boolean equalNBT = JsonHelper.getString(obj, "match_nbt", "equal").equals("equal");
            return new HasItemCondition(slot, item, count, nbt, equalNBT);
        }
    }
}
