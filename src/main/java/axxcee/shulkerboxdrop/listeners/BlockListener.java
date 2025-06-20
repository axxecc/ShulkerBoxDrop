package axxcee.shulkerboxdrop.listeners;

import axxcee.shulkerboxdrop.ShulkerBoxDropPlugin;
import axxcee.shulkerboxdrop.managers.ConfigManager;
import axxcee.shulkerboxdrop.managers.CounterManager;
import axxcee.shulkerboxdrop.managers.LangManager;
import axxcee.shulkerboxdrop.models.PlayerCounter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class BlockListener implements Listener {
    private final ShulkerBoxDropPlugin plugin;

    public BlockListener(ShulkerBoxDropPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        ConfigManager config = plugin.getConfigManager();
        LangManager lang = plugin.getLangManager();
        Player player = event.getPlayer();

        // 检查挖掘复制功能是否启用
        if (!config.isBlockDupeEnabled()) {
            return;
        }

        Block block = event.getBlock();
        Material blockType = block.getType();

        // 检查事件是否被取消（领地插件或其他插件取消）
        if (event.isCancelled()) {
            if (config.shouldShowCancelledEventMessage()) {
                player.sendMessage(lang.getMessage("messages.cancelled-event"));
            }
            return;
        }

        // 检查是否在禁用世界中
        if (config.isWorldDisabled(player.getWorld().getName())) {
            plugin.getCounterManager().resetPlayerCounter(player.getUniqueId());

            if (config.shouldBlockShowDisabledWorldMessage() &&
                    config.isCopyableBlock(blockType)) {
                player.sendMessage(lang.getMessage("messages.disabled-world"));
            }
            return;
        }

        // 检查是否是可复制方块
        boolean isCopyable = config.isCopyableBlock(blockType);

        CounterManager counterManager = plugin.getCounterManager();
        PlayerCounter counter = counterManager.getPlayerCounter(player);

        // 破坏非可复制方块时重置计数器
        if (!isCopyable) {
            counter.reset();
            return;
        }

        // 检查方块破坏后是否会产生掉落物
        if (!willBlockDropItems(player, block)) {
            // 不会产生掉落物，不计入计数
            return;
        }

        // 增加计数器
        int count = counter.increment();
        int cycleLength = config.getBlockCycleLength();

        // 达到复制周期时额外掉落一个
        if (count == cycleLength) {
            // 获取原始潜影盒的掉落物（包含名称）
            ItemStack originalDrop = getOriginalShulkerBox(block);

            // 复制一个额外的潜影盒
            ItemStack extraBox = originalDrop.clone();
            extraBox.setAmount(1);

            // 复制潜影盒内容
            copyShulkerBoxContents(block, extraBox);

            // 掉落额外物品
            block.getWorld().dropItemNaturally(block.getLocation(), extraBox);

            counter.reset();

            // 发送消息通知

            player.sendMessage(lang.getMessage("messages.special-drop"));
        }
    }

    /**
     * 检查方块破坏后是否会产生掉落物
     *
     * @param player 挖掘方块的玩家
     * @param block 被破坏的方块
     * @return 如果会产生掉落物返回true，否则返回false
     */
    private boolean willBlockDropItems(Player player, Block block) {
        // 创造模式玩家总是获得掉落物
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        // 获取玩家手中的工具
        ItemStack tool = player.getInventory().getItemInMainHand();

        // 使用Bukkit API检查方块是否会掉落物品
        List<ItemStack> drops = (List<ItemStack>) block.getDrops(tool);
        if (drops.isEmpty()) {
            return false;
        }

        // 检查精准采集附魔
        // 精准采集附魔会掉落方块本身
        tool.containsEnchantment(Enchantment.SILK_TOUCH);

        return true;
    }

    // 获取原始潜影盒的掉落物（包含名称）
    private ItemStack getOriginalShulkerBox(Block block) {
        // 创建原始潜影盒物品
        ItemStack original = new ItemStack(block.getType(), 1);

        // 复制方块实体数据
        BlockState state = block.getState();
        if (state instanceof ShulkerBox shulkerBox) {

            // 获取物品元数据
            ItemMeta meta = original.getItemMeta();
            if (meta != null) {
                // 复制显示名称
                if (shulkerBox.getCustomName() != null) {
                    meta.setDisplayName(shulkerBox.getCustomName());
                }

                // 如果物品支持BlockStateMeta，复制内容
                if (meta instanceof BlockStateMeta bsm) {
                    BlockState blockStateCopy = bsm.getBlockState();

                    if (blockStateCopy instanceof ShulkerBox newBox) {
                        newBox.getInventory().setContents(shulkerBox.getInventory().getContents());
                        bsm.setBlockState(newBox);
                    }
                }
                original.setItemMeta(meta);
            }
        }
        return original;
    }

    // 复制潜影盒内容（不覆盖名称）
    private void copyShulkerBoxContents(Block block, ItemStack itemStack) {
        BlockState state = block.getState();
        if (!(state instanceof ShulkerBox shulkerBox)) return;

        ItemMeta meta = itemStack.getItemMeta();

        if (meta instanceof BlockStateMeta bsm) {
            BlockState blockStateCopy = bsm.getBlockState();

            if (blockStateCopy instanceof ShulkerBox newBox) {

                // 保存当前的名称
                String displayName = meta.getDisplayName();

                // 复制内容
                newBox.getInventory().setContents(shulkerBox.getInventory().getContents());
                bsm.setBlockState(newBox);

                // 恢复名称
                meta.setDisplayName(displayName);
                itemStack.setItemMeta(meta);
            }
        }
    }
}