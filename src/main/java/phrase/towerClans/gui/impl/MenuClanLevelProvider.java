package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuProvider;

public class MenuClanLevelProvider extends MenuProvider {

    public MenuClanLevelProvider() {
        super(new MenuClanLevelService());
    }

    @Override
    public Inventory getMenu(ClanImpl clan, Plugin plugin) {
        return getMenuService().create(clan, plugin);
    }
}
