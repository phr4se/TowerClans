package phrase.towerclans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.Plugin;
import phrase.towerclans.clan.entity.ModifiedPlayer;

public interface MenuService extends Menu {
    Inventory create(ModifiedPlayer modifiedPlayer, Plugin plugin);
    boolean menuPages();
}
