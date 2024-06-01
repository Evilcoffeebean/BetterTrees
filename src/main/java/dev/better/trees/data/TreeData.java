package dev.better.trees.data;

import dev.better.trees.Core;
import dev.better.trees.particle.TimberEffect;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class TreeData {

    private final Block targetBlock;
    private final TreeType treeType;
    private final Random r = new Random();
    private static int threshold = 0;

    private final Material[] AXES = {
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    };

    private final Material[] LOGS = {
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG
    };

    private final TreeType[] TYPES = {
            TreeType.ACACIA,
            TreeType.BIRCH,
            TreeType.DARK_OAK,
            TreeType.JUNGLE,
            TreeType.TREE
    };

    public TreeData(Block block) {
        this(block, null);
    }

    public TreeData(Block block, TreeType type) {
        this.targetBlock = block;
        this.treeType = type != null ? type : TreeType.TREE;
    }

    public boolean checkConfig(Location provided) {
        if (!Core.getCore().getTreeConfig().hasData())
            return false;

        for (Location loc : Core.getCore().getTreeConfig().getSavedLocations()) {
            if (provided.equals(loc))
                return true;
        }
        return false;
    }

    public boolean isValid() {
        for (TreeType type : TYPES) {
            if (isLog(targetBlock) && treeType == type)
                return true;
        }
        return false;
    }

    public boolean hasAxe(Player player) {
        for (Material axe : AXES) {
            if (player.getInventory().getItemInMainHand().getType() == axe)
                return true;
        }
        return false;
    }

    private boolean isLog(Block given) {
        for (Material log : LOGS) {
            if (given.getType() == log)
                return true;
        }
        return false;
    }

    private void getBlockData(Block startBlock) {
        threshold++;
        List<Block> depthSearch = new ArrayList<>();
        for (int i = 0; i < BlockFace.values().length; i++) {
            Block found = startBlock.getRelative(BlockFace.values()[i]);
            if (found.getType() != Material.OAK_LEAVES) {
                continue;
            }
            depthSearch.add(found);
            found.setType(Material.AIR);

            for (Block depthBlock : depthSearch) {
                if (threshold <= 500) {
                    getBlockData(depthBlock);
                    depthBlock.setType(Material.AIR);
                    depthBlock.getWorld().playSound(depthBlock.getLocation(), Sound.BLOCK_GRASS_BREAK, 1f, 1f);
                }
            }

            depthSearch.clear();
            threshold = 0;
        }
    }

    private void initDrop(Block given) {
        given.setType(Material.AIR);
        given.getDrops().clear();
        given.getWorld().dropItemNaturally(given.getLocation(), shouldDrop() ? elderWood(false) : elderWood(true));
        new TimberEffect(Particle.HAPPY_VILLAGER).display(given.getLocation());
        given.getWorld().playSound(given.getLocation(), Sound.BLOCK_WOOD_BREAK, 1f, 1f);
    }

    public void executeTimber() {
        initDrop(targetBlock);

        new BukkitRunnable() {
            double counter = 0;
            @Override
            public void run() {
                counter++;
                Block current = targetBlock.getLocation().add(0, counter, 0).getBlock();
                getBlockData(current);

                for (BlockFace face : BlockFace.values()) {
                    if (isLog(current.getRelative(face)))
                        initDrop(current.getRelative(face));
                }

                if (counter >= 25) {
                    cancel();
                    counter = 0;
                }
            }
        }.runTaskTimer(Core.getCore(), 0L, 2L);
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
        return r.nextInt(100) <= 10;
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
