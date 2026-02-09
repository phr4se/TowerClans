package phrase.towerclans.gui;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;

public abstract class MenuProvider {
    private final Menu menu;

    public MenuProvider(Menu menu) {
        this.menu = menu;
    }

    public abstract Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin);
    public abstract boolean menuPages();

    protected <C extends Menu> C getMenu() {
        return (C) menu;
    }
}
