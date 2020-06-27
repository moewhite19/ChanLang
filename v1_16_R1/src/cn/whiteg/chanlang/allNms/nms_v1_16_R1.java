package cn.whiteg.chanlang.allNms;

import cn.whiteg.chanlang.ChanLang;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_16_R1.ChatDeserializer;
import net.minecraft.server.v1_16_R1.LocaleLanguage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class nms_v1_16_R1 extends nms {


    private Map<String, String> map;
    public nms_v1_16_R1(ChanLang chanLang) {
        super(chanLang);
    }

    //1.16获取map
    public Map<String, String> getMap() {
        if (map != null) return map;
        try{
            try{
                map = Maps.newHashMap();
                InputStream inputstream = LocaleLanguage.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
                Throwable throwable = null;
                try{
                    JsonElement jsonelement = (new Gson()).fromJson(new InputStreamReader(inputstream,StandardCharsets.UTF_8),JsonElement.class);
                    JsonObject jsonobject = ChatDeserializer.m(jsonelement,"strings");
                    Iterator iterator = jsonobject.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonElement> entry = (Map.Entry) iterator.next();
                        Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
                        String s = pattern.matcher(ChatDeserializer.a((JsonElement) entry.getValue(),(String) entry.getKey())).replaceAll("%$1s");
                        map.put(entry.getKey(),s);
                    }
                }catch (Throwable var16){
                    throwable = var16;
                    throw var16;
                } finally {
                    if (inputstream != null){
                        if (throwable != null){
                            try{
                                inputstream.close();
                            }catch (Throwable var15){
                                throwable.addSuppressed(var15);
                            }
                        } else {
                            inputstream.close();
                        }
                    }
                }

                Class<?> localeLanguageClass = ChanLang.getNmsClass("LocaleLanguage");
                Field f = localeLanguageClass.getDeclaredField("d");
                f.setAccessible(true);
                f.set(null,new LocaleLanguage() {
                    @Override
                    public String a(String s) {
                        return map.get(s);
                    }

                    @Override
                    public boolean b(String s) {
                        return map.containsKey(s);
                    }

                    @Override
                    public String a(String s,boolean b) {
                        return s;
                    }
                });

            }catch (IOException e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
