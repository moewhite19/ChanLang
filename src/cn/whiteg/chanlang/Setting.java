package cn.whiteg.chanlang;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Setting {
    public static int VER = 2;
    public FileConfiguration config;
    public List<String> lang = Arrays.asList("zh_cn","en_us");
    public boolean debug = false;

    public void reload() {
        File file = new File(ChanLang.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);

        //更新配置
        if (config.getInt("ver") != VER){
            ChanLang.logger.info("更新配置文件");
            ChanLang.plugin.saveResource("config.yml",true);
            config.set("ver",VER);
            final FileConfiguration newcon = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = newcon.getKeys(true);
            for (String k : keys) {
                if (config.isSet(k)) continue;
                config.set(k,newcon.get(k));
                ChanLang.logger.info("在配置文件新增值: " + k);
            }
            try{
                config.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        debug = config.getBoolean("debug");
        lang = config.getStringList("lang-list");
    }
}
