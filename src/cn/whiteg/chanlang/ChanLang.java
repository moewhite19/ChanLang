package cn.whiteg.chanlang;

import cn.whiteg.chanlang.allNms.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class ChanLang extends JavaPlugin {
    private static final String serverVersion;
    public static Logger logger;
    public static ChanLang plugin;
    private static Map<String, String> map;
    private static Nms nms;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public CommandManage mainCommand;
    public Setting setting;

    public ChanLang() {
        plugin = this;
    }


    public static Map<String, String> getLangMap() {
        return map;
    }

    public static String getServerVersion() {
        return serverVersion;
    }

    public static Class<?> getNmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }

    public static Nms getNms() {
        return nms;
    }

    public void onLoad() {
        saveDefaultConfig();
        logger = getLogger();
        setting = new Setting();
    }

    public void onEnable() {
        logger.info("开始加载插件");
        mainCommand = new CommandManage(this);
        mainCommand.setExecutor();
        switch (serverVersion) {
            case "v1_16_R3":
                nms = new Nms_v1_16_R3(this);
                break;
            case "v1_16_R2":
                nms = new Nms_v1_16_R2(this);
                break;
            case "v1_16_R1":
                nms = new Nms_v1_16_R1(this);
                break;
            default:
                nms = new Nms_Reflect(this);
                break;
        }

        map = nms.getMap();
        onReload();
        logger.info("全部加载完成");
    }

    public void onDisable() {
        //注销所有监听器
        logger.info("插件已关闭");
    }

    public void onReload() {
        logger.info("--开始加载--");
        //储存默认语言
        File langDir = new File(getDataFolder(),"langs");
        saveDefaultLang(langDir);
        setting.reload();
        //设置语言
        for (int i = setting.lang.size() - 1; i >= 0; i--) { //从末尾往头遍历
            String lang = setting.lang.get(i);
            File langFile = new File(langDir,lang + ".json");
            if (langFile.exists()){
                try{
                    loadFile(new FileInputStream(langFile));
                    logger.info("Load lang " + lang);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            } else {
                logger.warning("Lang not fount " + langFile.getName());
            }
        }

        logger.info("--加载完成--");
    }

    public void saveDefaultLang(File langDir) {
        //储存默认语言
        if (!langDir.isDirectory()){
            langDir.mkdirs();
            try{
                List<String> urls = PluginUtil.getUrls(getClassLoader(),false);
                for (String url : urls) {
                    if (url.startsWith("langs/")){
                        try{
                            logger.info("Save Lang: " + url);
                            URL uri = getClass().getClassLoader().getResource(url);
                            if (uri == null) continue;
                            InputStream is = uri.openStream();
                            File file = new File(langDir,url.substring(url.lastIndexOf('/') + 1));
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
            }catch (IOException e){
                e.printStackTrace();
            }
            String[] langs = new String[]{"en_us","zh_cn"};
            for (String lang : langs) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFile(InputStream inputstream) {
        String nmsPack = "net.minecraft.server." + serverVersion;
        Map<String, String> map;
        Method ccm;
        Method cca;
        try{
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
