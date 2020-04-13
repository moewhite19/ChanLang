# LangChan Minecraft服务器语言修改插件

可以和客户端一样修改服务器语言<br>
语言可以直接从客户端提取(插件只内置了zh_cn和en_us)<br>
修改后控制台内提示信息也都会变成制定语言<br>

对插件开发者来说<br>
插件获取物品名称直接使用BukkitAPI里的
```java
ItemStack.getI18NDisplayName()
```
即可获取到指定语言的物品名称<br>
无需再额外添加语言文件去翻译物品名称也能省下不少事
##指令以及使用权限
`chanlang reload` 重载配置和语言文件，需要权限`chanlang.reload`<br>
`chanlang clearall` 重载配置和语言文件，需要权限`chanlang.clearall`<br>
`chanlang test` 测试语言文件指令， 需要权限`chanlang.test`

##开发文档
```java
//导入工具类
import cn.whiteg.chanlang.LangUtils;

        //获取物品的名称
        LangUtils.getItemDisplayName(ItemStack item);
        //获取物品语言文件内的名称
        LangUtils.getItemDisplayName(ItemStack item);
        //获取物品类型的名称
        LangUtils.getMaterialName(Material mat);
        //获取实体名称
        LangUtils.getEntityName(Entity entity);
        //获取实体类型的名称
        LangUtils.getEntityTypeName(EntityType tyoe);
        //获取附魔属性名称
        LangUtils.getEnchantmentName(Enchantment en);
        //获取附魔等级名称
        LangUtils.getEnchantmentLvlName(int lvl);
        //获取药水名称
        LangUtils.getPotionEffectName(PotionType type);
        //获取药水等级名称
        LangUtils.getPotionEffectLvlName(int lvl)
        
        //使用key直接从语言文件里获取值
        LangUtils.getMessage(String key);
       
       
  
}
```



