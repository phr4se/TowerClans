package phrase.towerClans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;

public interface MenuService {

    Inventory create(ClanImpl clan, Plugin plugin);

}
