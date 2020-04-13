package cn.whiteg.chanlang.commands;

import cn.whiteg.chanlang.ChanLang;
import cn.whiteg.chanlang.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class clearall extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        String p = "chanlang.clear";
        if (!sender.hasPermission(p)){
            sender.sendMessage("§aNo Permission§f" + p);
            return true;
        }
        ChanLang.getLangMap().clear();
        sender.sendMessage("§bCleared§f");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
