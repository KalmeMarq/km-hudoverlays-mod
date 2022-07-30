package me.kalmemarq.hudoverlays;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Collections;

public class HudOverlayContext {
    private final PlayerEntity player;

    public HudOverlayContext(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public List<ItemStack> getArmorItems() {
        List<ItemStack> l = Lists.newArrayList(player.getArmorItems().iterator());
        Collections.reverse(l);
        return l;
    }

    public List<ItemStack> getHotbarItems() {
        List<ItemStack> l = player.getInventory().main.subList(0, 9);
        return l;
    }

    public List<ItemStack> getInventoryItems() {
        List<ItemStack> l = player.getInventory().main.subList(9, 36);
        return l;
    }
}
