package phrase.towerclans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.event.*;

public class ClanListener implements Listener {
    private final TowerClans plugin;

    public ClanListener(TowerClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClanPvp(ClanPvpEvent event) {
        ClanImpl clan = (ClanImpl) event.getClan();
        if (clan.isPvp()) event.setCancelled(true);
    }
}
