package cn.whiteg.chanlang.commands;

import cn.whiteg.chanlang.ChanLang;
import cn.whiteg.chanlang.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class reload extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        String p = "chanlang.reload";
        if (!sender.hasPermission(p)){
            sender.sendMessage("§aNo Permission§f" + p);
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
}
