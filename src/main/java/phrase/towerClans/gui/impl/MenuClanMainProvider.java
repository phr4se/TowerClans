package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuProvider;

public class MenuClanMainProvider extends MenuProvider {

    public MenuClanMainProvider() {
        super(new MenuClanMainService());
    }

    @Override
    public Inventory getMenu(ClanImpl clan, Plugin plugin) {
        return getMenuService().create(clan, plugin);
    }

}
