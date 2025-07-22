package TiXYA2357.MoveLight;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.scheduler.*;
import lombok.*;

import java.util.*;

import static TiXYA2357.MoveLight.Main.*;
import static TiXYA2357.MoveLight.Configs.*;

public class Utils {


    public static void itemToOffhand(Player p) {
        var hand = p.getInventory().getItemInHand();
        if (hand.getId() != 0) {
           replace: {
                for (String s : getLightItems().keySet()) {
                    if (checkItemByStr(s, hand)) {
                        var temp=p.getOffhandInventory().getItem(0);
                        p.getOffhandInventory().setItem(0, hand);
                        p.getInventory().setItemInHand(temp);
                        break replace;
                    }
                }
                p.sendActionBar(PT + "替换失败,这不是一个光源物品");
            }

        } else p.sendActionBar(PA + "请勿手持空气");
    }

    protected static HashMap<Player , List<String>> lightBlocks = new HashMap<>();

    public static String PosToString(Player p){
        return p.getLevelName() + ":" + (int) p.x + ":" +  (int) (p.y + 1) + ":" + (int) p.z;
    }
    public static UpdateBlockPacket FakeBlock(Player p, int blockId, Position pos){
        return FakeBlock(p, blockId, 0, pos);
    }

    public static int getPlayerLightLevel(Player p){
        var offhand = p.getOffhandInventory().getItem(0);
        var hand = p.getInventory().getItemInHand();
        var lv = 0;
        for (String s : getLightItems().keySet()) {
            if (checkItemByStr(s, offhand)) {
                lv = Math.max(0,Math.min(getLightItems().get(s),15));
                if (lv > 0) return lv;
            }
        }
        for (String s : getLightItems().keySet()) {
            if (checkItemByStr(s, hand)) {
                lv = Math.max(0,Math.min(getLightItems().get(s),15));
                if (lv > 0) return lv;
            }
        }
        return lv;
    }

    @SneakyThrows
    @SuppressWarnings("all")
    protected static boolean checkItemByStr(String itemStr, Item item){
        var numId = item.getId(); var itemDamage = item.getDamage();
        var strId = item.getNamespaceId().toString(); var ci = itemStr.
                replaceFirst("minecraft:", "").replace(" ", "");
        return numId != 255 && (ci.equals(numId+"") || ci.equals(numId + ":"
                + itemDamage)) || itemStr.contains("255") && (strId.equals("minecraft:reserved6")
                || strId.equals("minecraft:" + ci) || (strId + ":" + itemDamage).equals("minecraft:" + ci));
    }

    protected static boolean checkBlock(Block block) {
        for (String blockStr : getIgnBlocks()) {
             if (blockStr.equals(block.getId() + "") || blockStr.equals(block.getId() + ":" + block.getDamage())) return true;
        }
        return false;
    }


    protected static UpdateBlockPacket FakeBlock(Player p, int blockId, int meta, Position pos){
        var updateBlock = new UpdateBlockPacket();
        if (IS_PM1E) updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(p.protocol, blockId, meta);
        else updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(blockId, 0);

        updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        updateBlock.x = (int) pos.x;
        updateBlock.y = (int) pos.y;
        updateBlock.z = (int) pos.z;
        return updateBlock;
    }
    protected static void resSomeBlock(Player p) {
            var list = lightBlocks.getOrDefault(p, new ArrayList<>());
            new ArrayList<>(list).forEach(pos -> {
            var map=pos.split(":")[0];
            var x=pos.split(":")[1];
            var y=pos.split(":")[2];
            var z=pos.split(":")[3];
            var poss = new Position(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z), nks.getLevelByName(map));
            if (poss.distance(new Vector3(p.x, p.y + 1, p.z)) > 1.3 || getPlayerLightLevel(p) < 1) restoreBlock(poss);
        });
    }

    protected static void restoreBlock(Position pos) {
        Async(() -> {
            Block block = nks.getLevelByName(pos.getLevelName()).getBlock(pos);
            int blockId = block.getId();
            int meta = block.getDamage();
            // 发送真实方块更新数据包
            UpdateBlockPacket restore = new UpdateBlockPacket();
            nks.getOnlinePlayers().values().forEach(p -> {
                if (!p.getLevelName().equals(pos.getLevelName())) return;
                if (IS_PM1E) restore.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(p.protocol, blockId, meta);
                else restore.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(blockId, 0);
                restore.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
                restore.x = (int) pos.x;
                restore.y = (int) pos.y;
                restore.z = (int) pos.z;
                p.dataPacket(restore);});});
    }

    private static boolean IS_PM1E = true;
    public static String CORE_NAME = "";
    @SuppressWarnings("all")
    public static void checkServer(){
        Async(() -> {
            var ver = false;
            //双核心兼容
            CORE_NAME = "Nukkit";
            try {Class.forName("cn.nukkit.Nukkit").getField("NUKKIT_PM1E");
                ver = true;
                CORE_NAME = "Nukkit PM1E";
            } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
            try {var c = Class.forName("cn.nukkit.Nukkit").getField("NUKKIT");
                CORE_NAME = c.get(c).toString();
                ver = true;
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {}
            IS_PM1E = ver;
            if(ver){nks.enableExperimentMode = true;
                nks.forceResources = true;}
        });
    }


    public static boolean hasClazz(Class<?> clazz){
        try {
            Class.forName(clazz.getName());
            return true;
        } catch (ClassNotFoundException e) {
            return false;}
    }
    @Getter
    private static final SplittableRandom RANDOM = new SplittableRandom(System.currentTimeMillis());

    //方法名(() -> {方法体})
    @SuppressWarnings("all")
    public static void Repeat (Runnable logic,int time,boolean async){
        nks.getScheduler().scheduleRepeatingTask(getPlugin(), logic,time);}
    public static void Delayed (Runnable logic,int time){
        Delayed(logic,time,false);}
    @SuppressWarnings("all")
    public static void Delayed (Runnable logic,int time,boolean async){
        nks.getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {logic.run();}},time,async);}
    public static void Async (Runnable logic){
        nks.getScheduler().scheduleAsyncTask(getPlugin(), new AsyncTask() {
            @Override
            public void onRun() {logic.run();}});}

}
