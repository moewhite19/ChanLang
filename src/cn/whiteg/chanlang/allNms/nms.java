package cn.whiteg.chanlang.allNms;

import cn.whiteg.chanlang.ChanLang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class nms {

    final ChanLang plugin;

    public nms(ChanLang chanLang) {
        this.plugin = chanLang;
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
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
