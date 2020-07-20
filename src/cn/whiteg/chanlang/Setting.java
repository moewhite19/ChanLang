package cn.whiteg.chanlang;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Setting {
    public FileConfiguration config;
    public String land = "zh_cn";
    public boolean debug = false;

    public void reload() {
        File file = new File(ChanLang.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        land = config.getString("land",land);
        debug = config.getBoolean("debug");
    }
}
