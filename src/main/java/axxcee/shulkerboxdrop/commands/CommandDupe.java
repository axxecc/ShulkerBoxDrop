package axxcee.shulkerboxdrop.commands;

import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin;
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.LangManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class CommandDupe implements CommandExecutor {
    private final ShulkerBoxDropPlugin plugin;

    public CommandDupe(ShulkerBoxDropPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // 检查发送者是否是玩家
        if (!(sender instanceof Player player)) {
            LangManager lang = plugin.getLangManager();
            sender.sendMessage(lang.getMessage("messages.player-only"));
            return true;
        }

        LangManager lang = plugin.getLangManager();
        ConfigManager config = plugin.getConfigManager();

        // 检查命令复制功能是否启用
        if (!config.isCommandDupeEnabled()) {
            player.sendMessage(lang.getMessage("messages.command-dupe-disabled"));
            return true;
        }

        // 检查是否在禁用世界中
        if (config.isWorldDisabled(player.getWorld().getName())) {
            if (config.shouldCommandShowDisabledWorldMessage()) {
                player.sendMessage(lang.getMessage("messages.disabled-world"));
            }
            return true;
        }

        // 检查权限
        if (!player.hasPermission("shulkerboxdrop.dupe")) {
            player.sendMessage(lang.getMessage("messages.no-permission"));
            return true;
        }

        // 检查冷却时间
        long cooldown = plugin.getCooldownManager().getRemainingCooldown(player);
        if (cooldown > 0) {
            long seconds = TimeUnit.MILLISECONDS.toSeconds(cooldown);
            player.sendMessage(lang.getMessage("messages.dupe-cooldown")
                    .replace("%seconds%", String.valueOf(seconds)));
            return true;
        }

        // 获取玩家手中的物品
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // 检查手中是否有物品
        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(lang.getMessage("messages.dupe-no-item"));
            return true;
        }

        // 检查背包空间
        PlayerInventory inventory = player.getInventory();
        if (inventory.firstEmpty() == -1) {
            player.sendMessage(lang.getMessage("messages.dupe-inventory-full"));
            return true;
        }

        // 复制物品
        ItemStack clonedItem = itemInHand.clone();
        inventory.addItem(clonedItem);

        // 设置冷却时间
        plugin.getCooldownManager().setCooldown(player, config.getCommandCooldown());

        // 发送成功消息
        long remaining = plugin.getCooldownManager().getRemainingCooldown(player);
        long secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(remaining);

        // 发送成功消息，包含当前冷却时间
        player.sendMessage(lang.getMessage("messages.dupe-success")
                .replace("%cooldown%", String.valueOf(secondsRemaining)));
        return true;
    }
}