package cn.whiteg.chanlang.commands;

import cn.whiteg.chanlang.ChanLang;
import cn.whiteg.chanlang.CommandInterface;
import com.google.common.collect.ImmutableMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class clearall extends CommandInterface {
    String permission = "chanlang.clear";

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!canUseCommand(sender)){
            sender.sendMessage("§cNo Permission§f" + permission);
            return true;
        }
        ChanLang.getHandler().setMap(ImmutableMap.of());
        sender.sendMessage("§bCleared§f");
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
