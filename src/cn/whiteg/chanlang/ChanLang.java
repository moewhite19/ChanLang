package cn.whiteg.chanlang;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.v1_16_R1.ChatDeserializer;
import net.minecraft.server.v1_16_R1.LocaleLanguage;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class ChanLang extends JavaPlugin {
    private static final String serverVersion;
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    public static Logger logger;
    public static ChanLang plugin;
    private static Map<String, String> map;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public CommandManage mainCommand;
    public Setting setting;

    public ChanLang() {
        plugin = this;
    }

    public static Class getNmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + serverVersion + "." + name);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getLangMap() {
        return map;
    }

    public static String getServerVersion() {
        return serverVersion;
    }

    public void onLoad() {
        saveDefaultConfig();
        logger = getLogger();
        setting = new Setting();
//        saveResource("lands",false);
//        try{
//            Enumeration<URL> res = getClass().getClassLoader().getResources("langs");
//            while (res.hasMoreElements()) {
//                logger.info("资源" + res.nextElement());
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public void onEnable() {
        logger.info("开始加载插件");
        PluginCommand pc = getCommand("chanlang");
        if (pc != null){
            mainCommand = new CommandManage();
            pc.setExecutor(mainCommand);
            pc.setTabCompleter(mainCommand);
        } else {
            logger.info("没用注册指令(忘记添加指令到plugin.yml啦?)");
        }
        Class localeLanguageClass;
        try{
            localeLanguageClass = getNmsClass("LocaleLanguage");
            Method getLocaleLanguage = localeLanguageClass.getMethod("a");
            getLocaleLanguage.setAccessible(true);
            Object ll = getLocaleLanguage.invoke(null);
            logger.info("ServerVersion: " + serverVersion);
            if ("v1_16_R1".equals(serverVersion)){
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
            } else {
                //1.15或者更低获取的map
                Field mapField = localeLanguageClass.getDeclaredField("d");
                mapField.setAccessible(true);
                map = (Map<String, String>) mapField.get(ll);
            }
        }catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e){
            e.printStackTrace();
            logger.warning("not support version " + serverVersion);
            map = Maps.newHashMap();
            return;
        }
        File landDir = new File(getDataFolder(),"langs");
        if (!landDir.isDirectory()){
            landDir.mkdirs();
            String[] langs = new String[]{"en_us","zh_cn"};
            for (String lang : langs) {
                try{
                    String name = lang + ".json";
                    logger.info("Save Lang: " + name);
                    URL url = getClass().getClassLoader().getResource("langs/" + name);
                    if (url == null) continue;
                    InputStream is = url.openStream();
                    File file = new File(landDir,name);
                    if (!file.createNewFile()){
                        continue;
                    }
                    OutputStream os = new FileOutputStream(file);
                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = is.read(buffer,0,buffer.length)) != -1) {
                        os.write(buffer,0,bytesRead);
                    }
                    os.close();
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        File landFile = new File(landDir,setting.land + ".json");
        if (landFile.exists()){
            try{
                setLand(new FileInputStream(landFile));
                logger.info("已设置语言为" + setting.land);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        } else {
            logger.warning("语言文件不存在" + landFile.toString());
        }
        logger.info("全部加载完成");
    }

    public void onDisable() {
        //注销所有监听器
        logger.info("插件已关闭");
    }

    public void onReload() {
        logger.info("--开始重载--");
        setting.reload();
        onEnable();
        logger.info("--重载完成--");
    }

    @SuppressWarnings("unchecked")
    public void setLand(InputStream inputstream) {
        String nmsPack = "net.minecraft.server." + serverVersion;
        Map<String, String> map;
        Method ccm;
        Method cca;
        try{
//            Class<? extends ChatDeserializer> chatDeserializerClass = ChatDeserializer.class;
            map = getLangMap();
            Class chatDeserializerClass = Class.forName(nmsPack + ".ChatDeserializer");
            ccm = chatDeserializerClass.getMethod("m",JsonElement.class,String.class);
            cca = chatDeserializerClass.getMethod("a",JsonElement.class,String.class);
        }catch (ClassNotFoundException | NoSuchMethodException e){
            e.printStackTrace();
            logger.warning("not support version " + serverVersion);
            return;
        }
        try{
            Throwable throwable = null;
            try{
                Pattern b = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
                JsonElement jsonelement = new Gson().fromJson(new InputStreamReader(inputstream,StandardCharsets.UTF_8),JsonElement.class);
                JsonObject jsonobject = (JsonObject) ccm.invoke(null,jsonelement,"strings");
                Iterator<Map.Entry<String, JsonElement>> iterator = jsonobject.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonElement> entry = iterator.next();
                    String s = b.matcher((CharSequence) cca.invoke(null,entry.getValue(),entry.getKey())).replaceAll("%$1s");
                    map.put(entry.getKey(),s);
                }
            }catch (Throwable throwable1){
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (inputstream != null){
                    if (throwable != null){
                        try{
                            inputstream.close();
                        }catch (Throwable throwable2){
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        inputstream.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
