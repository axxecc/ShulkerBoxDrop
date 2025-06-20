package axxcee.shulkerboxdrop.listeners;

import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin; // 修正类名
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.CounterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final ShulkerBoxDropPlugin plugin;
    
    public PlayerListener(ShulkerBoxDropPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ConfigManager config = plugin.getConfigManager();
        
        // 检查是否在禁用世界中（退出时只在非禁用世界重置）
        if (!config.isWorldDisabled(player.getWorld().getName()) && config.shouldBlockResetOnLogout()) {
            plugin.getCounterManager().resetPlayerCounter(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        ConfigManager config = plugin.getConfigManager();
        CounterManager counterManager = plugin.getCounterManager();
        
        // 切换到禁用世界时立即重置计数器
        if (config.isWorldDisabled(player.getWorld().getName())) {
            counterManager.resetPlayerCounter(player.getUniqueId());
            return;
        }
        
        // 非禁用世界切换时根据配置重置
        if (config.shouldBlockResetOnWorldChange()) {
            counterManager.resetPlayerCounter(player.getUniqueId());
        }
    }
}