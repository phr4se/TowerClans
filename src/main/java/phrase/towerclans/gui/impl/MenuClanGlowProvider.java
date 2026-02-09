package phrase.towerclans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.gui.MenuProvider;

public class MenuClanGlowProvider extends MenuProvider {
    public MenuClanGlowProvider() {
        super(new MenuClanGlowService());
    }

    @Override
    public Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin) {
        return this.<MenuClanService>getMenu().create(modifiedPlayer, clan, plugin);
    }

    @Override
    public boolean menuPages() {
        return this.<MenuClanService>getMenu().menuPages();
    }
}
