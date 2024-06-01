package dev.better.trees.listener;

import dev.better.trees.Core;
import dev.better.trees.data.TreeData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class TreeBreakHandler implements Listener {

    private static final Random random = new Random();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        final TreeData treeData = new TreeData(e.getBlock());

        if (!treeData.checkConfig(e.getBlock().getLocation())) return;
        if (!treeData.isValid()) return;
        if (!treeData.hasAxe(e.getPlayer())) return;

        treeData.executeTimber();
        if (!Core.getCore().getCache().containsKey(treeData)) {
            Core.getCore().getCache().put(treeData, System.currentTimeMillis() + random.nextInt(1000 * 10));
        }

        if(shouldDrop()) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), elderWood(false));
        } else {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), elderWood(true));
        }

    }

    private ItemStack elderWood(boolean dead) {
        ItemStack item = new ItemStack(Material.OAK_WOOD, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !item.hasItemMeta())
            return null;

        meta.setDisplayName(dead ? ChatColor.DARK_GRAY + "Dead Elder Wood" : ChatColor.GREEN + "Charged Elder Wood");
        item.setItemMeta(meta);
        if (!dead)
            addGlow(item);
        return item;
    }

    private boolean shouldDrop() {
        return random.nextInt(100) <= 10;
    }

    private void addGlow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !stack.hasItemMeta())
            return;

        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
    }
}