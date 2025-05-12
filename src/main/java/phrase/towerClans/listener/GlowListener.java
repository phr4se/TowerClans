package phrase.towerClans.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.glow.Glow;

public class GlowListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        scheduleChangeArmor();
    }
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        scheduleChangeArmor();
    }

    // todo добавить эвент выхода из клана и входа в него.

    private void scheduleChangeArmor() {
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class),()->{
            for (ModifiedPlayer player : Glow.getEnabledPlayers()) {
                Player p = player.getPlayer();
                if (p!=null) {
                    for (Player p2 : Bukkit.getOnlinePlayers()) {

                        ModifiedPlayer modifiedPlayer = ModifiedPlayer.get(p2);

                        if (Glow.isMember(modifiedPlayer,player)) {

                            Glow.broadcastChange();

                        }
                    }
                }
            }
        },1);
    }

}
