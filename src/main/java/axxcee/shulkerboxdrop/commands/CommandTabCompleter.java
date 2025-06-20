package axxcee.shulkerboxdrop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {
    private static final List<String> MAIN_COMMANDS = Arrays.asList("reload", "list", "toggle", "status", "help");
    private static final List<String> TOGGLE_MODULES = Arrays.asList("block", "command");

    public CommandTabCompleter() {
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        // 第一个参数补全
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], MAIN_COMMANDS, completions);
        }
        // 第二个参数补全 (toggle 子命令)
        else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            StringUtil.copyPartialMatches(args[1], TOGGLE_MODULES, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}