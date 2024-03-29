package me.kalmemarq.hudoverlays;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Collections;

public class OverlayContext {
    private final PlayerEntity player;

    public OverlayContext(PlayerEntity player) {
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
        return player.getInventory().main.subList(0, 9);
    }

    public List<ItemStack> getInventoryItems() {
        return player.getInventory().main.subList(9, 36);
    }
}
