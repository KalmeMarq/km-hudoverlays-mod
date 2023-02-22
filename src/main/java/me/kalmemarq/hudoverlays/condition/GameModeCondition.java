package me.kalmemarq.hudoverlays.condition;

import com.google.gson.JsonObject;

import me.kalmemarq.hudoverlays.OverlayContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.GameMode;

public class GameModeCondition implements IOverlayCondition {
    public GameMode gameMode;

    public GameModeCondition(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public boolean test(OverlayContext context) {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();

        PlayerListEntry playerListEntry = null;
        if (networkHandler != null) {
            playerListEntry = networkHandler.getPlayerListEntry(context.getPlayer().getGameProfile().getId());
        }

        return playerListEntry != null && playerListEntry.getGameMode() == this.gameMode;
    }

    public static final class Serializer implements IOverlayConditionSerializer<GameModeCondition> {
        @Override
        public GameModeCondition fromJson(JsonObject obj) {
            String gm = JsonHelper.getString(obj, "name", "creative");
            return new GameModeCondition(GameMode.byName(gm, GameMode.CREATIVE));
        }
    } 
}
