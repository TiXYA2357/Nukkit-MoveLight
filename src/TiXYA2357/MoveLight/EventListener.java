package TiXYA2357.MoveLight;

import cn.nukkit.event.*;
import cn.nukkit.event.player.*;

import static TiXYA2357.MoveLight.Utils.*;

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
        Async(() ->{
            var pos = p.getPosition().add(0, 1, 0);
            if (e.getFrom().distance(e.getTo()) > 0.1) {
                restoreBlock();
                if (getPlayerLightLevel(p) > 0 && checkBlock(p.getLevel().getBlock(pos))) {
                    p.getLevel().getPlayers().values().forEach(pp -> {
                        if (pp.distance(p) < 33) pp.dataPacket(FakeBlock(p, 470, getPlayerLightLevel(p),pos));
                    });
                    if (!lightBlocks.contains(pos)) lightBlocks.add(pos);
                }
            }
        });

    }
}
