package phrase.towerClans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;

public abstract class MenuProvider {

    private final MenuService menuService;

    public MenuProvider(MenuService menuService) {
        this.menuService = menuService;
    }

    public abstract Inventory getMenu(ClanImpl clan, Plugin plugin);

    protected MenuService getMenuService() {
        return menuService;
    }
}
