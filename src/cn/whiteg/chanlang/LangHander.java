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
import net.minecraft.util.Unit;
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
    static Method itemGetName;
    static Method getBlockName;
    private static Method getBlockMethod;
    private static Method getItemMethod;
    private static Optional<Unit> chatFormUnit;

    static {
        try{
//            CraftMagicNumbers
            Class<?> craftMagicNumbersClass = Class.forName("org.bukkit.craftbukkit." + ChanLang.getServerVersion() + ".util.CraftMagicNumbers");
            getItemMethod = craftMagicNumbersClass.getMethod("getItem",Material.class);
            getBlockMethod = craftMagicNumbersClass.getMethod("getBlock",Material.class);

            for (Method method : Item.class.getDeclaredMethods()) {
                if (method.getParameterTypes().length == 0 && method.getReturnType().equals(String.class) && !method.getName().startsWith("toString")){
                    method.setAccessible(true);
                    itemGetName = method;
                    break;
                }
            }
            for (Method method : Block.class.getDeclaredMethods()) {
                if (method.getParameterTypes().length == 0 && method.getReturnType().equals(String.class) && !method.getName().startsWith("toString")){
                    method.setAccessible(true);
                    getBlockName = method;
                    break;
                }
            }

            try{
                final Field field = NMSUtils.getFieldFormType(IChatFormatted.class,Optional.class);
                //noinspection unchecked
                chatFormUnit = (Optional<Unit>) field.get(null);
            }catch (NoSuchFieldException | IllegalAccessException e){
                throw new RuntimeException(e);
            }


        }catch (ClassNotFoundException | NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    private Map<String, String> map;

    public LangHander() {
    }

    /**
     * 获取NMS物品
     *
     * @param mat 物品id
     * @return 返回nms物品Key
     */
    public static String getMaterialKey(Material mat) {
        try{
            Block block = (Block) getBlockMethod.invoke(null,mat);
            if (block != null){
                return (String) getBlockName.invoke(block);
            }
            Item item = (Item) getItemMethod.invoke(null,mat);
            if (item != null){
                return (String) itemGetName.invoke(item);
            }
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
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

                Field f = NMSUtils.getFieldFormType(LocaleLanguage.class,LocaleLanguage.class);
                f.setAccessible(true);
                f.set(null,new LocaleLanguage() {
                    @Override
                    public String a(String key, String fallback) {
                        return map.getOrDefault(key, fallback);
                    }

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
                                return StringDecomposer.c(var2,var1x,var1) ? Optional.empty() : chatFormUnit;
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

}
