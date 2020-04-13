package cn.whiteg.chanlang.commands;

import cn.whiteg.chanlang.CommandInterface;
import cn.whiteg.chanlang.LangUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class test extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        String p = "chanlang.test";
        if (!sender.hasPermission(p)){
            sender.sendMessage("§aNo Permission§f" + p);
            return true;
        }
        sender.sendMessage("\n附魔\n");
        for (Enchantment value : Enchantment.values()) {
            sender.sendMessage(LangUtils.getEnchantmentName(value));
        }

        sender.sendMessage("\n效果\n");
        for (PotionEffectType value : PotionEffectType.values()) {
            sender.sendMessage(LangUtils.getPotionEffectName(value));
        }

//
        sender.sendMessage("\nmat物品名\n");
        for (Material value : Material.values()) {
//            ItemStack itemstack = new ItemStack(value);
//            sender.sendMessage(LangUtils.getI18NDisplayName(itemstack, "\nerrrrrrrrrrrrrrrrrrrrrrrrrrrrr"));
            sender.sendMessage(LangUtils.getMaterialName(value));
        }

        sender.sendMessage("\n实体名");
        for (EntityType type : EntityType.values()) {
            World world = Bukkit.getWorlds().get(0);
            sender.sendMessage(LangUtils.getEntityTypeName(type));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
