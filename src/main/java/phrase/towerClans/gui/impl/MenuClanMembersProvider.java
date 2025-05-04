package phrase.towerClans.gui.impl;

import org.bukkit.inventory.Inventory;
import phrase.towerClans.Plugin;
import phrase.towerClans.clan.impl.ClanImpl;
import phrase.towerClans.gui.MenuPages;
import phrase.towerClans.gui.MenuProvider;

import java.util.UUID;

public class MenuClanMembersProvider extends MenuProvider {

    public MenuClanMembersProvider() {
        super(new MenuClanMembersService());
    }

    @Override
    public Inventory getMenu(ClanImpl clan, Plugin plugin) {
        return getMenuService().create(clan, plugin);
    }

    public MenuPages register(UUID player, MenuPages menuPages) {
        return MenuClanMembersService.register(player, menuPages);
    }

    public void unRegister(UUID player) {
        MenuClanMembersService.unRegister(player);
    }

    public boolean isRegistered(UUID player) {
        return MenuClanMembersService.isRegistered(player);
    }

    public MenuPages getMenuPages(UUID player) {
        return MenuClanMembersService.getMenuPages(player);
    }

}
