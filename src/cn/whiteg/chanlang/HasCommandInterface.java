package cn.whiteg.chanlang;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class HasCommandInterface extends CommandInterface {
    public abstract boolean executo(CommandSender sender,Command cmd,String str,String[] args);

    public List<String> complete(CommandSender sender,Command cmd,String str,String[] args) {
        return PlayersList(args);
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String str,String[] args) {
        if (canUseCommand(sender)){
            return executo(sender,cmd,str,args);
        } else {
            sender.sendMessage("§cNo Permission§f");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (canUseCommand(sender)){
            return complete(sender,cmd,label,args);
        } else return null;
    }
}
