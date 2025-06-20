package axxcee.shulkerboxdrop.commands;

import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin;
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.LangManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements CommandExecutor {
    private final ShulkerBoxDropPlugin plugin;

    public CommandListener(ShulkerBoxDropPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        LangManager lang = plugin.getLangManager();

        if (args.length == 0) {
            // 显示帮助信息
            return showHelp(sender);
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "reload" -> handleReload(sender, lang);
            case "list" -> handleList(sender, lang);
            case "toggle" -> {
                if (args.length < 2) {
                    sender.sendMessage(lang.getMessage("messages.toggle-usage"));
                    yield true;
                }
                yield handleToggle(sender, args[1].toLowerCase(), lang);
            }
            case "status" -> handleStatus(sender);
            default -> {
                sender.sendMessage(lang.getMessage("messages.unknown-command"));
                yield showHelp(sender);
            }
        };
    }

    private boolean showHelp(CommandSender sender) {
        sender.sendMessage("§6===== §eShulkerBoxDrop 帮助 §6=====");
        sender.sendMessage("§a/sbd reload §7- 重载插件配置");
        sender.sendMessage("§a/sbd list §7- 显示可复制方块列表");
        sender.sendMessage("§a/sbd toggle block §7- 切换挖掘复制状态");
        sender.sendMessage("§a/sbd toggle command §7- 切换命令复制状态");
        sender.sendMessage("§a/sbd status §7- 显示插件状态");
        sender.sendMessage("§a/dupe §7- 复制手中的物品");
        return true;
    }

    private boolean handleReload(CommandSender sender, LangManager lang) {
        if (!sender.hasPermission("shulkerboxdrop.reload")) {
            sender.sendMessage(lang.getMessage("messages.no-permission"));
            return true;
        }

        plugin.getConfigManager().reloadConfig();
        plugin.getLangManager().loadMessages();
        sender.sendMessage(lang.getMessage("messages.config-reloaded"));
        return true;
    }

    private boolean handleList(CommandSender sender, LangManager lang) {
        if (!sender.hasPermission("shulkerboxdrop.list")) {
            sender.sendMessage(lang.getMessage("messages.no-permission"));
            return true;
        }

        sender.sendMessage("§a可复制方块列表:");
        for (Material material : plugin.getCopyableBlocks()) {
            sender.sendMessage("§7- §f" + material.name());
        }
        return true;
    }

    private boolean handleToggle(CommandSender sender, String module, LangManager lang) {
        if (!(sender instanceof Player) && !sender.hasPermission("shulkerboxdrop.toggle")) {
            sender.sendMessage(lang.getMessage("messages.player-only"));
            return true;
        }

        if (!sender.hasPermission("shulkerboxdrop.toggle")) {
            sender.sendMessage(lang.getMessage("messages.no-permission"));
            return true;
        }

        ConfigManager config = plugin.getConfigManager();
        String status;

        switch (module) {
            case "block":
                config.toggleBlockDupeTemporary();
                status = config.isBlockDupeTemporaryEnabled() ?
                        lang.getMessage("messages.enabled") :
                        lang.getMessage("messages.disabled");
                sender.sendMessage(lang.getMessage("messages.block-toggled")
                        .replace("%status%", status));
                return true;

            case "command":
                config.toggleCommandDupeTemporary();
                status = config.isCommandDupeTemporaryEnabled() ?
                        lang.getMessage("messages.enabled") :
                        lang.getMessage("messages.disabled");
                sender.sendMessage(lang.getMessage("messages.command-toggled")
                        .replace("%status%", status));
                return true;

            default:
                sender.sendMessage(lang.getMessage("messages.invalid-module"));
                return true;
        }
    }

    private boolean handleStatus(CommandSender sender) {
        ConfigManager config = plugin.getConfigManager();

        sender.sendMessage("§6===== §e插件状态 §6=====");
        sender.sendMessage("§a挖掘复制: §f" + (config.isBlockDupeEnabled() ? "§a启用" : "§c禁用"));
        sender.sendMessage("§a命令复制: §f" + (config.isCommandDupeEnabled() ? "§a启用" : "§c禁用"));
        return true;
    }
}