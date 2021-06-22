package cn.whiteg.chanlang;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.locale.LocaleLanguage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatFormatted;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.FormattedString;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.bukkit.Material;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class LangHander {
    private Method getBlockMethod;
    private Method getItemMethod;
    private Map<String, String> map;

    public LangHander(ChanLang plugin) {
        try{
//            CraftMagicNumbers
            Class<?> craftMagicNumbersClass = Class.forName("org.bukkit.craftbukkit." + ChanLang.getServerVersion() + ".util.CraftMagicNumbers");
            getItemMethod = craftMagicNumbersClass.getMethod("getItem",Material.class);
            getBlockMethod = craftMagicNumbersClass.getMethod("getBlock",Material.class);
        }catch (ClassNotFoundException | NoSuchMethodException e){
            if (plugin.setting.debug) e.printStackTrace();
        }

    }

    //1.17获取map
    public Map<String, String> getMap() {
        if (map != null) return map;

        Map<String, String> builder = new HashMap<>(); //直接用builder的话，怕遇到重复key抛出异常x，还是直接用HashMap吧
        try{
            try (InputStream inputstream = LocaleLanguage.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
                JsonElement jsonelement = (new Gson()).fromJson(new InputStreamReader(inputstream,StandardCharsets.UTF_8),JsonElement.class);
                JsonObject jsonobject = ChatDeserializer.m(jsonelement,"strings");
                Iterator<Map.Entry<String, JsonElement>> iterator = jsonobject.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonElement> entry = iterator.next();
                    Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
                    String s = pattern.matcher(ChatDeserializer.a(entry.getValue(),entry.getKey())).replaceAll("%$1s");
                    builder.put(entry.getKey(),s);
                }

                Field f = LocaleLanguage.class.getDeclaredField("e");
                f.setAccessible(true);
                f.set(null,new LocaleLanguage() {
                    @Override
                    public String a(String s) {
                        return map.getOrDefault(s,s);
                    }

                    @Override
                    public boolean b(String s) {
                        return map.containsKey(s);
                    }

                    @Override
                    public boolean b() {
                        return false;
                    }

                    @Override
                    public FormattedString a(IChatFormatted iChatFormatted) {
                        return (var1) -> {
                            return iChatFormatted.a((var1x,var2) -> {
                                return StringDecomposer.c(var2,var1x,var1) ? Optional.empty() : IChatFormatted.b;
                            },ChatModifier.a).isPresent();
                        };
                    }
                });

            }catch (IOException e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        map = ImmutableMap.copyOf(builder);
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    /**
     * 获取NMS物品
     *
     * @param mat 物品id
     * @return 返回nms物品
     */
    public Item getNmsItem(Material mat) {
        try{
            Item item = (Item) getItemMethod.invoke(null,mat);
            if (item == null){
                Block block = (Block) getBlockMethod.invoke(null,mat);
                if (block != null){
                    return block.getItem();
                }
            }
            return item;
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

}
