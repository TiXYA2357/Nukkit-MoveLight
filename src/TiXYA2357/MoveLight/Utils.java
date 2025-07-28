package TiXYA2357.MoveLight;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.scheduler.*;
import lombok.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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

    protected static final List<Position> lightBlocks = new CopyOnWriteArrayList<>();


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
        for (String blockStr : getAllowBlock()) {
             if (blockStr.equals(block.getId() + "") || blockStr.equals(block.getId() + ":" + block.getDamage())) return true;
        }
        return false;
    }


    protected static UpdateBlockPacket FakeBlock(Player p, int blockId, int meta, Position pos){
        var updateBlock = new UpdateBlockPacket();
        if (IS_PM1E) updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(p.protocol, blockId, meta);
        else updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(blockId, meta);

        updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        updateBlock.x = (int) pos.x;
        updateBlock.y = (int) pos.y;
        updateBlock.z = (int) pos.z;
        return updateBlock;
    }

    protected static void restoreBlock() {
        lightBlocks.removeIf(pos -> {
            var hasp = new AtomicBoolean(false);
            pos.getLevel().getPlayers().values().forEach(p -> {
                if (p.distance(pos) < 1.3) hasp.set(true);
            });
            if (!hasp.get()) {
                var block = pos.getLevelBlock().getBlock();
                int blockId = block.getId();
                int meta = block.getDamage();
                // 发送真实方块更新数据包
                pos.getLevel().getPlayers().values().forEach(p -> p.dataPacket(FakeBlock(p, blockId, meta, pos)));
                return true;
            } else return false;
        });
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
