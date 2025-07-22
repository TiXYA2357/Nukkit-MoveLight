package TiXYA2357.MoveLight;

import TiXYA2357.MoveLight.Command.MainCommand;
import cn.nukkit.Server;
import cn.nukkit.plugin.*;
import lombok.*;

import static TiXYA2357.MoveLight.Configs.*;
import static TiXYA2357.MoveLight.Utils.checkServer;
import static TiXYA2357.MoveLight.Utils.hasClazz;

public final class Main extends PluginBase {
    @Getter
    private static String ConfigPath;
    @Getter
    static Main main;
    @Getter
    private static Plugin plugin;
    public final static Server nks = Server.getInstance();

    @Override
    public void onLoad() {
        ConfigPath = getDataFolder().getAbsolutePath();
        main = this;
        plugin = this;
        getLogger().info("Plugin loaded!");
    }
    @Override
    public void onEnable() {
        initConfig();
        checkServer();
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register(Configs.getCmd(), new MainCommand(Configs.getCmd()));
        this.getServer().getScheduler().scheduleRepeatingTask(new RepeatTask(this), 20);
//        getLogger().info("Plugin enabled!");
    }
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelAllTasks();
//        getLogger().info("Plugin disabled!");
    }
}
