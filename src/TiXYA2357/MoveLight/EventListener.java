package TiXYA2357.MoveLight;

import cn.nukkit.event.*;
import cn.nukkit.event.block.*;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Position;

import java.util.ArrayList;

import static TiXYA2357.MoveLight.Main.nks;
import static TiXYA2357.MoveLight.Utils.*;
import static TiXYA2357.MoveLight.Utils.lightBlocks;

public class EventListener implements Listener {
    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent e){

    }
    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent e){

    }
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        var p = e.getPlayer();
        var list = lightBlocks.getOrDefault(p, new ArrayList<>());
        Async(() ->{
            if (e.getFrom().distance(e.getTo()) > 0.1) {
                resSomeBlock(p);
                if (getPlayerLightLevel(p) > 0 && !checkBlock(nks.getLevelByName(p.getLevelName())
                        .getBlock((int) p.x, (int) (p.y + 1), (int) p.z)))
                    nks.getOnlinePlayers().values().forEach(pp -> {
                        if (pp.getLevelName().equals(p.getLevelName()))
                            pp.dataPacket(FakeBlock(p, 470, getPlayerLightLevel(p), new Position(p.x, p.y + 1, p.z)));
                    });
                list.add(PosToString(p));
                lightBlocks.put(p, list);
            }
        });

    }
}
