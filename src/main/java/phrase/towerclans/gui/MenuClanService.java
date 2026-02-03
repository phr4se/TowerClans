package phrase.towerclans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

public interface MenuClanService extends Menu {
    Inventory create(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin);
    boolean menuPages();
}
