package cn.whiteg.chanlang.allNms;

import org.bukkit.Material;

import java.util.Map;

public interface Nms {

    /**
     * 获取服务端语言Map
     *
     * @return 返回nms物品
     */
    Map<String, String> getMap();


    /**
     * 获取NMS物品
     *
     * @param mat 物品id
     * @return 返回nms物品
     */
    Object getNmsItem(Material mat);

    String getItemName(Object item);
}
