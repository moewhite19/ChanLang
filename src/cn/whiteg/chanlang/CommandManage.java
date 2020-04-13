package cn.whiteg.chanlang;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManage extends CommandInterface {
    final public List<String> allCmd = Arrays.asList("reload","clearall", "test");
    final public Map<String, CommandInterface> commandMap = new HashMap<>(allCmd.size());
    final public SubCommand subCommand = new SubCommand(this);

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
        if (commandMap.containsKey(args[0])){
            return commandMap.get(args[0]).onCommand(sender,cmd,label,args);
        } else {
            sender.sendMessage("无效指令");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 1){
            List ls = null;
            if (commandMap.containsKey(args[0])) ls = commandMap.get(args[0]).onTabComplete(sender,cmd,label,args);
            if (ls != null){
                return getMatches(args[args.length - 1],ls);
            }
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (args.length == 1){
            return getMatches(args[0],allCmd);
        }
        return null;
    }

    public void regCommand(String var1,CommandInterface cmd) {
        commandMap.put(var1,cmd);
    }

    public static class SubCommand extends CommandInterface {
        private final CommandManage commandManage;

        public SubCommand(CommandManage commandManage) {
            this.commandManage = commandManage;
        }

        @Override
        public boolean onCommand(CommandSender commandSender,Command command,String s,String[] strings) {
            CommandInterface ci = commandManage.commandMap.get(command.getName());
            if (ci == null) return false;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            ci.onCommand(commandSender,command,s,args);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
            CommandInterface ci = commandManage.commandMap.get(command.getName());
            if (ci == null) return null;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            return ci.onTabComplete(commandSender,command,s,args);
        }
    }
}
