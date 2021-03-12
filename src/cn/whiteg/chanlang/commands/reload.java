package cn.whiteg.chanlang.commands;

import cn.whiteg.chanlang.ChanLang;
import cn.whiteg.chanlang.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class reload extends HasCommandInterface {
    String permission = "chanlang.reload";

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission(permission)){
            sender.sendMessage("§aNo Permission§f" + permission);
            return true;
        }
        ChanLang.plugin.onReload();
        sender.sendMessage("§bReloaded");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission(permission);
    }
}
