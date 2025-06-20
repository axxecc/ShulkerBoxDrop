package axxcee.shulkerboxdrop.managers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {
    private final JavaPlugin plugin;

    // 添加临时状态字段
    private boolean blockDupeTemporaryEnabled = true;
    private boolean commandDupeTemporaryEnabled = true;

    // 全局配置
    private Set<String> disabledWorlds;
    private boolean showCancelledEventMessage;

    // 挖掘复制配置
    private boolean blockDupeEnabled;
    private int blockCycleLength;
    private boolean blockResetOnWorldChange;
    private boolean blockResetOnLogout;
    private boolean blockShowDisabledWorldMessage;
    private final Set<Material> copyableBlocks = new HashSet<>();

    // 命令复制配置
    private boolean commandDupeEnabled;
    private int commandCooldown;
    private boolean commandShowDisabledWorldMessage;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        // 加载全局配置
        ConfigurationSection global = config.getConfigurationSection("global");
        if (global != null) {
            List<String> worlds = global.getStringList("disabled-worlds");
            disabledWorlds = worlds.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            showCancelledEventMessage = global.getBoolean("show-cancelled-event-message", true);
        }

        // 加载挖掘复制配置
        ConfigurationSection blockDupe = config.getConfigurationSection("block-dupe");
        if (blockDupe != null) {
            blockDupeEnabled = blockDupe.getBoolean("enabled", true);
            blockCycleLength = blockDupe.getInt("cycle-length", 10);
            blockResetOnWorldChange = blockDupe.getBoolean("reset-on-world-change", true);
            blockResetOnLogout = blockDupe.getBoolean("reset-on-logout", true);
            blockShowDisabledWorldMessage = blockDupe.getBoolean("show-disabled-world-message", true);

            // 加载可复制方块列表
            copyableBlocks.clear();
            List<String> blockNames = blockDupe.getStringList("copyable-blocks");
            for (String blockName : blockNames) {
                try {
                    Material material = Material.valueOf(blockName.toUpperCase());
                    copyableBlocks.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("无效的方块类型: " + blockName);
                }
            }

            // 如果没有配置任何方块，添加默认的潜影盒
            if (copyableBlocks.isEmpty()) {
                plugin.getLogger().info("未配置可复制方块，添加默认潜影盒");
                addDefaultShulkerBoxes();
            }
        }

        // 加载命令复制配置
        ConfigurationSection commandDupe = config.getConfigurationSection("command-dupe");
        if (commandDupe != null) {
            commandDupeEnabled = commandDupe.getBoolean("enabled", true);
            commandCooldown = commandDupe.getInt("cooldown", 1000);
            commandShowDisabledWorldMessage = commandDupe.getBoolean("show-disabled-world-message", true);
        }
    }

    private void addDefaultShulkerBoxes() {
        for (Material material : Material.values()) {
            if (material.name().endsWith("_SHULKER_BOX")) {
                copyableBlocks.add(material);
            }
        }
    }


    // ====================
    // 全局配置获取方法
    // ====================
    public boolean isWorldDisabled(String worldName) {
        return disabledWorlds.contains(worldName.toLowerCase());
    }

    public boolean shouldShowCancelledEventMessage() {
        return showCancelledEventMessage;
    }

    // ====================
    // 挖掘复制配置获取方法
    // ====================

    public int getBlockCycleLength() {
        return blockCycleLength;
    }

    public boolean shouldBlockResetOnWorldChange() {
        return blockResetOnWorldChange;
    }

    public boolean shouldBlockResetOnLogout() {
        return blockResetOnLogout;
    }

    public boolean shouldBlockShowDisabledWorldMessage() {
        return blockShowDisabledWorldMessage;
    }

    public boolean isCopyableBlock(Material material) {
        return copyableBlocks.contains(material);
    }

    public Set<Material> getCopyableBlocks() {
        return copyableBlocks;
    }

    // ====================
    // 命令复制配置获取方法
    // ====================

    public int getCommandCooldown() {
        return commandCooldown;
    }

    public boolean shouldCommandShowDisabledWorldMessage() {
        return commandShowDisabledWorldMessage;
    }
    public void toggleBlockDupeTemporary() {
        blockDupeTemporaryEnabled = !blockDupeTemporaryEnabled;
    }

    public void toggleCommandDupeTemporary() {
        commandDupeTemporaryEnabled = !commandDupeTemporaryEnabled;
    }

    public boolean isBlockDupeTemporaryEnabled() {
        return blockDupeTemporaryEnabled;
    }

    public boolean isCommandDupeTemporaryEnabled() {
        return commandDupeTemporaryEnabled;
    }

    // 更新状态检查方法
    public boolean isBlockDupeEnabled() {
        return blockDupeEnabled && blockDupeTemporaryEnabled;
    }

    public boolean isCommandDupeEnabled() {
        return commandDupeEnabled && commandDupeTemporaryEnabled;
    }

    // 在重载时重置临时状态
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
        // 重置临时状态
        blockDupeTemporaryEnabled = true;
        commandDupeTemporaryEnabled = true;
    }
}
