package axxcee.shulkerboxdrop;

import axxcee.shulkerboxdrop.commands.CommandDupe;
import axxcee.shulkerboxdrop.integration.PlaceholderAPI;
import axxcee.shulkerboxdrop.listeners.BlockListener;
import axxcee.shulkerboxdrop.commands.CommandListener;
import axxcee.shulkerboxdrop.commands.CommandTabCompleter;
import axxcee.shulkerboxdrop.listeners.PlayerListener;
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.CooldownManager;
import axxcee.shulkerboxdrop.managers.CounterManager;
import axxcee.shulkerboxdrop.managers.LangManager;
import org.bukkit.Material;

import java.util.Objects;
import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;

public class ShulkerBoxDropPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private LangManager langManager;
    private CounterManager counterManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        // 初始化管理器
        this.configManager = new ConfigManager(this);
        this.langManager = new LangManager(this);
        this.counterManager = new CounterManager(this);
        this.cooldownManager = new CooldownManager();

        // 加载配置
        configManager.loadConfig();
        langManager.loadMessages();

        // 打印配置状态
        getLogger().info("挖掘复制功能: " + (configManager.isBlockDupeEnabled() ? "启用" : "关闭"));
        getLogger().info("命令复制功能: " + (configManager.isCommandDupeEnabled() ? "启用" : "关闭"));

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // 注册命令
        Objects.requireNonNull(getCommand("shulkerboxdrop")).setExecutor(new CommandListener(this));
        Objects.requireNonNull(getCommand("shulkerboxdrop")).setTabCompleter(new CommandTabCompleter());
        Objects.requireNonNull(getCommand("dupe")).setExecutor(new CommandDupe(this));

        // 注册 PlaceholderAPI 扩展
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI(this).register();
            getLogger().info("已注册 PlaceholderAPI 扩展");
        }

        getLogger().info("插件已启用！");

    }

    @Override
    public void onDisable() {
        getLogger().info("插件已禁用！");
    }

    // Getter 方法
    public ConfigManager getConfigManager() { return configManager; }
    public LangManager getLangManager() { return langManager; }
    public CounterManager getCounterManager() { return counterManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }

    public Set<Material> getCopyableBlocks() {
        return configManager.getCopyableBlocks();
    }
}