package dev.better.trees;

import com.google.common.collect.Maps;
import dev.better.trees.command.AddTreeLocation;
import dev.better.trees.data.TreeData;
import dev.better.trees.listener.TreeBreakHandler;
import dev.better.trees.thread.PopulationTask;
import dev.better.trees.util.LocationStorage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

@Getter
public final class Core extends JavaPlugin {

    @Getter
    private static Core core;
    private final Map<TreeData, Long> cache = Maps.newConcurrentMap();
    private LocationStorage treeConfig;


    @Override
    public void onEnable() {
        core = this;
        getServer().getPluginManager().registerEvents(new TreeBreakHandler(), this);

        getCommand("addtreelocation").setExecutor(new AddTreeLocation());

        treeConfig = new LocationStorage(this);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PopulationTask(), 0, 20*60*15); //15 minutes
    }

    @Override
    public void onDisable() {
        cache.clear();
    }
}
