package phrase.towerClans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.utils.colorizer.ColorizerProvider;

public interface MenuService {

    ColorizerProvider colorizerProvider = Plugin.getColorizerProvider();

    Inventory create(ClanImpl clan, Plugin plugin);

}
