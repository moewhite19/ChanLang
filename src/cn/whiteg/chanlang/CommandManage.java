package cn.whiteg.chanlang;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManage extends CommandInterface {
    final public List<String> allCmd = Arrays.asList("reload","clearall","test");
    final public Map<String, CommandInterface> commandMap = new HashMap<>(allCmd.size());

    public CommandManage() {
        for (String cmd : allCmd) {
            try{
                Class c = Class.forName("cn.whiteg.chanlang.commands." + cmd);
                CommandInterface ci = (CommandInterface) c.newInstance();
                regCommand(cmd,ci);
                PluginCommand pc = ChanLang.plugin.getCommand(cmd);
                if (pc != null){
                    pc.setExecutor(ci);
                    pc.setTabCompleter(ci);
                }
            }catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
        }

    }

    public static List<String> getMatches(String[] args,List<String> list) {
        return getMatches(args[args.length - 1],list);
    }

    public static List<String> getMatches(List<String> list,String[] args) {
        return getMatches(args[args.length - 1],list);
    }

    public static List<String> getMatches(String value,List<String> list) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).intern().toLowerCase();
            if (str.startsWith(value.toLowerCase())){
                result.add(list.get(i));
            }
        }
        return result;
    }

    public static List<String> getMatches(List<String> list,String value) {
        return getMatches(list,value);
    }

    public static List<String> PlayersList(String arg) {
        List<String> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) players.add(p.getName());
        return getMatches(arg,players);
    }

    public static List<String> PlayersList(String[] arg) {
        return PlayersList(arg[arg.length - 1]);
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            sender.sendMessage("§2[§b" + ChanLang.plugin.getName() + "§2]");
            return true;
        }

        CommandInterface subCommand = commandMap.get(args[0]);
        if (subCommand != null){
            if (args.length > 1){
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args,1,subArgs,0,subArgs.length);
                return subCommand.onCommand(sender,cmd,label,subArgs);
            } else {
                return subCommand.onCommand(sender,cmd,label,new String[]{});
            }
        } else {
            sender.sendMessage("无效指令");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return getMatches(args[0].toLowerCase(),allCmd);
        } else if (args.length > 1){
            CommandInterface subCommand = commandMap.get(args[0]);
            if (subCommand != null){
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args,1,subArgs,0,subArgs.length);
                return subCommand.onTabComplete(sender,cmd,label,subArgs);
            }
        }
        return null;
    }

    public void regCommand(String var1,CommandInterface cmd) {
        commandMap.put(var1,cmd);
    }
}
