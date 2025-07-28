package TiXYA2357.MoveLight;

import cn.nukkit.utils.Config;
import lombok.*;

import static TiXYA2357.MoveLight.Main.*;

import java.util.*;

public final class Configs {
    private Configs() {}

    /**
     * 消息前缀
     */
    public static String PT;
    /**
     * 提示前缀
     */
    public static String PA;
    /**
     * 自定义指令
     */
    @Getter
    private static String cmd;

    /**
     * 方块ID: 亮度等级
     */
    @Getter
    private static HashMap<String,Integer> lightItems= new HashMap<>();
    @Getter
    private static List<String> allowBlock= new ArrayList<>();

    public static boolean initConfig(){
        PT = getConfig("服务器名称", "§r§b移动光影 §a>>> §7");
        PA = getConfig("服务器提示", "§r§b移动光影 §a丨 §7");
        cmd = getConfig("指令", "mlh");
        lightItems = getConfig("移动光源物品", new HashMap<>());
        allowBlock= getConfig("支持方块", List.of("0","8","9","10","11"));
        return true;
    }

    public static <T> T getConfig(String Name, T Default){
        var config = new Config(getConfigPath()+"/config.yml", Config.YAML);
        if (!config.exists(Name)) config.set(Name, Default);
        config.save(); config.reload(); return config.get(Name, Default);}
}
