package phrase.towerclans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.gui.MenuProvider;

public class MenuClanStorageProvider extends MenuProvider {
    public MenuClanStorageProvider() {
        super(new MenuClanStorageService());
    }

    @Override
    public Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin) {
        return this.<MenuClanService>getMenu().create(modifiedPlayer, clan, plugin);
    }

    @Override
    public boolean menuPages() {
        return this.<MenuClanService>getMenu().menuPages();
    }

    public MenuClanStorageService getMenuClanStorageService() {
        return (MenuClanStorageService) this.<MenuClanService>getMenu();
    }
}
