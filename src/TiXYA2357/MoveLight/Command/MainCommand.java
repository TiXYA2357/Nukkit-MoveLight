package TiXYA2357.MoveLight.Command;

import TiXYA2357.MoveLight.Configs;
import cn.nukkit.Player;
import cn.nukkit.command.*;

import static TiXYA2357.MoveLight.Configs.*;
import static TiXYA2357.MoveLight.Utils.itemToOffhand;

public class MainCommand extends Command {
    public MainCommand(String name) {
        super(name, "§r§f移动光影主指令");
    }
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) sender.sendMessage(PT + Configs.getCmd() + """
                用法:
                reload 重载插件配置
                offhand 副手持光源方块
                """);
        else {
            switch (args[0]) {
                case "reload" -> {
                    if (sender instanceof Player p && !p.isOp()) return false;
                    if (initConfig()) sender.sendMessage(PT + "配置重载成功");
                    else sender.sendMessage(PT + "配置重载失败");
                }
                case "offhand" -> {
                    if (sender instanceof Player p) itemToOffhand(p);
                    else sender.sendMessage(PT + "此指令仅游戏内可执行");
                }
            }
        }
        return false;
    }
}
