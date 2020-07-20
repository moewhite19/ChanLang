package cn.whiteg.chanlang.allNms;

import cn.whiteg.chanlang.ChanLang;
import org.bukkit.Material;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Nms_Reflect implements Nms {

    final ChanLang plugin;
    private Method blockGetItem;
    private Method itemGetNameMethod;
    private Method getBlockMethod;
    private Method getItemMethod;

    public Nms_Reflect(ChanLang chanLang) {
        this.plugin = chanLang;
        try{
            Class craftMagicNumbersClass = Class.forName("org.bukkit.craftbukkit." + ChanLang.getServerVersion() + ".util.CraftMagicNumbers");
            getItemMethod = craftMagicNumbersClass.getMethod("getItem",Material.class);
            getBlockMethod = craftMagicNumbersClass.getMethod("getBlock",Material.class);

            itemGetNameMethod = ChanLang.getNmsClass("Item").getMethod("getName");
            blockGetItem = ChanLang.getNmsClass("Block").getMethod("getItem");
        }catch (ClassNotFoundException | NoSuchMethodException e){
            if (plugin.setting.debug) e.printStackTrace();
        }

    }

    //1.15或者更低获取的map
    public Map<String, String> getMap() {
        try{
            Class localeLanguageClass = ChanLang.getNmsClass("LocaleLanguage");
            Method getLocaleLanguage = localeLanguageClass.getMethod("a");
            getLocaleLanguage.setAccessible(true);
            Object ll = getLocaleLanguage.invoke(null);
            Field mapField = localeLanguageClass.getDeclaredField("d");
            mapField.setAccessible(true);
            return (Map<String, String>) mapField.get(ll);
        }catch (Exception e){
            if (plugin.setting.debug) e.printStackTrace();
            //如果失败了返回一个空map
            return new HashMap<>();
        }
    }

    /**
     * 获取NMS物品
     *
     * @param mat 物品id
     * @return 返回nms物品
     */
    public Object getNmsItem(Material mat) {
        try{
            Object item = getItemMethod.invoke(null,mat);
            if (item == null){
                Object block = getBlockMethod.invoke(null,mat);
                if (block != null){
                    return blockGetItem.invoke(block);
                }
            }
            return item;
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getItemName(Object item) {
        try{
            return (String) itemGetNameMethod.invoke(item);
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }


}
