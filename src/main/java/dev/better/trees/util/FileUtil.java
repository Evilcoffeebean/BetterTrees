package dev.better.trees.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class FileUtil {

    private final File file;
    private FileConfiguration config;

    public FileUtil(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        try {
            init();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    private void init() throws IOException {
        if (!file.exists()) {
            config.save(file);
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
    }

    public <T> void set(String path, T value) throws IOException {
        set(path, value, true);
    }

    public <T> void set(String path, T value, boolean overrideExisting) throws IOException {
        if (config != null && file.exists()) {
            if (config.get(path) != null) {
                if (overrideExisting) {
                    config.set(path, value);
                    config.save(file);
                }
                return;
            }
            config.set(path, value);
            config.save(file);
        }
    }

    public <T> T get(String path) {
        try {
            return (T) config.get(path);
        } catch (ClassCastException | NullPointerException e) {
            e.fillInStackTrace();
            return null;
        }
    }

    public String getString(String path) {
        try {
            return config.getString(path);
        } catch (ClassCastException | NullPointerException e) {
            e.fillInStackTrace();
            return "N/A";
        }
    }

    public double getDouble(String path) {
        try {
            return config.getDouble(path);
        } catch (ClassCastException | NullPointerException e) {
            e.fillInStackTrace();
            return 0.0;
        }
    }
}
