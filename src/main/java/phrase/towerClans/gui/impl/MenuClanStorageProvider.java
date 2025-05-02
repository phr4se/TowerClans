package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuProvider;

public class MenuClanStorageProvider extends MenuProvider {

    public MenuClanStorageProvider() {
        super(new MenuClanStorageService());
    }

    @Override
    public Inventory getMenu(ClanImpl clan, Plugin plugin) {
        return getMenuService().create(clan, plugin);
    }

}
