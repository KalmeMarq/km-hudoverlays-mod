package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.HudOverlayContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.GameMode;

public class GameModeCondition implements IHudOverlayCondition {
    public GameMode gameMode;

    public GameModeCondition(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public boolean test(HudOverlayContext context) {
        PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(context.getPlayer().getGameProfile().getId());
        return playerListEntry != null && playerListEntry.getGameMode() == this.gameMode;
    }

    public static final class Serializer implements IHudOverlayConditionSerializer {
        @Override
        public IHudOverlayCondition fromJson(JsonObject obj) {
            String gm = JsonHelper.getString(obj, "name");
            return new GameModeCondition(GameMode.byName(gm, GameMode.CREATIVE));
        }
    } 
}
