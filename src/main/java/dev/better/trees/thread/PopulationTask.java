package dev.better.trees.thread;

import dev.better.trees.Core;
import dev.better.trees.data.TreeData;
import org.bukkit.Material;

import java.util.Map;

public class PopulationTask implements Runnable {

    @Override
    public void run() {
        if (Core.getCore().getCache().isEmpty())
            return;

        for(Map.Entry<TreeData, Long> entry : Core.getCore().getCache().entrySet()) {
            if(System.currentTimeMillis() >= entry.getValue()) {
                if (entry.getKey().getTargetBlock().getType() == Material.AIR) {
                    entry.getKey().getTargetBlock().getWorld().generateTree(entry.getKey().getTargetBlock().getLocation(), entry.getKey().getTreeType());
                    Core.getCore().getCache().remove(entry.getKey());
                }
            }
        }
    }
}
