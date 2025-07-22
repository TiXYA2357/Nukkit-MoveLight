package TiXYA2357.MoveLight;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.PluginTask;

import java.util.ArrayList;
import java.util.HashMap;

import static TiXYA2357.MoveLight.Main.nks;
import static TiXYA2357.MoveLight.Utils.*;

public class RepeatTask extends PluginTask<Main> {
    public RepeatTask(Main owner) {
        super(owner);
    }
    @Override
    public void onRun(int i) {
    nks.getOnlinePlayers().values().forEach(Utils::resSomeBlock);
        new HashMap<>(lightBlocks).forEach((p,pos) -> {//如果玩家离线，则还原所有方块
        if (p == null) { pos.forEach(pos2 -> restoreBlock(new Position(
                            Integer.parseInt(pos2.split(":")[1]),
                            Integer.parseInt(pos2.split(":")[2]),
                            Integer.parseInt(pos2.split(":")[3]),
                            nks.getLevelByName(pos2.split(":")[0]))));
            lightBlocks.remove(p);
        }
    });
    }

}
