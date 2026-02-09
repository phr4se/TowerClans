package phrase.towerclans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;

public interface MenuService extends Menu {
    Inventory create(ModifiedPlayer modifiedPlayer, TowerClans plugin);
    boolean menuPages();
}
