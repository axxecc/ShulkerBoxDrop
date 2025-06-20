package axxcee.shulkerboxdrop.managers;

import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin;
import axxcee.shulkerboxdrop.models.PlayerCounter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CounterManager {
    private final HashMap<UUID, PlayerCounter> counters = new HashMap<>();

    public CounterManager(ShulkerBoxDropPlugin plugin) {
    }

    public PlayerCounter getPlayerCounter(Player player) {
        return counters.computeIfAbsent(
                player.getUniqueId(),
                k -> new PlayerCounter()
        );
    }

    public void resetPlayerCounter(UUID uuid) {
        PlayerCounter counter = counters.get(uuid);
        if (counter != null) {
            counter.reset();
        }
    }

    public void removePlayerCounter(UUID uuid) {
        counters.remove(uuid);
    }
}