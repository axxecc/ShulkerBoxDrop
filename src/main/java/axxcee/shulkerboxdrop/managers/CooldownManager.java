package axxcee.shulkerboxdrop.managers;

import org.bukkit.entity.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private boolean cleanupTaskRunning = false;

    public CooldownManager() {
    }

    public void setCooldown(Player player, int seconds) {
        if (seconds <= 0) {
            clearCooldown(player);
            return;
        }

        long cooldownTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(player.getUniqueId(), cooldownTime);

        // 启动清理任务（如果需要）
        startCleanupTaskIfNeeded();
    }

    public long getRemainingCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        Long cooldownEnd = cooldowns.get(uuid);

        if (cooldownEnd == null) {
            return 0;
        }

        long remaining = cooldownEnd - System.currentTimeMillis();

        // 自动移除过期冷却
        if (remaining <= 0) {
            cooldowns.remove(uuid);
            return 0;
        }

        return remaining;
    }

    public void clearCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    private synchronized void startCleanupTaskIfNeeded() {
        if (cleanupTaskRunning || cooldowns.isEmpty()) {
            return;
        }

        cleanupTaskRunning = true;
        new Thread(this::cleanupExpiredCooldowns).start();
    }

    private void cleanupExpiredCooldowns() {
        try {
            long now = System.currentTimeMillis();

            cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
        } finally {
            cleanupTaskRunning = false;

            // 如果还有条目，重新启动清理任务
            if (!cooldowns.isEmpty()) {
                startCleanupTaskIfNeeded();
            }
        }
    }
}