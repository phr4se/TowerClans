package phrase.towerclans.gui.impl;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import phrase.towerclans.TowerClans;
import phrase.towerclans.clan.entity.ModifiedPlayer;
import phrase.towerclans.clan.impl.clan.ClanImpl;
import phrase.towerclans.gui.MenuClanService;
import phrase.towerclans.gui.MenuPages;
import phrase.towerclans.gui.MenuProvider;
import phrase.towerclans.gui.Pages;

import java.util.List;
import java.util.UUID;

public class MenuClanMembersProvider extends MenuProvider implements Pages {
    public MenuClanMembersProvider() {
        super(new MenuClanMembersService());
    }

    @Override
    public boolean menuPages() {
        return this.<MenuClanService>getMenu().menuPages();
    }

    @Override
    public Inventory getMenu(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin) {
        return this.<MenuClanService>getMenu().create(modifiedPlayer, clan, plugin);
    }

    @Override
    public MenuPages register(UUID player, MenuPages menuPages) {
        return MenuClanMembersService.register(player, menuPages);
    }

    @Override
    public void unRegister(UUID player) {
        MenuClanMembersService.unRegister(player);
    }

    @Override
    public boolean isRegistered(UUID player) {
        return MenuClanMembersService.isRegistered(player);
    }

    @Override
    public List<ItemStack> getContents(ModifiedPlayer modifiedPlayer, ClanImpl clan, TowerClans plugin) {
        return ((MenuClanMembersService) getMenu()).getContents(modifiedPlayer, clan, plugin);
    }

    @Override
    public MenuPages getMenuPages(UUID player) {
        return MenuClanMembersService.getMenuPages(player);
    }
}

