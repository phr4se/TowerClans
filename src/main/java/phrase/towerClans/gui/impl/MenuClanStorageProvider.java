package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.entity.ModifiedPlayer;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuProvider;

public class MenuClanStorageProvider extends MenuProvider {

    public MenuClanStorageProvider() {
        super(new MenuClanStorageService());
    }

    @Override
    public Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, Plugin plugin) {
        return getMenuService().create(modifiedPlayer, clan, plugin);
    }

    @Override
    public boolean menuPages() {
        return getMenuService().menuPages();
    }
}
