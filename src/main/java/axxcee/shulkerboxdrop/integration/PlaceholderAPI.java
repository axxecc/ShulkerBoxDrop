package axxcee.shulkerboxdrop.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin;
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.CooldownManager;
import axxcee.shulkerboxdrop.managers.CounterManager;
import axxcee.shulkerboxdrop.models.PlayerCounter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.TimeUnit;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final ShulkerBoxDropPlugin plugin;

    public PlaceholderAPI(ShulkerBoxDropPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "shulkerboxdrop";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        ConfigManager config = plugin.getConfigManager();

        // 占位符处理
        switch (params.toLowerCase()) {
            case "blockdupe_enabled":
                return config.isBlockDupeEnabled() ? "启用" : "关闭";

            case "blockdupe_count":
                if (player == null) return "0";
                CounterManager counterManager = plugin.getCounterManager();
                PlayerCounter counter = counterManager.getPlayerCounter(player);
                return String.valueOf(counter.getCount());

            case "blockdupe_cycle":
                return String.valueOf(config.getBlockCycleLength());

            case "commanddupe_enabled":
                return config.isCommandDupeEnabled() ? "启用" : "关闭";

            case "commanddupe_cooldown":
                if (player == null) return "0";
                CooldownManager cooldownManager = plugin.getCooldownManager();
                long cooldown = cooldownManager.getRemainingCooldown(player);
                return String.valueOf(TimeUnit.MILLISECONDS.toSeconds(cooldown));

            case "world_disabled":
                if (player == null) return "启用";
                return isPlayerWorldDisabled(player) ? "禁用" : "启用";

            default:
                return null;
        }
    }

    private boolean isPlayerWorldDisabled(Player player) {
        ConfigManager config = plugin.getConfigManager();
        String worldName = player.getWorld().getName();
        return config.isWorldDisabled(worldName);
    }
}